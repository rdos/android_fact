package ru.smartro.worknote.log.awORKOLDs.service.database.entity.problem

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BreakDownReasonEntity(
    @PrimaryKey
    var id: Int = 0,
    var problem: String? = null
) : RealmObject()
