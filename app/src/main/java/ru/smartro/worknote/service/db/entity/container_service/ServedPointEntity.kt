package ru.smartro.worknote.service.db.entity.container_service

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ServedPointEntity(
    @PrimaryKey
    var pId: Int? = null,
    var beginnedAt: Long? = null,
    var co: RealmList<Double>? = null,
    var cs: RealmList<ServedContainerInfoEntity>? = null,
    var finishedAt: Long? = null,
    var mediaAfter: RealmList<String>? = null,
    var mediaBefore: RealmList<String>? = null,
    var oid: Int? = null,
    var woId: Int? = null
) : RealmObject()

open class ServedContainerInfoEntity(
    var cId: Int? = null,
    var comment: String? = null,
    var oid: Int? = null,
    var volume: Double? = null,
    var woId: Int? = null
) : RealmObject()