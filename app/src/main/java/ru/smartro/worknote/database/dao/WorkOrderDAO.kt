package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.WorkOrderEntity

@Dao
interface WorkOrderDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workOrderEntity: WorkOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(workOrderEntity: List<WorkOrderEntity>)

    @Update
    fun update(workOrderEntity: WorkOrderEntity)

    @Query("SELECT * FROM work_orders WHERE srp_id = :key LIMIT 1")
    fun get(key: Int): WorkOrderEntity

    @Query("SELECT * FROM work_orders WHERE way_bill_srp_id = :wayBillSrpId")
    fun getByWayBillId(wayBillSrpId: Int): List<WorkOrderEntity>
}