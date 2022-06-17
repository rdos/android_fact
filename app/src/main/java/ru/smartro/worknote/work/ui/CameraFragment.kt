package ru.smartro.worknote.work.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ac.map.AFragment
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraFragment(
    private val photoFor: Int,
    private val platformId: Int,
    private val containerId: Int
) : AFragment(), ImageCounter {

    private var mThumbNail: ImageButton? = null
    private var mImageCounter: TextView? = null
    private val maxPhotoCount = 3

    private var mActbPhotoFlash: AppCompatToggleButton? = null
    private lateinit var mRootView: View
    private var mBtnAcceptPhoto: Button? = null
    private lateinit var mPreviewView: PreviewView
    private lateinit var outputDirectory: File

    private val PERMISSIONS_REQUEST_CODE = 10
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

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

    override fun onResume() {
        super.onResume()
        enableTorch()
    }

    //    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
        mRootView.findViewById<Button>(R.id.btn_cancel).visibility = View.GONE
        mPreviewView = mRootView.findViewById(R.id.view_finder)
        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = CameraAct.getOutputDirectory(requireContext())
        mPreviewView.post{
            setUpCamera()
            updateCameraUi()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(context, "Разрешение принято", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Разрешение отклонёно", Toast.LENGTH_LONG).show()
//                requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        //  displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(requireContext())) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun setGalleryThumbnail(uri: Uri) {
        try {
            mThumbNail?.let {
                it.post {
                    // Remove thumbnail padding
                    Glide.with(App.getAppliCation())
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mThumbNail!!)
                }
            }
        } catch (ex: Exception) {
            logSentry("setGalleryThumbnail и try{}catch")
            Log.e(TAG, "setGalleryThumbnail и try{}catch", ex)
        }
    }

    private fun setImageCounter(plus: Boolean) {
        val count = if (plus) 1 else 0
        var mediaSize = 0
        mImageCounter?.post{
            when (photoFor) {
                PhotoTypeEnum.forContainerFailure -> {
                    val container = viewModel.findContainerEntity(containerId)
                    mediaSize = container.failureMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forContainerBreakdown -> {
                    val container = viewModel.findContainerEntity(containerId)
                    mediaSize = container.breakdownMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    mediaSize = platform.failureMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forAfterMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    mediaSize = platform.afterMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forBeforeMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    mediaSize = platform.beforeMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forServedKGO -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    mediaSize = platform.getServedKGOMediaSize() + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forRemainingKGO -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    mediaSize = platform.getRemainingKGOMediaSize() + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forPlatformPickupVolume -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    mediaSize = platform.pickupMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                    val btnCancel = mRootView.findViewById<TextView>(R.id.btn_cancel)
                    btnCancel.isVisible = mediaSize <= 0
                    mBtnAcceptPhoto?.visibility = if(mediaSize <= 0) View.GONE else View.VISIBLE
                    btnCancel.setOnClickListener {
                        activityFinish(photoFor, 404)
                    }
                }
            }
            try {
                mBtnAcceptPhoto?.apply {
                    if(mediaSize <= 0) {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_light_gray))
                    } else {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_green_interactive))
                    }
                }
            } catch (ex: Exception) {
                logSentry("setImageCounter.mBtnAcceptPhoto?.apply и try{}catch")
                Log.e(TAG, "setImageCounter.mBtnAcceptPhoto?.apply и try{}catch", ex)
            }

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

//        val orientationEventListener = object : OrientationEventListener(context) {
//            override fun onOrientationChanged(orientation: Int) {
//                // Monitors orientation values to determine the target rotation value
//                when (orientation) {
//                    in 45..134 -> {
//                        rotation = Surface.ROTATION_270
//                        rotationDegrees = 270F
//                    }
//                    in 135..224 -> {
//                        rotation = Surface.ROTATION_180
//                        rotationDegrees = 180F
//                    }
//                    in 225..314 -> {
//                        rotation = Surface.ROTATION_90
//                        rotationDegrees = 90F
//                    }
//                    else -> {
//                        rotation = Surface.ROTATION_0
//                        rotationDegrees = 0F
//                    }
//                }
//                imageCapture?.targetRotation = rotation
//                imageAnalyzer?.targetRotation = rotation
//            }
//        }

