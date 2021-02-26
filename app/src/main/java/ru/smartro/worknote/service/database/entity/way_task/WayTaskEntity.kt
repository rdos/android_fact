package ru.smartro.worknote.service.database.entity.way_task


import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class WayTaskEntity(
    /** ("id")**/
    @PrimaryKey
    var id: Int? = null,
    /**("accounting")**/
    var accounting: Int? = null,
    /** ("beginned_at")**/
    var beginned_at: String? = null,
    /** ("finished_at")**/
    var finished_at: String? = null,
    /**  ("name")**/
    var name: String? = null,
    /**  ("p")**/
    var p: RealmList<WayPointEntity>? = null,
    /**  ("start")**/
    var start: WayStartPointEntity? = null,
    /**  ("unload")**/
    var unload: WayUnloadPointEntity? = null
) : Serializable, RealmObject()

open class WayStartPointEntity(
    /** ("co")**/
    var co: RealmList<Double>? = null,
    /**  ("name")**/
    var name: String? = null
) : Serializable, RealmObject()

open class WayPointEntity(
    /**  ("id")**/
    var id: Int? = null,
    /** ("address")**/
    var address: String? = null,
    /**  ("co")**/
    var co: RealmList<Double>? = null,
    /** ("name")**/
    var name: String? = null,
    /**  ("cs")**/
    var cs: RealmList<ContainerInfoEntity>? = null,
    /**  ("srp_id")**/
    var srp_id: Int? = null,
    var isComplete: Boolean = false
) : Serializable, RealmObject()

open class WayUnloadPointEntity(
    /** ("co")**/
    var co: RealmList<Double>? = null,
    /** ("name")**/
    var name: String? = null
) : Serializable, RealmObject()

open class ContainerInfoEntity(
    /**("id")**/
    var id: Int? = null,
    /** ("client")**/
    var client: String? = null,
    /** ("contacts")**/
    var contacts: String? = null,
    /**  ("is_active")**/
    var is_active: Int? = null,
    /** ("number")**/
    var number: String? = null,
    /**("type_id")**/
    var type_id: Int? = null,
    var status: Int = 0
) : Serializable, RealmObject()