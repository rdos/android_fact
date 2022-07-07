package ru.smartro.worknote.work.cam

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.*
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.work.PlatformEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

//todo:
/**
val imageBase64 = Compressor.compress(requireContext(), photoFile) {
resolution(1024, 768)
quality(100)
format(Bitmap.CompressFormat.PNG)
//                                    size(81920) // 2 MB
destination(photoFile)
}
 */

open class CameraFragment(
    private val photoFor: Int,
    private val platformId: Int,
    private val containerId: Int
) : AFragment(), ImageCounter, OnImageSavedCallback {

    private var mFrameLayout: FrameLayout? = null
    private var mIsNoLimitPhoto: Boolean = false
    private var mCaptureButton: ImageButton? = null
    private lateinit var mCameraController: LifecycleCameraController
    private var mAcivPreviewPhoto: AppCompatImageView? = null
    private var mThumbNail: ImageButton? = null
    private var mImageCounter: TextView? = null
    private val maxPhotoCount = 3
    private val mCameraExecutor = Executors.newSingleThreadExecutor()
    private var mActbPhotoFlash: AppCompatToggleButton? = null
    private lateinit var mRootView: View
    private var mBtnAcceptPhoto: Button? = null
    private lateinit var mPreviewView: PreviewView

    private val PERMISSIONS_REQUEST_CODE = 10
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    private val viewModel: CameraViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        enableTorch()
    }

    //    @SuppressLint("MissingPermission")
    override fun onGetLayout(): Int {
        return R.layout.fragment_camera
    }

    //    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
        mRootView.findViewById<Button>(R.id.btn_cancel).visibility = View.GONE
        mPreviewView = mRootView.findViewById(R.id.view_finder)
        mFrameLayout = view.findViewById(R.id.fl_fragment_camera)
        //todo: mCameraController в App???
        mCameraController = LifecycleCameraController(requireContext())
        val outputSize = CameraController.OutputSize(Size(768, 1021))
        mCameraController.previewTargetSize = outputSize
        mCameraController.imageCaptureTargetSize = outputSize
        mCameraController.bindToLifecycle(viewLifecycleOwner)
        mCameraController.isTapToFocusEnabled = true
//        CameraXConfig()
//        val ins = ProcessCameraProvider.getInstance(App.getAppliCation())
//        ProcessCameraProvider.configureInstance()
        initViews()
//        mCameraController.setZoomRatio(.5000F)

        mPreviewView.controller = mCameraController