//        orientationEventListener.enable()

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

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        cameraProvider.unbindAll()

        try {
            mCamera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )
            enableTorch()
            preview?.setSurfaceProvider(mPreviewView.surfaceProvider)

        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }

    }

    private fun enableTorch() {
        if (mCamera?.cameraInfo?.hasFlashUnit() == true) {
            mCamera?.cameraControl?.enableTorch(paramS().isTorchEnabled)
            mActbPhotoFlash?.isChecked = paramS().isTorchEnabled
        } else {
            Log.e(TAG, "bindCameraUseCases mCamera?.cameraInfo?.hasFlashUnit() == false")
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
        Log.d(TAG, "updateCameraUi")

        mImageCounter = mRootView.findViewById(R.id.image_counter)
        mThumbNail = mRootView.findViewById(R.id.photo_view_button)
        //todo: !!!
        mThumbNail?.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())

        if(photoFor == PhotoTypeEnum.forPlatformPickupVolume){
            mRootView.findViewById<Button>(R.id.btn_cancel).visibility = View.VISIBLE
        }
        mBtnAcceptPhoto = mRootView.findViewById(R.id.photo_accept_button)
        mBtnAcceptPhoto?.setOnClickListener {
            val mediaSize = when (photoFor) {
                PhotoTypeEnum.forBeforeMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    platform.beforeMedia.size
                }
                PhotoTypeEnum.forAfterMedia -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    platform.afterMedia.size
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    platform.failureMedia.size
                }
                PhotoTypeEnum.forContainerFailure -> {
                    val container = viewModel.findContainerEntity(containerId)
                    container.failureMedia.size
                }
                PhotoTypeEnum.forContainerBreakdown -> {
                    val container = viewModel.findContainerEntity(containerId)
                    container.breakdownMedia.size
                }

                PhotoTypeEnum.forServedKGO -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    platform.getServedKGOMediaSize()
                }

                PhotoTypeEnum.forRemainingKGO -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    platform.getRemainingKGOMediaSize()
                }

                PhotoTypeEnum.forPlatformPickupVolume -> {
                    val platform = viewModel.findPlatformEntity(platformId)
                    platform.getPickupMediaSize()
                }
                else -> 0
            }
            Log.d("MEDIASIZE :::: ", "$mediaSize")
            if (mediaSize == 0) {
                toast("Сделайте фото")
                return@setOnClickListener
            }

            if (photoFor == PhotoTypeEnum.forAfterMedia) {
                showingProgress()
            }
            activityFinish(photoFor)
        }

        val captureButton = mRootView.findViewById<ImageButton>(R.id.camera_capture_button)
        val acivImage = mRootView.findViewById<AppCompatImageView>(R.id.aciv_fragment_camera)
        captureButton.setOnClickListener {

            imageCapture?.let { imageCapture ->
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
                val metadata = Metadata().apply {
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata).build()

//                val flashMode = if (photo_flash.isChecked) FLASH_MODE_ON else FLASH_MODE_OFF
//                imageCapture.flashMode = flashMode
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
                    PhotoTypeEnum.forContainerFailure -> {
                        val container = viewModel.findContainerEntity(containerId)
                        container.failureMedia.size >= maxPhotoCount
                    }
                    PhotoTypeEnum.forContainerBreakdown -> {
                        val container = viewModel.findContainerEntity(containerId)
                        container.breakdownMedia.size >= maxPhotoCount
                    }
                    PhotoTypeEnum.forServedKGO -> {
                        val platform = viewModel.findPlatformEntity(platformId)
                        platform.getServedKGOMediaSize() >= maxPhotoCount
                    }
                    PhotoTypeEnum.forRemainingKGO -> {
                        val platform = viewModel.findPlatformEntity(platformId)
                        platform.getRemainingKGOMediaSize() >= maxPhotoCount
                    }
                    PhotoTypeEnum.forPlatformPickupVolume -> {
                        val platform = viewModel.findPlatformEntity(platformId)
                        platform.pickupMedia.size >= maxPhotoCount
                    }
                    else -> {
                        false
                    }
                }
                if (currentMediaIsFull) {
                    toast("Разрешенное количество фотографий: 3")
                    hideProgress()
                } else {
                    captureButton.isClickable = false
                    captureButton.isEnabled = false
                    captureButton.isPressed = true
                    if (paramS().isCameraSoundEnabled) {
                        val mp = MediaPlayer.create(requireContext(), R.raw.camera_sound)
//                    final MediaPlayer mp = MediaPlayer.create(this, R.raw.soho);
                        mp.start()
                    }
                    Log.d(TAG, "$rotation")

                    imageCapture.takePicture(outputOptions, cameraExecutor, object : OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: OutputFileResults) {
                            val job = CoroutineScope(Dispatchers.Main)
                            val imageUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(TAG, "Photo capture succeeded: $imageUri path: ${imageUri.path}")
                            Log.d(TAG, "Current thread: ${Thread.currentThread()}")

                            acivImage.post{
                                acivImage.visibility = View.VISIBLE
                                mPreviewView.visibility = View.GONE
//                                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.my_anim_ttest)
                                Glide.with(App.getAppliCation())
                                    .load(imageUri)
                                    .into(acivImage)
//                                acivImage.startAnimation(animation)
                            }

                            acivImage.postDelayed({
                                acivImage.visibility = View.GONE
                                mPreviewView.visibility = View.VISIBLE
                                File(imageUri.path!!).delete()
                            }, 500)


                            job.launch {
                                Log.d("AAAAAAA", Thread.currentThread().name)

                                val imageBase64 = MyUtil.imageToBase64(imageUri, rotationDegrees,
                                    requireContext())

//                                val imageBase64 = Compressor.compress(requireContext(), photoFile) {
//                                    resolution(1024, 768)
//                                    quality(100)
//                                    format(Bitmap.CompressFormat.PNG)
////                                    size(81920) // 2 MB
//                                    destination(photoFile)
//                                }

                                val gps = App.getAppliCation().gps()
                                val imageEntity = gps.inImageEntity(imageBase64)
                                if (imageEntity.isCheckedData()) {
                                    if (photoFor == PhotoTypeEnum.forContainerBreakdown
                                        || photoFor == PhotoTypeEnum.forContainerFailure
                                    ) {
                                        viewModel.baseDat.updateContainerMedia(photoFor, platformId, containerId, imageEntity)
                                    } else {
                                        viewModel.baseDat.updatePlatformMedia(photoFor, platformId, imageEntity)
                                    }
                                    setImageCounter(false)
                                    setGalleryThumbnail(imageUri)
                                } else {
                                    toast("Извините, произошла ошибка во время сохранения фото. \n повторите, пожалуйста, попытку")
                                }
                                captureButton.isClickable = true
                                captureButton.isEnabled = true
                                job.cancel()
                            }
                        }
                    })
                }
            }
        }

        mActbPhotoFlash = mRootView.findViewById<AppCompatToggleButton>(R.id.photo_flash)
        mActbPhotoFlash?.setOnClickListener {
           paramS().isTorchEnabled = mActbPhotoFlash!!.isChecked
           enableTorch()
        }

        val actbSound = mRootView.findViewById<AppCompatToggleButton>(R.id.actb_fragment_camera__sound)
        actbSound.isChecked = paramS().isCameraSoundEnabled
        actbSound?.setOnClickListener {
            paramS().isCameraSoundEnabled = actbSound.isChecked
        }

        // Listener for button used to view the most recent photo
        mThumbNail?.setOnClickListener {
            val fragment = GalleryFragment(
                platformId = platformId, photoFor = photoFor,
                containerId = containerId, imageCountListener = this
            )
            fragment.show(childFragmentManager, "GalleryFragment")
        }

        setImageCounter(false)
    }

    private fun activityFinish(photoType: Int, resultCode: Int = -1) {
        when {
            photoType == PhotoTypeEnum.forServedKGO -> {
                requireActivity().setResult(101)
            }

            photoType == PhotoTypeEnum.forRemainingKGO -> {
                requireActivity().setResult(102)
            }

            photoType == PhotoTypeEnum.forPlatformPickupVolume -> {
                if(resultCode != -1)
                    requireActivity().setResult(resultCode)
                else
                    requireActivity().setResult(14)
            }

            photoType != PhotoTypeEnum.forBeforeMedia -> {
                requireActivity().setResult(Activity.RESULT_OK)
            }
        }

        requireActivity().finish()
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
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