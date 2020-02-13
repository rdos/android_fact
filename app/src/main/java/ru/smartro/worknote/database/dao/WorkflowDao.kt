package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.WorkflowEntity

@Dao
interface WorkflowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workflowEntity: WorkflowEntity)

    @Update
    fun update(workflowEntity: WorkflowEntity)


    @Query("SELECT * FROM workflow WHERE user_id = :userId LIMIT 1")
    fun getByUserId(userId: Int): WorkflowEntity?
}