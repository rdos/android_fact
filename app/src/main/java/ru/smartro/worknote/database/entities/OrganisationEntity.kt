package ru.smartro.worknote.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.smartro.worknote.domain.models.OrganisationModel

@Entity(tableName = "organisations")
data class OrganisationEntity constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)

fun OrganisationEntity.toDomainModel(): OrganisationModel {
    return OrganisationModel(id = id, name = name)
}

fun List<OrganisationEntity>.toDomainModel() : List<OrganisationModel>
{
    return this.map {
        it.toDomainModel()
    }
}
