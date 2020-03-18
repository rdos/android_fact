package ru.smartro.worknote.domain.models.complex

import androidx.room.Relation

class WorkOrderWithRelations(
    val srpId: Int,
    @Relation(parentColumn = "id", entityColumn = "userId")
    val platforms: List<SrpPlatformWithRelations>
)