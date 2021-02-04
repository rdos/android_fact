/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.smartro.worknote.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.simulateClick
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.db.entity.container_service.PhotoAfterEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoBeforeEntity
import ru.smartro.worknote.service.response.way_task.WayPoint
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class CameraFragment(private val photoFor: Int, private val wayPoint: WayPoint) : Fragment() {
    private val KEY_EVENT_ACTION = "key_event_action"
    private val KEY_EVENT_EXTRA = "key_event_extra"
    private val IMMERSIVE_FLAG_TIMEOUT = 500L
    private val ANIMATION_FAST_MILLIS = 50L
    private val ANIMATION_SLOW_MILLIS = 100L
    private lateinit var hostLayout: ConstraintLayout
    private lateinit var viewFinder: PreviewView
    private lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager

    private val PERMISSIONS_REQUEST_CODE = 10
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val viewModel: CameraViewModel by viewModel()

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private lateinit var cameraExecutor: ExecutorService

    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                // When the volume down button is pressed, simulate a shutter button click
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val shutter = hostLayout
                        .findViewById<ImageButton>(R.id.camera_capture_button)
                    shutter.simulateClick()
                }
            }
        }
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.displayId) {
                Log.d(TAG, "Rotation changed: ${view.display.rotation}")
                imageCapture?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!MyUtil.hasPermissions(requireContext())) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                // Take the user to the success fragment when permission is granted
                Toast.makeText(context, "Разрешение принято", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Разрешение отклонёно", Toast.LENGTH_LONG).show()
                requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Shut down our background executor
        cameraExecutor.shutdown()
        // Unregister the broadcast receivers and listeners
        broadcastManager.unregisterReceiver(volumeDownReceiver)
        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_camera, container, false)

    private fun setGalleryThumbnail(uri: Uri) {
        // Reference of the view that holds the gallery thumbnail
        val thumbnail = hostLayout.findViewById<ImageButton>(R.id.photo_view_button)
        // Run the operations in the view's thread
        thumbnail.post {
            // Remove thumbnail padding
            thumbnail.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
            // Load thumbnail into circular button using Glide
            Glide.with(thumbnail)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(thumbnail)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostLayout = view as ConstraintLayout
        viewFinder = hostLayout.findViewById(R.id.view_finder)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        broadcastManager = LocalBroadcastManager.getInstance(view.context)
        // Set up the intent filter that will receive events from our main activity
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)
        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null)
        outputDirectory = CameraActivity.getOutputDirectory(requireContext())
        viewFinder.post {
            displayId = viewFinder.display.displayId
            // Build UI controls
            updateCameraUi()
            setUpCamera()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Redraw the camera UI controls
        updateCameraUi()
        // Enable or disable switching between cameras
    }

    /** Initialize CameraX, and prepare to bind the camera use cases  */
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            // CameraProvider
            cameraProvider = cameraProviderFuture.get()
            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasFrontCamera() -> CameraSelector.LENS_FACING_BACK
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }
            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()
        // The analyzer can then be assigned to the instance
        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    /**
     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /** Method used to re-draw the camera UI controls, called every time configuration changes. */
    private fun updateCameraUi() {
        hostLayout.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            hostLayout.removeView(it)
        }

        val controls = View.inflate(requireContext(), R.layout.camera_ui_container, hostLayout)

        //кнопка отправить в камере. Определения для чего делается фото
        controls.findViewById<ImageButton>(R.id.photo_accept_button).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                when (photoFor) {
                    PhotoTypeEnum.forBeforeMedia -> {
                        if (viewModel.findBeforePhotosByIdNoLv(wayPoint.id).isNullOrEmpty()) {
                            withContext(Dispatchers.Main) {
                                toast("Сделайте фото")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                requireActivity().finish()
                            }
                        }
                    }
                    PhotoTypeEnum.forAfterMedia -> {
                        if (viewModel.findAfterPhotosByIdNoLv(wayPoint.id).isNullOrEmpty()) {
                            withContext(Dispatchers.Main) {
                                toast("Сделайте фото")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                requireActivity().setResult(Activity.RESULT_OK)
                                requireActivity().finish()
                            }
                        }
                    }
                    PhotoTypeEnum.forProblemMedia -> {
                        if (viewModel.findProblemPhotosByIdNoLv(wayPoint.id).isNullOrEmpty()) {
                            withContext(Dispatchers.Main) {
                                toast("Сделайте фото")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                requireActivity().finish()
                            }
                        }
                    }
                }
            }
        }
        controls.findViewById<ImageButton>(R.id.camera_capture_button).setOnClickListener {
            imageCapture?.let { imageCapture ->
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
                val metadata = Metadata().apply {
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata)
                    .build()

                imageCapture.takePicture(
                    outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(TAG, "Photo capture succeeded: $savedUri path: ${savedUri.path}")

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setGalleryThumbnail(savedUri)
                            }

                            when (photoFor) {
                                PhotoTypeEnum.forAfterMedia -> {
                                    val afterPhoto = PhotoAfterEntity(id = 0, photoPath = photoFile.absolutePath, pointID = wayPoint.id)
                                    viewModel.insertAfterPhoto(afterPhoto)
                                }
                                PhotoTypeEnum.forBeforeMedia -> {
                                    val beforePhoto = PhotoBeforeEntity(id = 0, photoPath = photoFile.absolutePath, pointID = wayPoint.id)
                                    viewModel.insertBeforePhoto(beforePhoto)
                                }
                                PhotoTypeEnum.forProblemMedia -> {

                                }
                            }
                        }
                    })

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hostLayout.postDelayed({
                        hostLayout.foreground = ColorDrawable(Color.WHITE)
                        hostLayout.postDelayed(
                            { hostLayout.foreground = null }, ANIMATION_FAST_MILLIS
                        )
                    }, ANIMATION_SLOW_MILLIS)
                }
            }
        }

        // Listener for button used to view the most recent photo
        controls.findViewById<ImageButton>(R.id.photo_view_button).setOnClickListener {
            val fragment: GalleryFragment
            when (photoFor) {
                PhotoTypeEnum.forBeforeMedia -> {
                    fragment = GalleryFragment(containerId = wayPoint.id, photoFor = PhotoTypeEnum.forBeforeMedia)
                    fragment.show(childFragmentManager, "GalleryFragment")
                }
                PhotoTypeEnum.forAfterMedia -> {
                    fragment = GalleryFragment(containerId = wayPoint.id, photoFor = PhotoTypeEnum.forAfterMedia)
                    fragment.show(childFragmentManager, "GalleryFragment")
                }
                PhotoTypeEnum.forProblemMedia -> {
                    fragment = GalleryFragment(containerId = wayPoint.id, photoFor = PhotoTypeEnum.forProblemMedia)
                    fragment.show(childFragmentManager, "GalleryFragment")
                }
            }
        }
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
    }
}
