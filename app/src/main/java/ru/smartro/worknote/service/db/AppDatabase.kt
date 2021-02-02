package ru.smartro.worknote.service.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.smartro.worknote.service.db.entity.co_service.PhotoAfterEntity
import ru.smartro.worknote.service.db.entity.co_service.PhotoBeforeEntity
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.db.entity.container_info.WayPointEntity

import ru.smartro.worknote.service.db.entity.task.TaskEntity
import ru.smartro.worknote.service.db.entity.way_task.WayTaskJsonEntity


@Database(
    entities = [
        TaskEntity::class,
        WayTaskJsonEntity::class,
        PhotoBeforeEntity::class,
        PhotoAfterEntity::class,
        ContainerInfoEntity::class,
        WayPointEntity::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dbDao(): RoomDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun instance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fact"
                ).build()
                Companion.instance = instance
                instance
            }

        }
    }
}