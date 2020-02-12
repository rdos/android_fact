package ru.smartro.worknote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.smartro.worknote.database.dao.OrganisationDao
import ru.smartro.worknote.database.dao.UserDao
import ru.smartro.worknote.database.dao.VehicleDao
import ru.smartro.worknote.database.entities.OrganisationEntity
import ru.smartro.worknote.database.entities.UserEntity
import ru.smartro.worknote.database.entities.VehicleEntity

@Database(
    entities = [
        UserEntity::class,
        OrganisationEntity::class,
        VehicleEntity::class
    ], version = 1
)
abstract class DataBase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val organisationDao: OrganisationDao
    abstract val vehicleDao: VehicleDao
}


private lateinit var INSTANCE: DataBase

fun getDatabase(context: Context): DataBase {
    synchronized(DataBase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                DataBase::class.java,
                "database"
            ).build()
        }
    }
    return INSTANCE
}