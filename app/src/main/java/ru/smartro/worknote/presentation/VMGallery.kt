package ru.smartro.worknote.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.App
import ru.smartro.worknote.Dnull
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import ru.smartro.worknote.log.todo.*
import ru.smartro.worknote.App.Companion.PhotoTypeMapping
import java.io.File

class VMGallery(val app: Application) : ru.smartro.worknote.ac.AViewModel(app) {

//    private val _fileList: MutableLiveData<MutableList<File>> = MutableLiveData(mutableListOf())
//    val fileList: LiveData<MutableList<File>>
//        get() = _fileList

    private val _imageInfoList: MutableLiveData<MutableList<ImageInfoEntity>> = MutableLiveData(mutableListOf())
    val imageInfoList: LiveData<MutableList<ImageInfoEntity>>
        get() = _imageInfoList

    fun calculateFileList(directoryPath: String) {
        val rawFileList = App.getAppliCation().getDFileList(directoryPath).sortedDescending().toMutableList()
        val arguments = directoryPath.split("/")
        LOG.debug("ARGUMENTS ::: ${arguments}")
        when(arguments.size) {
            3 -> {
                val platformId = arguments[1].toInt()
                val mediaType = arguments[2]
                val platformImageList = getPlatformImageInfoList(platformId, mediaType)
                calculateDiff(platformImageList, rawFileList)
            }
            4 -> {
                val platformId = arguments[1].toInt()
                val containerId = arguments[2].toInt()
                val mediaType = arguments[3]
                val platformImageList = getContainerImageInfoList(platformId, containerId, mediaType)
                calculateDiff(platformImageList, rawFileList)
            }
        }
    }

    private fun calculateDiff(rawImageList: List<ImageInfoEntity>, rawFileList: List<File>) {

        val downloadList = mutableListOf<String>()
        val pureImageList = mutableListOf<ImageInfoEntity>()

        for(imageInfo in rawImageList) {
            var isImageExist = false
            rawFileList.forEach { file ->
                if(file.nameWithoutExtension == imageInfo.md5) {
                    isImageExist = true
                    return@forEach
                }
            }

            if(isImageExist) {
                pureImageList.add(imageInfo)
            } else {
                downloadList.add(imageInfo.md5)
            }
        }

        val filesToDelete = mutableListOf<File>()

        for(file in rawFileList) {
            var isImageExist = false
            rawImageList.forEach { image ->
                if(image.md5 == file.nameWithoutExtension) {
                    isImageExist = true
                    return@forEach
                }
            }
            if(isImageExist == false) {
                filesToDelete.add(file)
            }
        }

        filesToDelete.forEach {
            it.delete()
        }

        _imageInfoList.postValue(pureImageList)
    }

    private fun downloadImages(downloadList: List<String>) {
        for(hash in downloadList) {
            // download to directory (path???) instantly
            // put to entity (path?????)
        }
    }

    private fun getContainerImageInfoList(
        platformId: Int,
        containerId: Int,
        mediaType: String
    ): List<ImageInfoEntity> {
        val result = mutableListOf<ImageInfoEntity>()

        when(mediaType) {
            PhotoTypeMapping.CONTAINER_BEFORE_MEDIA -> {

            }
            PhotoTypeMapping.BREAKDOWN_MEDIA -> {

            }
            PhotoTypeMapping.FAILURE_MEDIA -> {

            }
        }
        database.getContainerImageInfoList(platformId, containerId, mediaType)
        return result
    }

    private fun getPlatformImageInfoList(
        platformId: Int,
        mediaType: String
    ) : List<ImageInfoEntity> {
        val result = mutableListOf<ImageInfoEntity>()
        when(mediaType) {
            PhotoTypeMapping.BEFORE_MEDIA -> {

            }
            PhotoTypeMapping.AFTER_MEDIA -> {

            }
            PhotoTypeMapping.UNLOAD_BEFORE_MEDIA -> {

            }
            PhotoTypeMapping.UNLOAD_AFTER_MEDIA -> {

            }
            PhotoTypeMapping.VEHICLE_BEFORE_MEDIA -> {

            }
            PhotoTypeMapping.FAILURE_MEDIA -> {

            }
            PhotoTypeMapping.KGO_REMAINING_MEDIA -> {

            }
            PhotoTypeMapping.KGO_SERVED_MEDIA -> {

            }
            PhotoTypeMapping.PICKUP_MEDIA -> {

            }
        }
        database.getContainerImageInfoList(platformId, containerId, mediaType)
        return result
    }

    fun removeImageInfoEntityByHash(hash: String) {
        database.removeImageInfoEntityByHash(hash)
    }
}