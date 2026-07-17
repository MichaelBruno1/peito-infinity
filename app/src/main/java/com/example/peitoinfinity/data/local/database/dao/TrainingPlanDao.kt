package com.example.peitoinfinity.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import com.example.peitoinfinity.data.local.database.entity.TrainingPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingPlanDao {
    @Query("SELECT * FROM training_plans WHERE isActive = 1 LIMIT 1")
    fun getActivePlan(): Flow<TrainingPlanEntity?>

    @Query("SELECT * FROM training_plans ORDER BY createdAt DESC")
    fun getAllPlans(): Flow<List<TrainingPlanEntity>>

    @Insert
    suspend fun insertPlan(plan: TrainingPlanEntity): Long

    @Update
    suspend fun updatePlan(plan: TrainingPlanEntity)

    @Query("UPDATE training_plans SET isActive = 0")
    suspend fun deactivateAllPlans()

    @Delete
    suspend fun deletePlan(plan: TrainingPlanEntity)

    @Transaction
    suspend fun insertAndActivatePlan(plan: TrainingPlanEntity): Long {
        deactivateAllPlans()
        return insertPlan(plan.copy(isActive = true))
    }
}
