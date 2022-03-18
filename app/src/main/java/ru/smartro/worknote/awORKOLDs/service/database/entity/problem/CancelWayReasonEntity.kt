package ru.smartro.worknote.awORKOLDs.service.database.entity.problem

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CancelWayReasonEntity(
    @PrimaryKey
    var id: Int = 0,
    var problem: String? = null
) : RealmObject()
