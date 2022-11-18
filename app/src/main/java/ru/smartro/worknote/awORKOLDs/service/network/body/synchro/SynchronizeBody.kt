package ru.smartro.worknote.awORKOLDs.service.network.body.synchro

import com.google.gson.annotations.Expose
import ru.smartro.worknote.presentation.work.PlatformEntity

class SynchronizeBody(
    @Expose
    val wb_id: Int,
    @Expose
    val coords: List<Double>,
    @Expose
    val device: String,
    @Expose
    val lastKnownLocationTime: Long,
    @Expose
    val data: List<PlatformEntity>?
)