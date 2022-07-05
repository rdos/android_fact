package ru.smartro.worknote.work.net

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CancelWayReasonEntity(
    @PrimaryKey
    var id: Int = 0,
    var problem: String? = null
) : RealmObject()
