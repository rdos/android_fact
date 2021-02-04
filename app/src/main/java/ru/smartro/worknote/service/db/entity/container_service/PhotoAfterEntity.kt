package ru.smartro.worknote.service.db.entity.container_service

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PhotoAfterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val pointID: Int,
    val photoPath: String
)
