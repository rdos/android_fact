package ru.smartro.worknote.domain.models.complex

import android.location.Location

class SrpPlatformWithRelations(
    val srpId: Int,
    val address: String,
    val kgoNorma: Int?,
    val name: String,
    val location: Location,
    val containers: List<SrpContainerWithRelations>
)