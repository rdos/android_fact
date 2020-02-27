package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.Converters.DateConverter
import ru.smartro.worknote.database.entities.WayBillHeadEntity
import java.time.LocalDate

@Dao
@TypeConverters(DateConverter::class)
interface WayBillHeadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wayBillHeadEntity: WayBillHeadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<WayBillHeadEntity>)

    @Query("SELECT * FROM way_bill_heads WHERE organisation_id = :organisationId and date = :date")
    fun getAllByDataAndOrganisationId(organisationId: Int, date: LocalDate): List<WayBillHeadEntity>

    @Query("SELECT * FROM way_bill_heads WHERE id = :key LIMIT 1")
    fun getById(key: Int): WayBillHeadEntity?

    @Query("SELECT * FROM way_bill_heads WHERE number = :number AND organisation_id = :organisationId LIMIT 1")
    fun getByNumberAndOrganisation(number: Int, organisationId: Int): WayBillHeadEntity?
}