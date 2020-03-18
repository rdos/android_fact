package ru.smartro.worknote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.smartro.worknote.database.Converters.DateConverter
import ru.smartro.worknote.database.dao.*
import ru.smartro.worknote.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        OrganisationEntity::class,
        VehicleEntity::class,
        WorkflowEntity::class,
        WayBillHeadEntity::class,
        SrpContainerEntity::class,
        SrpContainerTypeEntity::class,
        SrpPlatformEntity::class,
        WayBillBodyEntity::class,
        WorkOrderEntity::class
    ], version = 1
)
@TypeConverters(DateConverter::class)
abstract class DataBase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val organisationDao: OrganisationDao
    abstract val vehicleDao: VehicleDao
    abstract val workflowDao: WorkflowDao
    abstract val wayBillHeadDao: WayBillHeadDao
    abstract val srpContainerDao: SrpContainerDao
    abstract val srpContainerTypeDao: SrpContainerTypeDao
    abstract val srpPlatformDao: SrpPlatformDao
    abstract val wayBillBodyDAO: WayBillBodyDAO
    abstract val workOrderDAO: WorkOrderDAO
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