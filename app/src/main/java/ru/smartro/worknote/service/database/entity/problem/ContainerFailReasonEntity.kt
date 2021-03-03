package ru.smartro.worknote.service.database.entity.problem

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ContainerFailReasonEntity(
    @PrimaryKey
    var id: Int = 0,
    var reason: String? = null
) : RealmObject()