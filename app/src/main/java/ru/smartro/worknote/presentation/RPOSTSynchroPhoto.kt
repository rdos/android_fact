package ru.smartro.worknote.presentation

import com.google.gson.annotations.Expose
import io.realm.Realm
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.work.work.RealmRepository
import ru.smartro.worknote.presentation.ac.AbsRequest
import ru.smartro.worknote.presentation.ac.NetObject
import ru.smartro.worknote.todo
import java.io.File
import kotlin.reflect.KClass

class RPOSTSynchroPhoto(
    private val imageInfoEntity: ImageInfoEntity
): AbsRequest<SynchroPhotoBodyIn, SynchroPhotoBodyOut>() {

    override fun onGetSRVName(): String {
        return "synchro"
    }

    override fun onGetMultipartBody(): MultipartBody {
        LOG.todo("db.before")
        val db = RealmRepository(Realm.getDefaultInstance())
        LOG.todo("db.after")

        var dirPath = C_PHOTO_D + File.separator + imageInfoEntity.platformId + File.separator
        if(imageInfoEntity.isContainer())
            dirPath += (imageInfoEntity.containerId.toString() + File.separator)
        dirPath += imageInfoEntity.mediaType

        val fileName = "${imageInfoEntity.md5}.webp"
        val file = App.getAppliCation().getF(dirPath, fileName)

        val mediaType = "image/*".toMediaTypeOrNull()

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("organisation_id", imageInfoEntity.organisationId.toString())
            .addFormDataPart("image_file", fileName, file.asRequestBody(mediaType))
            .build()

        db.updateImageInfoEntityAttempt(imageInfoEntity)

        return body
    }

    override fun onAfter(bodyOut: SynchroPhotoBodyOut) {
        LOG.todo("db.before")
        val db = RealmRepository(Realm.getDefaultInstance())
        LOG.todo("db.after")

        db.updateImageInfoEntitySynchro(imageInfoEntity)
    }


    // todo::: ФИГНЯ
    override fun onGetRequestBodyIn(): SynchroPhotoBodyIn {
        return SynchroPhotoBodyIn(1, "")
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {
        
    }

    override fun onBefore() {
        
    }

    override fun onGetResponseClazz(): KClass<SynchroPhotoBodyOut> {
        return SynchroPhotoBodyOut::class
    }

}

class SynchroPhotoBodyIn(
    @Expose
    val organisationId: Int,
    @Expose
    val imagePath: String
): NetObject()

class SynchroPhotoBodyOut(
    @Expose
    val success: Boolean,
    @Expose
    val hash: String
): NetObject()
