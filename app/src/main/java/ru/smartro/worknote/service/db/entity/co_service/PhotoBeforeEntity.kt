package ru.smartro.worknote.service.db.entity.co_service

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PhotoBeforeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val pointID: Int,
    val photo: String
)