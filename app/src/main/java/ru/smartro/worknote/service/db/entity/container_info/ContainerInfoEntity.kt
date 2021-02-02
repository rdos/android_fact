package ru.smartro.worknote.service.db.entity.container_info

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ContainerInfoEntity(
    @PrimaryKey (autoGenerate = true)
    val id : Int,
    val containerId: Int,
    val wo_id: Int,
    val volume: Int,
    val comment: String,
    val o_id: Int,
    val wayPointId : Int
)