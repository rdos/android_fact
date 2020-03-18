package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.WayBillBodyEntity

@Dao
interface WayBillBodyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wayBillBodyEntity: WayBillBodyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(wayBillBodyEntity: List<WayBillBodyEntity>)

    @Update
    fun update(wayBillBodyEntity: WayBillBodyEntity)

    @Query("SELECT * FROM way_bill_bodies WHERE srp_id = :key LIMIT 1")
    fun get(key: Int): WayBillBodyEntity
}