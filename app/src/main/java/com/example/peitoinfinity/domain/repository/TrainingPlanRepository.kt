package com.example.peitoinfinity.domain.repository

import com.example.peitoinfinity.data.local.database.entity.PlanDayEntity
import com.example.peitoinfinity.data.local.database.entity.PlanExerciseEntity
import com.example.peitoinfinity.data.local.database.entity.TrainingPlanEntity
import com.example.peitoinfinity.domain.model.PlanDay
import com.example.peitoinfinity.domain.model.PlanExercise
import com.example.peitoinfinity.domain.model.TrainingPlan
import kotlinx.coroutines.flow.Flow

interface TrainingPlanRepository {
    fun getActivePlan(): Flow<TrainingPlan?>
    fun getAllPlans(): Flow<List<TrainingPlan>>
    fun getDaysForPlan(planId: Long): Flow<List<PlanDay>>
    fun getExercisesForDay(dayId: Long): Flow<List<PlanExercise>>
    suspend fun getExercisesForDaySync(dayId: Long): List<PlanExercise>
    suspend fun insertAndActivate(plan: TrainingPlanEntity): Long
    suspend fun insertDay(day: PlanDayEntity): Long
    suspend fun insertExercises(exercises: List<PlanExerciseEntity>)
    suspend fun deleteExercisesForDay(dayId: Long)
    suspend fun deletePlan(planId: Long)
    suspend fun getPlanById(planId: Long): TrainingPlan
    suspend fun getDayById(dayId: Long): PlanDay?
    suspend fun getAverageRestForDays(dayIds: List<Long>): Int
}
