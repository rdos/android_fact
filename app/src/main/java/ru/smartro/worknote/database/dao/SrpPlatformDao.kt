package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.SrpPlatformEntity
import ru.smartro.worknote.ui.workFlow.showSrpPlatform.PlatformToShow

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


    @Query(
        """SELECT
                    srp_platforms.name as name
                    , srp_platforms.address as address
                    , count(srp_containers.srp_point_details_id) as containersCount
                FROM
                    srp_platforms
                    LEFT JOIN srp_containers ON srp_containers.platform_srp_id = srp_platforms.srp_id
                WHERE
                    work_order_srp_id = :workOrderId
                GROUP BY
                    srp_platforms.name, srp_platforms.address"""
    )
    fun getWithContainerCount(workOrderId: Int): List<PlatformToShow>
}