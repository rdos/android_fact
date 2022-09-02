package ru.smartro.worknote.presentation.came

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Size
import android.view.*
import android.widget.*
import androidx.appcompat.widget.*
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.ANOFragment
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM
import ru.smartro.worknote.work.ImageEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executors

//todo:AbsViewGroup
/**
val imageBase64 = Compressor.compress(requireContext(), photoFile) {
resolution(1024, 768)
quality(100)
format(Bitmap.CompressFormat.PNG)
//                                    size(81920) // 2 MB
destination(photoFile)
}
 */

//mask
const val C_PHOTO_D = "photo"
abstract class APhotoFragment(
) : ANOFragment(), OnImageSavedCallback {
    private var acetComment: AppCompatEditText? = null
    protected var mAcactvFail: AppCompatAutoCompleteTextView? = null
    private var mMediaPlayer: MediaPlayer? = null
//    override var TAG : String = "--Aa${this::class.simpleName}"

    protected var mMaxPhotoCount = 3
    private var acbCancel: AppCompatButton? = null
    private var ibTakePhoto: AppCompatImageButton? = null
    private lateinit var mCameraController: LifecycleCameraController
    private var mThumbNail: AppCompatImageButton? = null
    private var mImageCounter: AppCompatTextView? = null
    private val mCameraExecutor = Executors.newSingleThreadExecutor()
    private var mActbPhotoFlash: AppCompatToggleButton? = null
    private var acbGotoNext: AppCompatButton? = null
    private lateinit var mPreviewView: PreviewView

    private val PERMISSIONS_REQUEST_CODE = 10
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

//  todo:!R_dos??  protected val viewModel: PlatformServeSharedViewModel by viewModel()
    protected val viewModel: ServePlatformVM by activityViewModels()


    protected open fun onGetTextLabelFor(): String? = null
    open protected fun onGetTextForFailHint(): String?{
        // TODO: name  onGetTextForFailHint = onGetTextForBlablabla)))!R_dos
        return null
    }
    protected abstract fun onGetMediaRealmList(): RealmList<ImageEntity>

    protected abstract fun onGetDirName(): String
    protected abstract fun onBeforeUSE()
    abstract fun onGotoNext(): Boolean
    protected abstract fun onAfterUSE(imageS: List<ImageEntity>)
    protected abstract fun onSavePhoto()
    protected abstract fun onGetIsVisibleBtnCancel(): Boolean
    protected abstract fun onClickBtnCancel()
    protected open fun onTakePhoto() {

    }
    open fun onGetStringList(): List<String>? {
        return null
    }

    open fun onGetIsVisibleComment(): Boolean {
        return false
    }

    protected fun getCommentText(): String {
        return acetComment?.text.toString()
    }

    override fun onResume() {
        super.onResume()
        enableFlash()
    }

    //    @SuppressLint("MissingPermission")
    override fun onGetLayout(): Int {
        return R.layout.f_aphoto
    }

    //    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        mMediaPlayer = MediaPlayer.create(requireContext(), R.raw.camera_sound)
        view.findViewById<AppCompatTextView>(R.id.label_for).visibility = View.GONE
        mPreviewView = view.findViewById(R.id.view_finder)
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
        initViews(view)
//        mCameraController.setZoomRatio(.5000F)

        mPreviewView.controller = mCameraController

        onBeforeUSE()

        if (GalleryPhotoF.isCostFileNotExist(getOutputD())) {
            //        getArgSBundle() !!! no_restorePhotoFileS
            restorePhotoFileS(onGetMediaRealmList())
        }
    }

    private fun getMediaCount(): Int {
        val files = AppliCation().getDFileList(C_PHOTO_D)
        var result = 0
        files?.let { itS ->
            result = itS.size
            for(f in  itS) {
                if (f.isDirectory) {
                    result -= 1
                }
            }
        }
        return result
    }

    private fun takePhoto() {
        val mediaSize = getMediaCount()
        if (mediaSize >= mMaxPhotoCount) {
            toast("Разрешенное количество фотографий: ${mMaxPhotoCount}")
            ibTakePhoto?.isEnabled = true
            return
        }
        onTakePhoto()
        val photoFL = createFile(getOutputD(), MyUtil.timeStampInSec().toString())
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFL).build()

        mCameraController.takePicture(outputOptions, mCameraExecutor, this)

        if (paramS().isCameraSoundEnabled) {
            mMediaPlayer?.start()
        }
    }

    protected fun dropOutputD() {
        val basePhotoD = AppliCation().getDPath(C_PHOTO_D)
        val file = File(basePhotoD)
        file.deleteRecursively()
    }

    override fun onDetach() {
        super.onDetach()
        LOG.warn( "onDetach")
    }

    override fun onDestroy() {
        super.onDestroy()
        LOG.info( "onDestroy")
    }

    fun getOutputD(): File {
        val basePhotoD = AppliCation().getD(C_PHOTO_D)
        return basePhotoD
    }


    private val TOAST_TEXT: String = "Извините, произошла ошибка во время сохранения фото. \n повторите, пожалуйста, попытку"
    override fun onImageSaved(outputFileResults: OutputFileResults) {
        val imageUri = outputFileResults.savedUri!!

        log("Photo capture succeeded: $imageUri path: ${imageUri.path}")
        LOG.error( "Current thread: ${Thread.currentThread().id}")
        setImageCounter()
        setGalleryThumbnail(imageUri)

        try {
            val imageFile = File(imageUri.path!!)
            val imageStream: InputStream = imageFile.inputStream()
            val baos = ByteArrayOutputStream()
            var bitmap: Bitmap? = null
            imageStream.use {
                val resource = BitmapFactory.decodeStream(imageStream)

                if(resource.width > resource.height) {
                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    val scaledBitmap = Bitmap.createScaledBitmap(resource, resource.width, resource.height, true)
                    bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                }

            }
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val byteArray = baos.toByteArray()
            val outputStream = imageFile.outputStream()
            outputStream.use { it ->
                it.write(byteArray)
            }
            LOG.warn( Thread.currentThread().name)


            onSavePhoto()
        } catch (ex: Exception) {
            LOG.error("onImageSaved", ex)
            toast(TOAST_TEXT)
        }
    }

    override fun onError(exception: ImageCaptureException) {
        toast(TOAST_TEXT)
        LOG.error("onError", exception)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        getAct().hideProgress()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAct().hideProgress()
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
            LOG.info( "setGalleryThumbnail и try{}catch")
            LOG.error("setGalleryThumbnail", ex)
        }
    }

    private fun setImageCounter() {
            val mediaSize = getMediaCount()
        mImageCounter?.post{
            mImageCounter?.text = "$mediaSize"
            try {

                acbGotoNext?.apply {
                    if(mediaSize <= 0) {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_light_gray))
                    } else {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_green_interactive))
                    }
                }
                if (mediaSize <= 0) {
                    if(onGetIsVisibleBtnCancel()) {
//            photoFor == PhotoTypeEnum.forPlatformPickupVolume
                        acbCancel?.visibility = View.VISIBLE
                        acbGotoNext?.visibility = View.GONE
                    } else {
                        acbGotoNext?.visibility = View.VISIBLE
                    }
                } else {
                    acbGotoNext?.visibility = View.VISIBLE
                    acbCancel?.visibility = View.GONE
                }
