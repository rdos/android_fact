package ru.smartro.worknote.workold.service.network.body.synchro

import ru.smartro.worknote.work.PlatformEntity

class SynchronizeBody(
    val wb_id: Int,
    val coords: List<Double>,
    val device: String,
    val lastKnownLocationTime: Long,
    val data: List<PlatformEntity>?
)