//        mPreviewView.post{
//            setUpCamera()
//            updateCameraUi()
//        }
        //todo:!!!r_dos
        mIsNoLimitPhoto = requireActivity().intent.getBooleanExtra("isNoLimitPhoto", false)
        Log.w(TAG, "mIsNoLimitPhoto=${mIsNoLimitPhoto}")
    }

    private fun takePicture() {
        if (isCurrentMediaIsFull()) {
            toast("Разрешенное количество фотографий: 3")
            (requireActivity() as ActNOAbst).hideProgress()
        } else {
//            captureButton.isClickable = false

//            captureButton.isPressed = true
            val photoFile = createFile(CameraAct.getOutputFL(), FILENAME, PHOTO_EXTENSION)

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            if (paramS().isCameraSoundEnabled) {
                val mp = MediaPlayer.create(requireContext(), R.raw.camera_sound)
                mp.start()
            }
            mCameraController.takePicture(outputOptions, mCameraExecutor, this)
        }
    }

    override fun onImageSaved(outputFileResults: OutputFileResults) {
        val imageUri = outputFileResults.savedUri!!
        Log.d("TAGS", "Photo capture succeeded: $imageUri path: ${imageUri.path}")
        Log.d("TAGS", "Current thread: ${Thread.currentThread()}")
        setImageCounter(true)
        setGalleryThumbnail(imageUri)

        Log.d("TAGS", Thread.currentThread().name)
        //todo: хз!!!,,,???

        Glide.with(App.getAppliCation())
            .asBitmap()
            .load(imageUri)
            .into(object : CustomTarget<Bitmap?>() {
                private val TOAST_TEXT: String = "Извините, произошла ошибка во время сохранения фото. \n повторите, пожалуйста, попытку"

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    try {
                        Log.d("TAGS", Thread.currentThread().name)
                        mAcivPreviewPhoto?.visibility = View.VISIBLE
                        mFrameLayout?.visibility = View.GONE
                        mAcivPreviewPhoto?.setImageBitmap(resource)

                        mAcivPreviewPhoto?.postDelayed({
                            mAcivPreviewPhoto?.visibility = View.GONE
                            mFrameLayout?.visibility = View.VISIBLE
                        }, 1000)

                        val baos = ByteArrayOutputStream()
                        resource.compress(Bitmap.CompressFormat.WEBP, 80, baos)
                        val b: ByteArray = baos.toByteArray()
                        Log.w("TAGS", "b.size=${b.size}")
                        val imageBase64 = "data:image/png;base64,${Base64.encodeToString(b, Base64.DEFAULT)}"
                        Log.w("TAGS", "imageBase64=${imageBase64.length}")
                        val gps = App.getAppliCation().gps()
                        val imageEntity = gps.inImageEntity(imageBase64, mIsNoLimitPhoto)
                        if (imageEntity.isCheckedData()) {
                            if (photoFor == PhotoTypeEnum.forContainerBreakdown
                                || photoFor == PhotoTypeEnum.forContainerFailure
                            ) {
                                viewModel.baseDat.updateContainerMedia(photoFor, platformId, containerId, imageEntity)
                            } else {
                                viewModel.baseDat.updatePlatformMedia(photoFor, platformId, imageEntity)
                            }
                        } else {
                            toast(TOAST_TEXT)
                        }
                    } catch (ex: Exception) {
                        Log.e(TAG, "eXthr.message", ex)
                        toast(TOAST_TEXT)
                    } finally {
                        File(imageUri.path!!).delete()
                        mCaptureButton?.isEnabled = true
                    }

                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

//            MyUtil.imageToBase64(imageUri, requireContext())
//                                File(imageUri.path!!).delete()
//            captureButton.isClickable = true
//            captureButton.isEnabled = true

    }

    override fun onError(exception: ImageCaptureException) {
        toast("Извините, произошла ошибка \n повторите, пожалуйста, попытку")
        Log.e("TAGS", "Photo capture failed: ${exception.message}", exception)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        (requireActivity() as ActNOAbst).hideProgress()
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
        //  displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as ActNOAbst).hideProgress()
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
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mThumbNail!!)
                }
            }
        } catch (ex: Exception) {
            logSentry("setGalleryThumbnail и try{}catch")
            Log.i(TAG, "setGalleryThumbnail и try{}catch")
            Log.e(TAG, "eXthr.message", ex)
        }
    }

    private fun setImageCounter(plus: Boolean) {
        val count = if (plus) 1 else 0
        var mediaSize = 0
        mImageCounter?.post{
            when (photoFor) {
                PhotoTypeEnum.forContainerFailure -> {
                    val container = viewModel.baseDat.getContainerEntity(containerId)
                    mediaSize = container.failureMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forContainerBreakdown -> {
                    val container = viewModel.baseDat.getContainerEntity(containerId)
                    mediaSize = container.breakdownMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    mediaSize = platform.failureMedia.size + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forAfterMedia -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    mediaSize = count + getCountAfterMedia(platform)
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forBeforeMedia -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    mediaSize = count + getCountBeforeMedia(platform)
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forServedKGO -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    mediaSize = platform.getServedKGOMediaSize() + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forRemainingKGO -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    mediaSize = platform.getRemainingKGOMediaSize() + count
                    mImageCounter?.text = "$mediaSize"
                }
                PhotoTypeEnum.forPlatformPickupVolume -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
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
                Log.i(TAG, "setImageCounter.mBtnAcceptPhoto?.apply и try{}catch")
                Log.e(TAG, "eXthr.message", ex)
            }

        }

    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        updateCameraUi()
//    }

    private fun enableTorch() {
        if (mPreviewView.controller?.cameraInfo?.hasFlashUnit() == true) {
            mPreviewView.controller?.enableTorch(paramS().isTorchEnabled)
            mActbPhotoFlash?.isChecked = paramS().isTorchEnabled
        } else {
            Log.e(TAG, "bindCameraUseCases mCamera?.cameraInfo?.hasFlashUnit() == false")
        }
    }

    private fun initViews() {
        Log.d("TAGS", "initViews")

        mImageCounter = mRootView.findViewById(R.id.image_counter)

        if(photoFor == PhotoTypeEnum.forPlatformPickupVolume){
            mRootView.findViewById<Button>(R.id.btn_cancel).visibility = View.VISIBLE
        }
        mBtnAcceptPhoto = mRootView.findViewById(R.id.photo_accept_button)
        mBtnAcceptPhoto?.setOnClickListener {
            val mediaSize = when (photoFor) {
                PhotoTypeEnum.forBeforeMedia -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    getCountBeforeMedia(platform)
                }
                PhotoTypeEnum.forAfterMedia -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    getCountAfterMedia(platform)
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    platform.failureMedia.size
                }
                PhotoTypeEnum.forContainerFailure -> {
                    val container = viewModel.baseDat.getContainerEntity(containerId)
                    container.failureMedia.size
                }
                PhotoTypeEnum.forContainerBreakdown -> {
                    val container = viewModel.baseDat.getContainerEntity(containerId)
                    container.breakdownMedia.size
                }

                PhotoTypeEnum.forServedKGO -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    platform.getServedKGOMediaSize()
                }

                PhotoTypeEnum.forRemainingKGO -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    platform.getRemainingKGOMediaSize()
                }

                PhotoTypeEnum.forPlatformPickupVolume -> {
                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
                    platform.getPickupMediaSize()
                }
                else -> 0
            }
            if (mediaSize == 0) {
                toast("Сделайте фото")
                return@setOnClickListener
            }

            if (photoFor == PhotoTypeEnum.forAfterMedia) {
                (requireActivity() as ActNOAbst).showingProgress()
            }
            activityFinish(photoFor)
        }


        mCaptureButton = mRootView.findViewById<ImageButton>(R.id.camera_capture_button)
        mAcivPreviewPhoto = mRootView.findViewById(R.id.aciv_fragment_camera_preview_photo)
        mAcivPreviewPhoto?.visibility = View.GONE
        mActbPhotoFlash = mRootView.findViewById<AppCompatToggleButton>(R.id.photo_flash)
        mCameraController.initializationFuture.addListener({
            Log.d("TAGS", "initializationFuture")
            if (hasBackCamera()) {
                mCaptureButton?.setOnClickListener {
                    mCaptureButton?.isEnabled = false
                    try {
                        takePicture()
                    } catch (ex: Exception) {
                        toast("Извините, произошла ошибка \n повторите, пожалуйста, попытку")
                        Log.e(TAG, "eXthr.message", ex)
                        mCaptureButton?.isEnabled = true
                    }

                }
                mActbPhotoFlash?.setOnClickListener {
                    paramS().isTorchEnabled = mActbPhotoFlash!!.isChecked
                    enableTorch()
                }
            } else {
                toast("Извините, но на вашем устройстве \n отсутсвует камера")
            }

            Log.d("TAGS", Thread.currentThread().name)
        }, ContextCompat.getMainExecutor(requireContext()))


        val actbSound = mRootView.findViewById<AppCompatToggleButton>(R.id.actb_fragment_camera__sound)
        actbSound.isChecked = paramS().isCameraSoundEnabled
        actbSound?.setOnClickListener {
            paramS().isCameraSoundEnabled = actbSound.isChecked
        }

        // Listener for button used to view the most recent photo
        mThumbNail = mRootView.findViewById(R.id.photo_view_button)
        //todo: !!!
        mThumbNail?.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
        mThumbNail?.setOnClickListener {
            val fragment = GalleryFragment(
                platformId = platformId, photoFor = photoFor,
                containerId = containerId, imageCountListener = this
            )
            fragment.show(childFragmentManager, "GalleryFragment")
        }

        setImageCounter(false)
    }


    private fun isCurrentMediaIsFull(): Boolean {
        val res = when (photoFor) {
            PhotoTypeEnum.forAfterMedia -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                //todo: фильтер конечно же...!!!
                getCountAfterMedia(platform) >= if(mIsNoLimitPhoto) Int.MAX_VALUE else maxPhotoCount
            }
            PhotoTypeEnum.forBeforeMedia -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                //todo: фильтер конечно же лучше переписать)))) !!!
                getCountBeforeMedia(platform) >= if(mIsNoLimitPhoto) Int.MAX_VALUE else maxPhotoCount
            }
            PhotoTypeEnum.forPlatformProblem -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.failureMedia.size >= maxPhotoCount
            }
            PhotoTypeEnum.forContainerFailure -> {
                val container = viewModel.baseDat.getContainerEntity(containerId)
                container.failureMedia.size >= maxPhotoCount
            }
            PhotoTypeEnum.forContainerBreakdown -> {
                val container = viewModel.baseDat.getContainerEntity(containerId)
                container.breakdownMedia.size >= maxPhotoCount
            }
            PhotoTypeEnum.forServedKGO -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.getServedKGOMediaSize() >= maxPhotoCount
            }
            PhotoTypeEnum.forRemainingKGO -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.getRemainingKGOMediaSize() >= maxPhotoCount
            }
            PhotoTypeEnum.forPlatformPickupVolume -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.pickupMedia.size >= maxPhotoCount
            }
            else -> {
                false
            }
        }

        return res
    }

    private fun getCountAfterMedia(platform: PlatformEntity): Int {
        var res = Inull
        if (mIsNoLimitPhoto) {
            res = platform.afterMedia.size
        } else {
            res = platform.afterMedia.filter {!it.isNoLimitPhoto }.size
        }
        return res
    }

    private fun getCountBeforeMedia(platform: PlatformEntity): Int {
        var res = Inull
        if (mIsNoLimitPhoto) {
            res = platform.beforeMedia.size
        } else {
            res = platform.beforeMedia.filter {!it.isNoLimitPhoto }.size
        }
        return res
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

    //    private fun hasFrontCamera(): Boolean {
//        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
//    }
    //todo:???
    private fun hasBackCamera(): Boolean {
        return mCameraController.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    companion object {
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val MANUAL_FOCUS_DURATION__MS = 8000L
        private const val ANIMATION_MANUAL_FOCUS_DURATION__MS = MANUAL_FOCUS_DURATION__MS / 2
        //        private fun createFile(baseFolder: File): File {
//            val res = File(baseFolder, FILENAME)
//            res.deleteOnExit()
//            return res
//        }
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
    }

    override fun mediaSizeChanged() {
        setImageCounter(false)
    }

}

interface ImageCounter {
    fun mediaSizeChanged()
}

/**
 *    val orientationEventListener = object : OrientationEventListener(context) {
override fun onOrientationChanged(orientation: Int) {
// Monitors orientation values to determine the target rotation value
Log.i(TAG, "onOrientationChanged.before.mRotation=${mRotation} orientation=${orientation}")
when (orientation) {
in 45..134 -> {
mRotation = Surface.ROTATION_270
rotationDegrees = 270F
}
in 135..224 -> {
mRotation = Surface.ROTATION_180
rotationDegrees = 180F
}
in 225..314 -> {
mRotation = Surface.ROTATION_90
rotationDegrees = 90F
}
else -> {
mRotation = Surface.ROTATION_0
rotationDegrees = 0F
}
}
Log.i(TAG, "onOrientationChanged.after.mRotation=${mRotation}")
mImageCapture?.targetRotation = mRotation
imageAnalyzer?.targetRotation = mRotation
}
}

orientationEventListener.enable()
 */
class CameraViewModel(application: Application) : BaseViewModel(application) {


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
