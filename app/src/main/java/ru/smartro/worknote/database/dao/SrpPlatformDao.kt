package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.SrpPlatformEntity

@Dao
interface SrpPlatformDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(srpPlatformEntity: SrpPlatformEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(srpPlatformEntity: List<SrpPlatformEntity>)

    @Update
    fun update(srpPlatformEntity: SrpPlatformEntity)

    @Query("SELECT * FROM srp_platforms WHERE srp_id = :key LIMIT 1")
    fun get(key: Int): SrpPlatformEntity


    @Query("SELECT * FROM srp_platforms WHERE work_order_srp_id = :workOrderId")
    fun getBayWorkOrder(workOrderId: Int): List<SrpPlatformEntity>
}