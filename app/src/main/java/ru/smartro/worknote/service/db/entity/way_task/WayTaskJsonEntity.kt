package ru.smartro.worknote.service.db.entity.way_task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WayTaskJsonEntity(
    @PrimaryKey
    val userLogin: String,
    val wayTaskJson: String
)