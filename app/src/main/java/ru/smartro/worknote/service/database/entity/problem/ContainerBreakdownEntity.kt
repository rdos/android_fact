package ru.smartro.worknote.service.database.entity.problem

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ContainerBreakdownEntity(
    @PrimaryKey
    var id: Int = 0,
    var problem: String? = null
) : RealmObject()
