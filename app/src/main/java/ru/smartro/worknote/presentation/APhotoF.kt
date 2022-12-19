package ru.smartro.worknote.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Size
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.*
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.log.todo.ImageEntity
import java.io.*
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
abstract class
APhotoF(
) : AF(), OnImageSavedCallback {
    private var mIsSavePhotoMode: Boolean = false
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
    protected val viewModel: VMPserve by activityViewModels()

    //    @SuppressLint("MissingPermission")
    override fun onGetLayout(): Int {
        return R.layout.f_aphoto
    }

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

    protected fun setCommentText(comment: String) {
        acetComment?.setText(comment)
    }

    protected fun getCommentText(): String {
        return acetComment?.text.toString()
    }

    override fun onResume() {
        super.onResume()
        enableFlash()
    }

    //    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOG.debug("onViewCreated")
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
        mIsSavePhotoMode = true
        onTakePhoto()
        val photoFL = createFile(getOutputD(), App.getAppliCation().timeStampInSec().toString())
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

        LOG.debug("Photo capture succeeded: $imageUri path: ${imageUri.path}")
        LOG.error( "Current thread: ${Thread.currentThread().id}")
        setGalleryThumbnail(imageUri)

        try {
            val imageFile = File(imageUri.path!!)
            val imageStream: InputStream = imageFile.inputStream()
            val baos = ByteArrayOutputStream()
            var bitmap: Bitmap? = BitmapFactory.decodeStream(imageStream)

            imageStream.use {
                val resource = BitmapFactory.decodeStream(imageStream)

                if(resource.width > resource.height) {
                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    val scaledBitmap = Bitmap.createScaledBitmap(resource, resource.width, resource.height, true)

                    bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                } else {
                    bitmap = resource
                }

            }
            
            bitmap!!.compress(Bitmap.CompressFormat.WEBP, 90, baos)
            val byteArray = baos.toByteArray()
            val outputStream = imageFile.outputStream()
            outputStream.use {
                it.write(byteArray)
            }
            LOG.warn( Thread.currentThread().name)

            onSavePhoto()
        } catch (ex: Exception) {
            LOG.error("onImageSaved", ex)
            toast(TOAST_TEXT)
        } finally {
            setImageCounter()
        }
    }

    override fun onError(exception: ImageCaptureException) {
        toast(TOAST_TEXT)
        LOG.error("onError", exception)
        setImageCounter()
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
        if (!App.getAppliCation().hasPermissions(requireContext())) {
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
                    viewModel.updatePlatformEntity()
                }
                ibTakePhoto?.isEnabled = true
                mIsSavePhotoMode = false
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
        LOG.debug("enableFlash:isEnableTorch= ${isEnableTorch}")
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
        LOG.debug("initViews")

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
            if (mIsSavePhotoMode) {
                toast("фото медленно сохраняется :(")
                return@setOnClickListener
            }
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
                LOG.debug("onAfterUSE")
                onAfterUSE(imageS)
                viewModel.updatePlatformEntity()
                LOG.info("onAfterUSE.after")
                dropOutputD()
            } finally {
//                hideProgress()
            }
        }


        ibTakePhoto = view.findViewById(R.id.ib_f_aphoto__takephoto)
        mActbPhotoFlash = view.findViewById<AppCompatToggleButton>(R.id.photo_flash)
        mCameraController.initializationFuture.addListener({
            LOG.debug("initializationFuture")
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
                        mIsSavePhotoMode = false
                    }

                }
                mActbPhotoFlash?.setOnClickListener {
                    paramS().isTorchEnabled = mActbPhotoFlash!!.isChecked
                    LOG.debug("mActbPhotoFlash:setOnClickListener paramS().isTorchEnabled=${paramS().isTorchEnabled}")
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

        mThumbNail = view.findViewById(R.id.photo_view_button)
        //todo: !!!
        mThumbNail?.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
        mThumbNail?.setOnClickListener {
            val mediaSize = getMediaCount()
            if (mediaSize <= 0) {
                return@setOnClickListener
            }
            navigateNext(R.id.GalleryPhotoF, getArgumentID(), onGetDirName())
        }
    }


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
        LOG.debug("restorePhotoFileS(imageS.size=${imageS.size}) before")
        for (imageEntity in imageS) {
            LOG.debug("restorePhotoFileS(/):for(imageEntity in imageS).imageEntityID=${imageEntity.date}")
            val imageInBase64 = imageEntity.image!!.replace("data:image/png;base64,", "")
            val byteArray: ByteArray =
                Base64.decode(imageInBase64, Base64.DEFAULT)
            val photoFL = createFile(getOutputD(), imageEntity.date.toString())
            val outputStream = photoFL.outputStream()
            outputStream.use { it ->
                it.write(byteArray)
            }
        }
        LOG.debug("restorePhotoFileS()after")
    }

    inner class PhotoFileScanner(val Dname: String) : AbsObject("PhotoFileScanner") {
        private var mIdx: Int = Inull
        private var mFileS: Array<File>? = null

        fun scan(): Boolean {
            LOG.warn( "scan().before")
            if (mFileS == null) {
                LOG.error( "scan(false).after mFileS == null")
                return false
            }
            if (mIdx > mFileS!!.size - 1) {
                LOG.debug("scan(false).after mIdx > mFileS!!.size")
                return false
            }
            while (mFileS!![mIdx].isDirectory) {
                mIdx++
                if (mIdx > mFileS!!.size - 1) {
                    LOG.debug("scan(false).mFileS!![mIdx].isDirectory) mIdx > mFileS!!.size")
                    return false
                }
                LOG.warn( "onAfterUSE")
                LOG.error( "onAfterUSE")
                LOG.debug("onAfterUSE")
            }

            LOG.debug("scan(true).after ")
            return true
        }

        private fun imageToBase64(imageFile: File): ImageEntity {
            val size = imageFile.length().toInt()
            val b = ByteArray(size)
            try {
                val buf = BufferedInputStream(FileInputStream(imageFile))
                buf.read(b, 0, b.size)
                buf.close()
            } catch (e: FileNotFoundException) {
                AppliCation().sentryCaptureException(e)
                LOG.error(e.stackTraceToString())
            } catch (e: IOException) {
                AppliCation().sentryCaptureException(e)
                LOG.error(e.stackTraceToString())
            }

            LOG.warn( "b.size=${size}")
            val imageBase64 = "data:image/png;base64,${Base64.encodeToString(b, Base64.DEFAULT)}"
            LOG.warn( "imageBase64=${imageBase64.length}")
            val gps = App.getAppliCation().gps()
            val imageEntity = gps.inImageEntity(imageBase64)

            imageEntity.date = imageFile.name.substring(0, imageFile.name.length - 4).toLong()
//        imageEntity.isNoLimitPhoto = true
//        onGetImage
            imageEntity.md5 = MD5.calculateMD5(imageFile)!!
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


val EXTENSION_WHITELIST = arrayOf("JPG")
//class GalleryFragment(p_id: Int) internal constructor()
class GalleryPhotoF : AF() {

    private lateinit var mediaList: MutableList<File>

    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment {
            val textNumOfCount = "${position+1} из $count"
            return MediaAdapterFragment(mediaList[position], textNumOfCount)
        }
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true

        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
        mediaList = AppliCation().getDFileList(C_PHOTO_D).sortedDescending().toMutableList()
    }



    override fun onGetLayout(): Int {
        return R.layout.f_gallery_photo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apibDelete =  view.findViewById<AppCompatImageButton>(R.id.apib_f_gallery_photo__delete)

        val viewPager = view.findViewById<ViewPager>(R.id.vp_f_gallery_photo)

        val apibBack = view.findViewById<AppCompatImageButton>(R.id.apib_f_gallery_photo__back)
        apibBack.setOnClickListener {
            navigateBack()
        }

        //Checking media files list
        if (mediaList.isEmpty()) {
            apibDelete.isEnabled = false
//            fragmentGalleryBinding.shareButton.isEnabled = false
        }

        // Populate the ViewPager and implement a cache of two media items
        viewPager.apply {
            offscreenPageLimit = 2
            adapter = MediaAdapter(childFragmentManager)
        }

        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
//            fragmentGalleryBinding.cutoutSafeArea.padWithDisplayCutout()
        }

        // Handle back button press


        // Handle share button press
//        fragmentGalleryBinding.shareButton.setOnClickListener {
//
//            mediaList.getOrNull(fragmentGalleryBinding.photoViewPager.currentItem)?.let { mediaFile ->
//
//                // Create a sharing intent
//                val intent = Intent().apply {
//                    // Infer media type from file extension
//                    val mediaType = MimeTypeMap.getSingleton()
//                            .getMimeTypeFromExtension(mediaFile.extension)
//                    // Get URI from our FileProvider implementation
//                    val uri = FileProvider.getUriForFile(
//                            view.context, BuildConfig.APPLICATION_ID + ".provider", mediaFile)
//                    // Set the appropriate intent extra, type, action and flags
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    type = mediaType
//                    action = Intent.ACTION_SEND
//                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                }
//
//                // Launch the intent letting the user choose which app to share with
//                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
//            }
//        }

        // Handle delete button press
        apibDelete.setOnClickListener {

            mediaList.getOrNull(viewPager.currentItem)?.let { imageEntity ->
                AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                    .setTitle("Подтвердите")
                    .setMessage(getString(R.string.warning_detele))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes) { _, _ ->
//                        viewModel.removeImageEntity(p_platformId, p_containerId, photoFor , imageEntity.md5)
                        mediaList.remove(imageEntity)
                        imageEntity.delete()
                        createCOSTFile()
                        viewPager.adapter?.notifyDataSetChanged()
                        if (mediaList.size <= 0) {
                            navigateBack()
                        }
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .create().show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateBack()
    }

    companion object {
        private const val COST___EXTENSION = "cost."
        private const val FILE_NAME_KEY = "no_restorePhotoFileS"
        private const val NUM_OF_COUNT = "num_of_count"

        private fun createCOSTFile() {
            val outD = App.getAppliCation().getD(C_PHOTO_D)
            createFile(outD, getCOSTFileName())
        }
        private fun createFile(baseFolder: File, fileName: String) {
            val costFL = File(baseFolder, fileName)
            val outputStream = costFL.outputStream()
            outputStream.use { it ->
                it.write(Int.MAX_VALUE)
            }
        }
        fun getCOSTFileName(): String {
            return COST___EXTENSION + FILE_NAME_KEY
        }


        fun isCostFileNotExist(outD: File): Boolean {
            val costFL = File(outD, getCOSTFileName())
            return !costFL.exists()
        }
//        fun create(image: ImageEntity, numOfCount: String) = MediaAdapterFragment().apply {
//            arguments = Bundle().apply {
//                putString(FILE_NAME_KEY, image.absolutePath)
//                putString(NUM_OF_COUNT, numOfCount)
//            }
//        }
    }
    class MediaAdapterFragment(val image: File, val numOfCount: String) : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.media_fragment, container, false)
            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
//        val args = arguments ?: return
            val aptvNumOfCount = view.findViewById<AppCompatTextView>(R.id.aptv_media_fragment__num_of_count)
            aptvNumOfCount.text = numOfCount
            val imageView = view.findViewById<AppCompatImageView>(R.id.apiv_media_fragment)
//        val resource = args.getString(FILE_NAME_KEY)?.let { File(it) } ?: R.drawable.ic_photo
//        val bmp = image?.let { BitmapFactory.decodeByteArray(image, 0, it.size) }
//        val resource = args.getString(FILE_NAME_KEY)?.let { File(it) } ?: R.drawable.ic_photo
            Glide.with(view).load(image).into(imageView)
        }


    }
}
