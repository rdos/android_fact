package ru.smartro.worknote.ui.camera

import android.Manifest
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
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.simulateClick
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
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
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import android.animation.Animator
import android.annotation.SuppressLint
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.android.synthetic.main.fragment_camera.*
import ru.smartro.worknote.base.AbstractFragment


/** Helper type alias used for analysis use case callbacks */
typealias LumaListener = (luma: Double) -> Unit


class CameraFragment(
    private val photoFor: Int,
    private val platformId: Int,
    private val containerId: Int
) : AbstractFragment(),  ImageCounter {

    private val KEY_EVENT_ACTION = "key_event_action"
    private val KEY_EVENT_EXTRA = "key_event_extra"
    private val ANIMATION_FAST_MILLIS = 50L
    private val ANIMATION_SLOW_MILLIS = 100L
    private lateinit var mRootView: View
    private lateinit var mPreviewView: PreviewView
    private lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager

    private val PERMISSIONS_REQUEST_CODE = 10
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var mCamera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val viewModel: CameraViewModel by viewModel()

    private lateinit var cameraExecutor: ExecutorService

    private var rotation = Surface.ROTATION_0
    private var rotationDegrees = 0F

    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val shutter = mRootView
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    private fun setGalleryThumbnail(uri: Uri) {
        val thumbnail = mRootView.findViewById<ImageButton>(R.id.photo_view_button)
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
        val imageCounter = mRootView.findViewById<TextView>(R.id.image_counter)
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

//    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
        mPreviewView = mRootView.findViewById(R.id.view_finder)
        cameraExecutor = Executors.newSingleThreadExecutor()
        broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)
        outputDirectory = CameraActivity.getOutputDirectory(requireContext())
        mPreviewView.post{
            displayId = mPreviewView.display.displayId
            setUpCamera()
            updateCameraUi()
        }
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
                imageAnalyzer?.targetRotation = rotation
            }
        }

        orientationEventListener.enable()
        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
//            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(FLASH_MODE_OFF)
            .build()

        //todo:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        imageCapture!!.flashMode = FLASH_MODE_ON


        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                    // Values returned from our analyzer are passed to the attached listener
                    // We log image analysis results here - you should do something useful
                    // instead!
                    Log.d("LuminosityAnalyzer", "Average luminosity: $luma")
                })
            }

//        imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(screenAspectRatio)
//            .setTargetRotation(rotation)
//            .setImageQueueDepth(100)
//            .build()
        cameraProvider.unbindAll()

//        val imageAnalyzerConfig = ImageAnalysisConfig.Builder()

        try {
            mCamera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )
            preview?.setSurfaceProvider(mPreviewView.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }

//        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager?
//        val camera = activity.getCameraInstance();
//        val params: Camera.Parameters = mCamera.getParameters()

