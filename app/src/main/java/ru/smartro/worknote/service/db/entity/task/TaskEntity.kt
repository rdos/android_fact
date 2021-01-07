package ru.smartro.worknote.service.db.entity.task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)
