package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.SrpContainerEntity

@Dao
interface SrpContainerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(srpContainerEntity: SrpContainerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(srpContainers: List<SrpContainerEntity>)

    @Update
    fun update(srpContainers: SrpContainerEntity)

    @Query("SELECT * FROM srp_containers WHERE srp_point_details_id = :key LIMIT 1")
    fun get(key: Int): SrpContainerEntity

    @Query("SELECT * FROM srp_containers WHERE platform_srp_id = :platformId")
    fun getByPlatform(platformId: Int): List<SrpContainerEntity>
}