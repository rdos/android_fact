package ru.smartro.worknote.domain.models.complex

import ru.smartro.worknote.domain.models.SrpContainerTypeModel

class SrpContainerWithRelations(
    val srpPointDetailsId: Int,
    val invNumber: String?,
    val isActive: Boolean,
    val srpType: SrpContainerTypeModel
)