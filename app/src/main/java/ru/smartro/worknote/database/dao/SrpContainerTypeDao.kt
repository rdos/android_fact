package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.SrpContainerTypeEntity

@Dao
interface SrpContainerTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(srpContainerTypeEntity: SrpContainerTypeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(srpContainerTypes: List<SrpContainerTypeEntity>)

    @Update
    fun update(srpContainerType: SrpContainerTypeEntity)

    @Query("SELECT * FROM srp_container_types WHERE srp_id = :key LIMIT 1")
    fun get(key: Int): SrpContainerTypeEntity
}