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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.BaseFragment
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.simulateClick
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.ui.platform_service.ServiceActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SlideCameraFragment(private val photoFor: Int, private val platformId: Int, private val containerId: Int) : BaseFragment(), ImageCounter {
    private val KEY_EVENT_ACTION = "key_event_action"
    private val KEY_EVENT_EXTRA = "key_event_extra"
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

    private lateinit var cameraExecutor: ExecutorService

    private var rotation = Surface.ROTATION_0
    private var rotationDegrees = 0F

    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val shutter = hostLayout
                        .findViewById<ImageButton>(R.id.camera_capture_button)
                    shutter.simulateClick()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!MyUtil.hasPermissions(requireContext())) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(context, "Разрешение принято", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Разрешение отклонёно", Toast.LENGTH_LONG).show()
                requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        broadcastManager.unregisterReceiver(volumeDownReceiver)
        //  displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_camera, container, false)

    private fun setGalleryThumbnail(uri: Uri) {
        val thumbnail = hostLayout.findViewById<ImageButton>(R.id.photo_view_button)
        thumbnail.post {
            // Remove thumbnail padding
            thumbnail.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
            Glide.with(thumbnail)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(thumbnail)
        }

    }

    private fun setImageCounter(plus: Boolean) {
        val imageCounter = hostLayout.findViewById<TextView>(R.id.image_counter)
        val count = if (plus) 1 else 0
        imageCounter.post {
            when (photoFor) {
                PhotoTypeEnum.forContainerProblem -> {
                    val container = viewModel.findContainerEntity(containerId)
                    imageCounter.text = "${container.failureMedia.size + count}"
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    imageCounter.text = "${platform.failureMedia.size + count}"
                }
                PhotoTypeEnum.forAfterMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    imageCounter.text = "${platform.afterMedia.size + count}"
                }
                PhotoTypeEnum.forBeforeMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    imageCounter.text = "${platform.beforeMedia.size + count}"
                }
                PhotoTypeEnum.forKGO -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    imageCounter.text = "${platform.kgoMedia.size + count}"
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCamera()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateCameraUi()
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            lensFacing = when {
                hasFrontCamera() -> CameraSelector.LENS_FACING_BACK
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val screenAspectRatio = aspectRatio(320, 620)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        val orientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                when (orientation) {
                    in 45..134 -> {
                        rotation = Surface.ROTATION_270
                        rotationDegrees = 270F
                    }
                    in 135..224 -> {
                        rotation = Surface.ROTATION_180
                        rotationDegrees = 180F
                    }
                    in 225..314 -> {
                        rotation = Surface.ROTATION_90
                        rotationDegrees = 90F
                    }
                    else -> {
                        rotation = Surface.ROTATION_0
                        rotationDegrees = 0F
                    }
                }
                imageCapture?.targetRotation = rotation
            }
        }

        orientationEventListener.enable()
        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
        cameraProvider.unbindAll()
        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(FLASH_MODE_OFF)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun updateCameraUi() {
        hostLayout.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            hostLayout.removeView(it)
        }

        val controls = View.inflate(requireContext(), R.layout.camera_ui_container, hostLayout)

        //кнопка отправить в камере. Определения для чего делается фото
        controls.findViewById<ImageButton>(R.id.photo_accept_button).setOnClickListener {
            val serviceActivity = (requireActivity() as ServiceActivity)
            when (photoFor) {
                PhotoTypeEnum.forBeforeMedia -> {
                    serviceActivity.nextPage()
                }
                PhotoTypeEnum.forAfterMedia -> {
                    viewModel.updatePlatformStatus(platformId, StatusEnum.SUCCESS)
                    serviceActivity.setResult(Activity.RESULT_OK)
                    serviceActivity.finish()
                }
            }
        }
        val captureButton = controls.findViewById<ImageButton>(R.id.camera_capture_button)
        captureButton.setOnClickListener {
            imageCapture?.let { imageCapture ->
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
                val metadata = Metadata().apply {
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata).build()

                val maxPhotoCount = 3
                val flashMode = if (photo_flash.isChecked) FLASH_MODE_ON else FLASH_MODE_OFF
                imageCapture.flashMode = flashMode
                val currentMediaIsFull = when (photoFor) {
                    PhotoTypeEnum.forAfterMedia -> {
                        val platform = viewModel.findPlatformEntity(platformId)
                        platform.afterMedia.size >= maxPhotoCount
                    }
                    PhotoTypeEnum.forBeforeMedia -> {
                        val platform = viewModel.findPlatformEntity(platformId)
                        platform.beforeMedia.size >= maxPhotoCount
                    }
                    else -> {
                        false
                    }
                }
                if (currentMediaIsFull) {
                    toast("Разрешенное количество фотографий:3")
                    loadingHide()
                } else {
                    captureButton.isClickable = false
                    captureButton.isEnabled = false
                    captureButton.isPressed = true

                    Log.d(TAG, "${rotation}")

                    imageCapture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, ": ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val imageUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(TAG, "Photo capture succeeded: $imageUri path: ${imageUri.path}")
                            Log.d(TAG, "Current thread: ${Thread.currentThread()}")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setGalleryThumbnail(imageUri)
                                setImageCounter(true)
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                val imageBase64 = MyUtil.imageToBase64(imageUri, rotationDegrees, requireContext())

                                if (photoFor == PhotoTypeEnum.forContainerProblem) {
                                    viewModel.updateContainerMedia(platformId, containerId, imageBase64)
                                } else {
                                    viewModel.updatePlatformMedia(photoFor, platformId, imageBase64)
                                }
                                captureButton.isClickable = true
                                captureButton.isEnabled = true
                            }

                        }
                    })
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        hostLayout.postDelayed({
                            hostLayout.foreground = ColorDrawable(Color.WHITE)
                            hostLayout.postDelayed({ hostLayout.foreground = null }, ANIMATION_FAST_MILLIS)
                        }, ANIMATION_SLOW_MILLIS)
                    }
                }
            }
        }

        // Listener for button used to view the most recent photo
        controls.findViewById<ImageButton>(R.id.photo_view_button).setOnClickListener {
            val fragment = GalleryFragment(
                platformId = platformId, photoFor = photoFor,
                containerId = containerId, imageCountListener = this
            )
            fragment.show(childFragmentManager, "GalleryFragment")
        }

        setImageCounter(false)
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun initCamera() {
        hostLayout = view as ConstraintLayout
        viewFinder = hostLayout.findViewById(R.id.view_finder)
        cameraExecutor = Executors.newSingleThreadExecutor()
        broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)
        outputDirectory = CameraActivity.getOutputDirectory(requireContext())
        viewFinder.post {
            displayId = viewFinder.display.displayId
            updateCameraUi()
            setUpCamera()
        }
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

    override fun mediaSizeChanged() {
        setImageCounter(false)
    }

    override fun onPauseFragment() {
        super.onPauseFragment()
        cameraProvider?.unbindAll()
    }

    override fun onResumeFragment() {
        super.onResumeFragment()
        initCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
    }
}

