package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.VehicleEntity

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicleEntity: VehicleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vehicleEntities: List<VehicleEntity>)

    @Update
    fun update(vehicleEntity: VehicleEntity)

    @Query("SELECT * FROM vehicles WHERE id = :key LIMIT 1")
    fun get(key: Int): VehicleEntity?

    @Query("DELETE FROM vehicles WHERE organisation_id = :organisationId")
    fun clearByOrganisationId(organisationId: Int)

    @Query("SELECT * FROM vehicles WHERE organisation_id = :organisationId")
    fun getByOrganisationId(organisationId: Int): List<VehicleEntity>

    @Transaction
    fun refreshInOrganisation(organisationId: Int, vehicleEntities: List<VehicleEntity>) {
        when (vehicleEntities.indexOfFirst { vehicleEntity: VehicleEntity ->
            vehicleEntity.organisationId != organisationId
        }) {
            -1 -> Unit
            else -> throw Exception("all vehicles must have $organisationId organisation id")
        }
        clearByOrganisationId(organisationId)
        insertAll(vehicleEntities)
    }
}