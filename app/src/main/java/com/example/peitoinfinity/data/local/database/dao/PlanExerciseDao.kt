package com.example.peitoinfinity.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.peitoinfinity.data.local.database.entity.PlanExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanExerciseDao {
    @Query("SELECT * FROM plan_exercises WHERE planDayId = :dayId ORDER BY orderIndex")
    fun getExercisesForDay(dayId: Long): Flow<List<PlanExerciseEntity>>

    @Query("SELECT * FROM plan_exercises WHERE planDayId = :dayId ORDER BY orderIndex")
    suspend fun getExercisesForDaySync(dayId: Long): List<PlanExerciseEntity>

    @Insert
    suspend fun insertExercises(exercises: List<PlanExerciseEntity>)

    @Query("DELETE FROM plan_exercises WHERE planDayId = :dayId")
    suspend fun deleteExercisesForDay(dayId: Long)

    @Query("SELECT COALESCE(AVG(restSeconds), 0) FROM plan_exercises WHERE planDayId IN (:dayIds)")
    suspend fun getAverageRestForDays(dayIds: List<Long>): Int
}
