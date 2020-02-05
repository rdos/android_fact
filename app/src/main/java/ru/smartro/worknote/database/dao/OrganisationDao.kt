package ru.smartro.worknote.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.smartro.worknote.database.entities.OrganisationEntity

@Dao
interface OrganisationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(organisationEntity: OrganisationEntity)

    @Query("SELECT * FROM organisations WHERE id IN (:keys)")
    fun getAllByUserId(keys : List<Int>): List<OrganisationEntity>
}