//        val manager = ContextCompat.getSystemService(requireContext(), CameraManagerCompat::class.java)
//        for (cameraId in manager?.cameraIdList!!) {
//            val chars = manager.getCameraCharacteristics(cameraId)
//            val range = mCamera.CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
//            Log.e("CameraCharacteristics", "Camera $cameraId range: ${range.toString()}")
//        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    private fun animateFocusRing(x: Float, y: Float) {
        val focusRing: AppCompatImageView = mRootView.findViewById(R.id.focusRing)

        // Move the focus ring so that its center is at the tap location (x, y)
        focusRing.x = x - focusRing.width / 2
        focusRing.y = y - focusRing.height / 2

        // Show focus ring
        focusRing.animate()?.cancel()
        focusRing.alpha = 1f
        // Animate the focus ring to disappear
        focusRing.animate()
            ?.setDuration(ANIMATION_MANUAL_FOCUS_DURATION__MS)
            ?.alpha(0f)
            ?.setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    focusRing?.setVisibility(View.VISIBLE)
                    Log.d(TAG, "onAnimationStart")

                }

                override fun onAnimationEnd(animator: Animator?) {
                    focusRing.setVisibility(View.INVISIBLE)
                    Log.d(TAG, "onAnimationEnd")

                } // The rest of AnimatorListener's methods.

                override fun onAnimationCancel(animation: Animator?) {
//                    TODO("Not yet implemented")
                    focusRing.setVisibility(View.INVISIBLE)
                    Log.d(TAG, "onAnimationCancel")

                }

                override fun onAnimationRepeat(animation: Animator?) {
//                    TODO("Not yet implemented")
                    Log.d(TAG, "onAnimationRepeat")
                }
            })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateCameraUi() {
        Log.d(TAG, "updateCameraUi")

        mRootView.setOnTouchListener { _, event ->
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                        mPreviewView.width.toFloat(), mPreviewView.height.toFloat()
                    )
                    val autoFocusPoint = factory.createPoint(event.x, event.y)
                    try {
                        mCamera?.cameraControl?.startFocusAndMetering(
                            FocusMeteringAction.Builder(
                                autoFocusPoint,
                                FocusMeteringAction.FLAG_AF
                            ).apply {
                                //focus only when the user tap the preview
                                setAutoCancelDuration(MANUAL_FOCUS_DURATION__MS, TimeUnit.MILLISECONDS)
                            }.build()
                        )
                        animateFocusRing(event.x, event.y)
                    } catch (error: CameraInfoUnavailableException) {
                        Log.d(TAG, "cannot access camera", error)
                    }
                    true
                }
                else -> false
            }
        }

        //кнопка отправить в камере. Определения для чего делается фото
        mRootView.findViewById<ImageButton>(R.id.photo_accept_button).setOnClickListener {
            when (photoFor) {
                PhotoTypeEnum.forBeforeMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    if (platform.beforeMedia.size == 0) {
                        toast("Сделайте фото")
                    } else {
                        requireActivity().finish()
                    }
                }
                PhotoTypeEnum.forAfterMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    if (platform.afterMedia.size == 0) {
                        toast("Сделайте фото")
                    } else {
                        requireActivity().setResult(Activity.RESULT_OK)
                        requireActivity().finish()
                    }
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    if (platform.failureMedia.size == 0) {
                        toast("Сделайте фото")
                    } else {
                        requireActivity().setResult(Activity.RESULT_OK)
                        requireActivity().finish()
                    }
                }
                PhotoTypeEnum.forContainerProblem -> {
                    val container = viewModel.findContainerEntity(containerId)
                    if (container.failureMedia.size == 0) {
                        toast("Сделайте фото")
                    } else {
                        requireActivity().setResult(Activity.RESULT_OK)
                        requireActivity().finish()
                    }
                }

                PhotoTypeEnum.forKGO -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    if (platform.kgoMedia.size == 0) {
                        toast("Сделайте фото")
                    } else {
                        requireActivity().setResult(101)
                        requireActivity().finish()
                    }
                }
            }
        }
        val captureButton = mRootView.findViewById<ImageButton>(R.id.camera_capture_button)
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
                    PhotoTypeEnum.forPlatformProblem -> {
                        val platform = viewModel.findPlatformEntity(platformId)
                        platform.failureMedia.size >= maxPhotoCount
                    }
                    PhotoTypeEnum.forContainerProblem -> {
                        val container = viewModel.findContainerEntity(containerId)
                        container.failureMedia.size >= maxPhotoCount
                    }
                    PhotoTypeEnum.forKGO -> {
                        val platform = viewModel.findPlatformEntity(platformId)
                        platform.kgoMedia.size >= maxPhotoCount
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

                    imageCapture.takePicture(outputOptions, cameraExecutor, object : OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: OutputFileResults) {
                            val job = CoroutineScope(Dispatchers.Main)
                            val imageUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(TAG, "Photo capture succeeded: $imageUri path: ${imageUri.path}")
                            Log.d(TAG, "Current thread: ${Thread.currentThread()}")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setGalleryThumbnail(imageUri)
                                setImageCounter(true)
                            }

                            job.launch {
                                val imageBase64 = MyUtil.imageToBase64(imageUri, rotationDegrees, requireContext())
                                if (photoFor == PhotoTypeEnum.forContainerProblem) {
                                    viewModel.updateContainerMedia(platformId, containerId, imageBase64, AppPreferences.getCurrentLocation())
                                } else {
                                    viewModel.updatePlatformMedia(photoFor, platformId, imageBase64, AppPreferences.getCurrentLocation())
                                }
                                captureButton.isClickable = true
                                captureButton.isEnabled = true
                                File(imageUri.path!!).delete()
                                job.cancel()
                            }
                        }
                    })
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mRootView.postDelayed({
                            mRootView.foreground = ColorDrawable(Color.WHITE)
                            mRootView.postDelayed({ mRootView.foreground = null }, ANIMATION_FAST_MILLIS)
                        }, ANIMATION_SLOW_MILLIS)
                    }
                }
            }
        }

        val tbPhotoFlash = mRootView.findViewById<AppCompatToggleButton>(R.id.photo_flash)
        tbPhotoFlash.setOnClickListener{
            Log.d(TAG, "tbPhotoflash.before")
            if (mCamera?.cameraInfo?.hasFlashUnit() == true) {
                mCamera?.cameraControl?.enableTorch(tbPhotoFlash.isChecked)
            } else {
                Log.e(TAG, "updateCameraUi mCamera?.cameraInfo?.hasFlashUnit() == true")
            }
        }
        // Listener for button used to view the most recent photo
        mRootView.findViewById<ImageButton>(R.id.photo_view_button).setOnClickListener {
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



    /**
     * Our custom image analysis class.
     *
     * <p>All we need to do is override the function `analyze` with our desired operations. Here,
     * we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
     */
    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        /**
         * Used to add listeners that will be called with each luma computed
         */
        fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)

        /**
         * Helper extension function used to extract a byte array from an image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        /**
         * Analyzes an image to produce a result.
         *
         * <p>The caller is responsible for ensuring this analysis method can be executed quickly
         * enough to prevent stalls in the image acquisition pipeline. Otherwise, newly available
         * images will not be acquired and analyzed.
         *
         * <p>The image passed to this method becomes invalid after this method returns. The caller
         * should not store external references to this image, as these references will become
         * invalid.
         *
         * @param image image being analyzed VERY IMPORTANT: Analyzer method implementation must
         * call image.close() on received images when finished using them. Otherwise, new images
         * may not be received or the camera may stall, depending on back pressure setting.
         *
         */
        override fun analyze(image: ImageProxy) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) {
                image.close()
                return
            }

            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0

            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases

            lastAnalyzedTimestamp = frameTimestamps.first

            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val buffer = image.planes[0].buffer

            // Extract image data from callback object
            val data = buffer.toByteArray()

            // Convert the data into an array of pixel values ranging 0-255
            val pixels = data.map { it.toInt() and 0xFF }

            // Compute average luminance for the image
            val luma = pixels.average()

            // Call all listeners with new value
            listeners.forEach { it(luma) }

            image.close()
        }
    }


    companion object {
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val MANUAL_FOCUS_DURATION__MS = 8000L
        private const val ANIMATION_MANUAL_FOCUS_DURATION__MS = MANUAL_FOCUS_DURATION__MS / 2
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
    }

    override fun mediaSizeChanged() {
        setImageCounter(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
    }


//    private class LuminosityAnalyzer:ImageAnalysis.Analyzer{
//        private var lastAnalyzedTimestamp = 0L
//        /**
//         * Helper extension function used to extract a byte array from an
//         * image plane buffer
//         */
//        private fun ByteBuffer.toByteArray():ByteArray{
//            rewind() //Rewind buffer to zero
//            val data=ByteArray(remaining())
//            get(data)  // Copy buffer into byte array
//            return data // Return byte array
//        }
//
//        override fun analyze(image: ImageProxy) {
//            val currentTimestamp =System.currentTimeMillis()
//            // Calculate the average luma no more often than every second
//            if(currentTimestamp-lastAnalyzedTimestamp>=java.util.concurrent.TimeUnit.SECONDS.toMillis(1)){
//                // Since format in ImageAnalysis is YUV, image.planes[0]
//                // contains the Y (luminance) plane
//                val buffer = image.planes[0].buffer
//                // Extract image data from callback object
//                val data = buffer.toByteArray()
//                // Convert the data into an array of pixel values
//                val pixels = data.map { it.toInt() and 0xFF }
//                // Compute average luminance for the image
//                val luma = pixels.average()
//                // Log the new luma value
//                Log.d( "CameraX Demo" , "Average luminosity: $luma")
//                // Update timestamp of last analyzed frame
//                lastAnalyzedTimestamp = currentTimestamp
//            }
//        }
//
//
//    }
}


interface ImageCounter {
    fun mediaSizeChanged()
}


