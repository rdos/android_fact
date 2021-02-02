package ru.smartro.worknote.service.db.entity.container_info

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WayPointEntity(
    @PrimaryKey
    val id: Int,
    val address: String,
    val name: String,
    val srpId: Int,
    val status : Boolean
)