//                 = if(mediaSize <= 0) else View.VISIBLE
                acbCancel?.setOnClickListener {
                    onClickBtnCancel()
                }
                ibTakePhoto?.isEnabled = true

            } catch (ex: Exception) {
                logSentry("setImageCounter.mBtnAcceptPhoto?.apply и try{}catch")
                LOG.info("setImageCounter.mBtnAcceptPhoto?.apply и try{}catch")
                LOG.error("setImageCounter", ex)
            }

        }

    }


//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        updateCameraUi()
//    }

    private fun enableFlash() {
        val isEnableTorch = paramS().isTorchEnabled
        log("enableFlash:isEnableTorch= ${isEnableTorch}")
        mCameraController.enableTorch(isEnableTorch)
        mActbPhotoFlash?.isChecked = isEnableTorch
    }

    //onViewCreated
//    protected abstract fun onInitViewS(mRootView: View)

    protected fun tvLabelFor(view: View){
        val tvLabelFor = view.findViewById<TextView>(R.id.label_for)
        val labelForText = onGetTextLabelFor()
        if(labelForText.isNullOrEmpty()) {
            tvLabelFor?.visibility = View.GONE
        } else {
            tvLabelFor?.apply {
                visibility = View.VISIBLE
                text = labelForText
            }
        }
    }


    private fun initViews(view: View) {
        log("initViews")

        mImageCounter = view.findViewById(R.id.image_counter)
        acbCancel = view.findViewById(R.id.acb_f_aphoto__cancel)
        acbCancel?.visibility = View.GONE

        acetComment = view.findViewById(R.id.acet_f_aphoto__comment)
        acetComment?.visibility = View.GONE
        if(onGetIsVisibleComment()) {
            acetComment?.visibility = View.VISIBLE
        }

        mAcactvFail = view.findViewById(R.id.acactv_f_aphoto__fail_reason)
        val reasonsString = onGetStringList()

        if (reasonsString.isNullOrEmpty()) {
            tvLabelFor(view)
            mAcactvFail?.visibility = View.GONE
            acetComment?.visibility = View.GONE
        } else {
            mAcactvFail?.visibility = View.VISIBLE
            acetComment?.visibility = View.VISIBLE
            mAcactvFail?.requestFocus()
            mAcactvFail?.hint = onGetTextForFailHint()
            mAcactvFail?.setAdapter(ArrayAdapter(getAct(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, reasonsString))
            mAcactvFail?.setOnClickListener {
                mAcactvFail?.showDropDown()
            }
//            acactv.setOnFocusChangeListener { _, _ ->
//                acactv.showDropDown()
//            }
        }

        
//        onInitViewS(mRootView)

        acbGotoNext = view.findViewById(R.id.acb_f_aphoto__goto_next)
        acbGotoNext?.setOnClickListener {
            val mediaSize = getMediaCount()
            if (mediaSize == 0) {
                toast("Сделайте фото")
                return@setOnClickListener
            }
            if (!onGotoNext()) {
                return@setOnClickListener
            }
            try {
//                showingProgress("Сохраняем фото")
                val photoFileScanner = PhotoFileScanner(C_PHOTO_D)
                val imageS = mutableListOf<ImageEntity>()
                while (photoFileScanner.scan()) {
                    val imageEntity = photoFileScanner.getImageEntity()
                    imageS.add(imageEntity)
                }
                onAfterUSE(imageS)
                dropOutputD()
            } finally {
//                hideProgress()
            }
        }


        ibTakePhoto = view.findViewById(R.id.ib_f_aphoto__takephoto)
        mActbPhotoFlash = view.findViewById<AppCompatToggleButton>(R.id.photo_flash)
        mCameraController.initializationFuture.addListener({
            log("initializationFuture")
            if (hasBackCamera()) {
                if (hasFlashUnit()) {
                    enableFlash()
                }

                ibTakePhoto?.setOnClickListener {
                    ibTakePhoto?.isEnabled = false
                    try {
                        takePhoto()
                    } catch (ex: Exception) {
                        toast("Извините, произошла ошибка \n повторите, пожалуйста, попытку")
                        LOG.error("ibTakePhoto?.setOnClickListener", ex)
                        ibTakePhoto?.isEnabled = true
                    }

                }
                mActbPhotoFlash?.setOnClickListener {
                    paramS().isTorchEnabled = mActbPhotoFlash!!.isChecked
                    log("mActbPhotoFlash:setOnClickListener paramS().isTorchEnabled=${paramS().isTorchEnabled}")
                    if (hasFlashUnit()) {
                        enableFlash()
                    } else {
                        toast("На Вашем телефоне нет фонаря")
                    }
                }
                setImageCounter()
            } else {
                toast("Извините, но на вашем устройстве \n отсутсвует камера")
            }

            LOG.error( Thread.currentThread().name)
        }, ContextCompat.getMainExecutor(requireContext()))


        val actbSound = view.findViewById<AppCompatToggleButton>(R.id.actb_fragment_camera__sound)
        actbSound.isChecked = paramS().isCameraSoundEnabled
        actbSound?.setOnClickListener {
            paramS().isCameraSoundEnabled = actbSound.isChecked
        }

        // Listener for button used to view the most recent photo
        mThumbNail = view.findViewById(R.id.photo_view_button)
        //todo: !!!
        mThumbNail?.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
        mThumbNail?.setOnClickListener {
            val mediaSize = getMediaCount()
            if (mediaSize <= 0) {
                return@setOnClickListener
            }
            navigateMain(R.id.GalleryPhotoF, getArgumentID(), onGetDirName())
        }
    }



    //todo:???
    //    private fun hasFrontCamera(): Boolean {
//        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
//    }
    private fun hasBackCamera(): Boolean {
        return mCameraController.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }
    private fun hasFlashUnit(): Boolean {
        return mCameraController.cameraInfo?.hasFlashUnit() ?: false
    }

    companion object {
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val MANUAL_FOCUS_DURATION__MS = 8000L
        private const val ANIMATION_MANUAL_FOCUS_DURATION__MS = MANUAL_FOCUS_DURATION__MS / 2
        private fun createFile(baseFolder: File, fileName: String) =
            File(baseFolder, fileName + PHOTO_EXTENSION)

    }
    private fun restorePhotoFileS(imageS: RealmList<ImageEntity>) {
        log("restorePhotoFileS(imageS.size=${imageS.size}) before")
        for (imageEntity in imageS) {
            log("restorePhotoFileS(/):for(imageEntity in imageS).imageEntityID=${imageEntity.date}")
            val imageInBase64 = imageEntity.image!!.replace("data:image/png;base64,", "")
            val byteArray: ByteArray =
                Base64.decode(imageInBase64, Base64.DEFAULT)
            val photoFL = createFile(getOutputD(), imageEntity.date.toString())
            val outputStream = photoFL.outputStream()
            outputStream.use { it ->
                it.write(byteArray)
            }
        }
        log("restorePhotoFileS()after")
    }

    inner class PhotoFileScanner(val Dname: String) : AbsObject(TAG, "ImageEntityScanner") {
        private var mIdx: Int = Inull
        private var mFileS: Array<File>? = null

        fun scan(): Boolean {
            LOG.warn( "scan().before")
            if (mFileS == null) {
                LOG.error( "scan(false).after mFileS == null")
                return false
            }
            if (mIdx > mFileS!!.size - 1) {
                log("scan(false).after mIdx > mFileS!!.size")
                return false
            }
            while (mFileS!![mIdx].isDirectory) {
                mIdx++
                if (mIdx > mFileS!!.size - 1) {
                    log("scan(false).mFileS!![mIdx].isDirectory) mIdx > mFileS!!.size")
                    return false
                }
                LOG.warn( "onAfterUSE")
                LOG.error( "onAfterUSE")
                log("onAfterUSE")
            }

            log("scan(true).after ")
            return true
        }

        private fun imageToBase64(imageFile: File, rotationDegrees: Float = Fnull): ImageEntity {
            val imageStream: InputStream = imageFile.inputStream()
            val baos = ByteArrayOutputStream()
            imageStream.use {
                val resource = BitmapFactory.decodeStream(imageStream)
                resource.compress(Bitmap.CompressFormat.WEBP, 90, baos)
            }
            val b: ByteArray = baos.toByteArray()
            LOG.warn( "b.size=${b.size}")
            val imageBase64 = "data:image/png;base64,${Base64.encodeToString(b, Base64.DEFAULT)}"
            LOG.warn( "imageBase64=${imageBase64.length}")
            val gps = App.getAppliCation().gps()
            val imageEntity = gps.inImageEntity(imageBase64)

            imageEntity.date = imageFile.name.substring(0, imageFile.name.length - 4).toLong()
//        imageEntity.isNoLimitPhoto = true
//        onGetImage
            return imageEntity
        }

        fun getImageEntity(): ImageEntity {
            val imageFile = mFileS!![mIdx]
            val imageEntity = imageToBase64(imageFile)
            mIdx++
            return imageEntity
        }

        private fun init(){
//            val filter = FilenameFilter { dir, name ->!!!
            mFileS = AppliCation().getDFileList(Dname)
            mIdx = 0
        }

        init {
            init()
        }
    }
}

/**
 *    val orientationEventListener = object : OrientationEventListener(context) {
override fun onOrientationChanged(orientation: Int) {
// Monitors orientation values to determine the target rotation valueLoG.info( "onOrientationChanged.before.mRotation=${mRotation} orientation=${orientation}")
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
}LoG.info( "onOrientationChanged.after.mRotation=${mRotation}")
mImageCapture?.targetRotation = mRotation
imageAnalyzer?.targetRotation = mRotation
}
}

orientationEventListener.enable()
 */

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
//                log("Average luminosity: $luma")
//                // Update timestamp of last analyzed frame
//                lastAnalyzedTimestamp = currentTimestamp
//            }
//        }
//
//
//    }
