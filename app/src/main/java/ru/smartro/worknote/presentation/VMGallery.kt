package ru.smartro.worknote.presentation

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import ru.smartro.worknote.App
import ru.smartro.worknote.App.Companion.PhotoTypeMapping
import ru.smartro.worknote.LOG
import ru.smartro.worknote.Snull
import ru.smartro.worknote.log.todo.*
import java.io.File

class VMGallery(val app: Application) : ru.smartro.worknote.ac.AViewModel(app) {

    private val _imageInfoList: MutableLiveData<MutableList<ImageInfoEntity>> = MutableLiveData(mutableListOf())
    val imageInfoList: LiveData<MutableList<ImageInfoEntity>>
        get() = _imageInfoList

    private var mDirectoryPath: String? = null

    private var mPlatformId: Int? = null
    private var mContainerId: Int? = null
    private var mMediaType: String? = null
    fun mIsContainer() = mContainerId != null

    private var platform: PlatformEntity? = null

    fun calculateFileList(directoryPath: String, platformEntity: PlatformEntity) {
        mDirectoryPath = directoryPath
        val rawFileList = App.getAppliCation().getDFileList(directoryPath).sortedDescending().toMutableList()
        val arguments = directoryPath.split("/")
        LOG.debug("ARGUMENTS ::: ${arguments}")


        // TODO::: WTF MOMENT)
        if(arguments.size > 2) {
            if(platformEntity.platformId == arguments[1].toInt()) {
                platform = platformEntity
            } else {
                platform = database.getPlatformEntity(arguments[1].toInt())
            }
        }

        var imageListToDiff = listOf<ImageInfoEntity>()

        when(arguments.size) {
            3 -> {
                mPlatformId = arguments[1].toInt()
                val mediaType = arguments[2]
                LOG.debug("THATS PLATFORM ::: ${mediaType}")
                mMediaType = mediaType
                imageListToDiff = getPlatformImageInfoList(mediaType)
            }
            4 -> {
                val containerId = arguments[2].toInt()
                mContainerId = containerId
                val mediaType = arguments[3]
                LOG.debug("THATS CONTAINER ::: ${mediaType}")
                mMediaType = mediaType
                imageListToDiff = getContainerImageInfoList(containerId, mediaType)
            }
        }

        calculateImagesToDownload(imageListToDiff, rawFileList)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun calculateImagesToDownload(rawImageList: List<ImageInfoEntity>, rawFileList: List<File>) {

        val downloadList = mutableListOf<ImageInfoEntity>()

        for(imageInfo in rawImageList) {
            var isImageExist = false
            rawFileList.forEach { file ->
                if(file.nameWithoutExtension == imageInfo.md5) {
                    isImageExist = true
                    return@forEach
                }
            }

            if(isImageExist == false) {
                downloadList.add(imageInfo)
            }
        }

        LOG.debug("downloadList=${downloadList}")

        _imageInfoList.postValue(rawImageList.toMutableList())

        GlobalScope.async(Dispatchers.IO) {

            // 3 SEC TIMER

            downloadList.forEach { imageInfoEntity ->
                if(imageInfoEntity.url != Snull) {
                    val file = App.getAppliCation().getF(C_PHOTO_D,  mDirectoryPath + File.separator + "${imageInfoEntity.md5}.webp")
                    var dm: DownloadManager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val uri = Uri.parse(imageInfoEntity.url)
                    val request = DownloadManager.Request(uri)

                    request.setDestinationUri(file.toUri())
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setTitle("Скачивание Фото ${imageInfoEntity.md5}")
                    Toast.makeText(app.applicationContext, "Начинаем скачивать фото ${imageInfoEntity.md5}", Toast.LENGTH_SHORT).show()

                    val downloadReference = dm?.enqueue(request) ?: 0
                }
            }
        }
    }

    private fun getContainerImageInfoList(
        containerId: Int,
        mediaType: String
    ): List<ImageInfoEntity> {
        val result = mutableListOf<ImageInfoEntity>()
        val container = platform?.containerS?.find { it.containerId == containerId }

        when(mediaType) {
            PhotoTypeMapping.CONTAINER_BEFORE_MEDIA -> {
                result.addAll(platform?.beforeMedia ?: listOf())
            }
            PhotoTypeMapping.BREAKDOWN_MEDIA -> {
                result.addAll(container?.breakdownMedia ?: listOf())
            }
            PhotoTypeMapping.FAILURE_MEDIA -> {
                result.addAll(container?.failureMedia ?: listOf())
            }
        }
        return result
    }

    private fun getPlatformImageInfoList(
        mediaType: String
    ) : List<ImageInfoEntity> {
        val result = mutableListOf<ImageInfoEntity>()

        when(mediaType) {
            PhotoTypeMapping.BEFORE_MEDIA -> {
                result.addAll(platform?.beforeMedia ?: listOf())
            }
            PhotoTypeMapping.AFTER_MEDIA -> {
                result.addAll(platform?.afterMedia ?: listOf())
            }
            PhotoTypeMapping.UNLOAD_BEFORE_MEDIA -> {
                result.addAll(platform?.unloadEntity?.beforeMedia ?: listOf())
            }
            PhotoTypeMapping.UNLOAD_AFTER_MEDIA -> {
                result.addAll(platform?.unloadEntity?.afterMedia ?: listOf())
            }
            PhotoTypeMapping.VEHICLE_BEFORE_MEDIA -> {
                // TODO :::
                result.addAll(listOf())
            }
            PhotoTypeMapping.FAILURE_MEDIA -> {
                result.addAll(platform?.failureMedia ?: listOf())
            }
            PhotoTypeMapping.KGO_REMAINING_MEDIA -> {
                result.addAll(platform?.kgoRemaining?.media ?: listOf())
            }
            PhotoTypeMapping.KGO_SERVED_MEDIA -> {
                result.addAll(platform?.kgoServed?.media ?: listOf())
            }
            PhotoTypeMapping.PICKUP_MEDIA -> {
                result.addAll(platform?.pickupMedia ?: listOf())
            }
        }
        return result
    }

    fun removeImageInfoEntityByHash(hash: String) {
        database.removeImageInfoEntityByHash(hash)
    }
}