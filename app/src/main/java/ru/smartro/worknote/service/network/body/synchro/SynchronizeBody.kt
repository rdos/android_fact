package ru.smartro.worknote.service.network.body.synchro

import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity

class SynchronizeBody(
    val wb_id: Int,
    val lat: Double?,
    val lon: Double?,
    val device : String,
    val data: List<PlatformEntity>?
)