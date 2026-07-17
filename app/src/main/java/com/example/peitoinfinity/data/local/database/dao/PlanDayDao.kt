package com.example.peitoinfinity.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import com.example.peitoinfinity.data.local.database.entity.PlanDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDayDao {
    @Query("SELECT * FROM plan_days WHERE planId = :planId ORDER BY dayNumber")
    fun getDaysForPlan(planId: Long): Flow<List<PlanDayEntity>>

    @Query("SELECT * FROM plan_days WHERE id = :dayId")
    suspend fun getDayById(dayId: Long): PlanDayEntity?

    @Insert
    suspend fun insertDays(days: List<PlanDayEntity>): List<Long>

    @Insert
    suspend fun insertDay(day: PlanDayEntity): Long

    @Delete
    suspend fun deleteDay(day: PlanDayEntity)

    @Query("DELETE FROM plan_days WHERE planId = :planId AND id = :dayId")
    suspend fun deleteDayById(planId: Long, dayId: Long)
}
