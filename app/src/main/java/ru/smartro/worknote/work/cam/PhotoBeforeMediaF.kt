package ru.smartro.worknote.work.cam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class PhotoBeforeMediaF : APhotoFragment() {
    companion object {
//        fun newInstance(workOrderId: Any? = null): PhotoBeforeMediaF {
//            workOrderId as Int
//            val fragment = PhotoBeforeMediaF()
//            fragment.addArgument(workOrderId)
//            return fragment
//        }
    }
    private var mPlatformEntity: PlatformEntity? = null

    override fun onSaveFoto() {
//        TODO("Not yet implemented")
//        id: String = UUID.randomUUID().toString(),
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "beforeMedia"
    }

    override fun onBeforeUSE() {
//        TODO("Not yet implemented")
        val platformId = getArgumentID()
//        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        viewModel.getPlatformEntity(platformId)

        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
            mPlatformEntity = it
        }

    }

    fun imageToBase64(imageUri: File, rotationDegrees: Float = Fnull): ImageEntity {
        val imageStream: InputStream = imageUri.inputStream()
        val resource = BitmapFactory.decodeStream(imageStream)


        val baos = ByteArrayOutputStream()
        resource.compress(Bitmap.CompressFormat.WEBP, 80, baos)
        val b: ByteArray = baos.toByteArray()
        Log.w("TAGS", "b.size=${b.size}")
        val imageBase64 = "data:image/png;base64,${Base64.encodeToString(b, Base64.DEFAULT)}"
        Log.w("TAGS", "imageBase64=${imageBase64.length}")
        val gps = App.getAppliCation().gps()
        val imageEntity = gps.inImageEntity(imageBase64)
//        imageEntity.isNoLimitPhoto = true
//        onGetImage
        return imageEntity
    }

    inner class FileScanner : AbsObject(TAG, "ImageEntityScanner") {
        private var mIdx: Int = Inull
        private var mFileS: Array<File>? = null

        fun scan(): Boolean {

            if (mFileS == null) {
                return false
            }
            while (mFileS!![mIdx].isDirectory) {
                mIdx++
                Log.w(TAG, "onAfterUSE")
                Log.e(TAG, "onAfterUSE")
                Log.d(TAG, "onAfterUSE")
            }
            if (mFileS!!.size >= mIdx) {
                return false
            }
            return true
        }

        fun getImageEntity(): ImageEntity {
            val imageEntity = imageToBase64(mFileS!![mIdx])
            return imageEntity
        }

        private fun init(){
            mFileS = getOutputD().listFiles()
            mIdx = 0
        }

        fun dropFile() {
            TODO("Not yet implemented")
        }

        init {
            init()
        }
    }
    override fun onAfterUSE() {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return
        }


        val fileScanner = FileScanner()
        while (fileScanner.scan()) {
            val imageEntity = fileScanner.getImageEntity()
            viewModel.baseDat.addBeforeMediaPlatform(mPlatformEntity?.platformId!!, imageEntity)
            fileScanner.dropFile()
        }
      
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
    }

    override fun onGetTextLabelFor() = getString(R.string.service_before)
    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onmThumbNailClick() {

    }

    override fun onBtnAcceptPhoto_know1() {
//        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (getOutputFileCount() <= 0) {
            navigateClose()
        } else {
            navigateMain(R.id.PServeF, mPlatformEntity?.platformId)

        }
    }

}