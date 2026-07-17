package com.example.peitoinfinity.data.repository

import com.example.peitoinfinity.data.local.database.dao.PlanDayDao
import com.example.peitoinfinity.data.local.database.dao.PlanExerciseDao
import com.example.peitoinfinity.data.local.database.dao.TrainingPlanDao
import com.example.peitoinfinity.data.local.database.entity.PlanDayEntity
import com.example.peitoinfinity.data.local.database.entity.PlanExerciseEntity
import com.example.peitoinfinity.data.local.database.entity.TrainingPlanEntity
import com.example.peitoinfinity.domain.model.*
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class TrainingPlanRepositoryImpl @Inject constructor(
    private val trainingPlanDao: TrainingPlanDao,
    private val planDayDao: PlanDayDao,
    private val planExerciseDao: PlanExerciseDao
) : TrainingPlanRepository {

    override fun getActivePlan(): Flow<TrainingPlan?> {
        return trainingPlanDao.getActivePlan().flatMapLatest { planEntity ->
            if (planEntity == null) flowOf(null)
            else {
                getDaysForPlan(planEntity.id).map { days ->
                    planEntity.toDomain(days)
                }
            }
        }
    }

    override fun getAllPlans(): Flow<List<TrainingPlan>> {
        return trainingPlanDao.getAllPlans().flatMapLatest { planEntities ->
            if (planEntities.isEmpty()) flowOf(emptyList())
            else {
                val planFlows = planEntities.map { planEntity ->
                    getDaysForPlan(planEntity.id).map { days ->
                        planEntity.toDomain(days)
                    }
                }
                combine(planFlows) { it.toList() }
            }
        }
    }

    override fun getDaysForPlan(planId: Long): Flow<List<PlanDay>> {
        return planDayDao.getDaysForPlan(planId).flatMapLatest { dayEntities ->
            if (dayEntities.isEmpty()) flowOf(emptyList())
            else {
                val dayFlows = dayEntities.map { dayEntity ->
                    getExercisesForDay(dayEntity.id).map { exercises ->
                        dayEntity.toDomain(exercises)
                    }
                }
                combine(dayFlows) { it.toList() }
            }
        }
    }

    override fun getExercisesForDay(dayId: Long): Flow<List<PlanExercise>> {
        return planExerciseDao.getExercisesForDay(dayId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getExercisesForDaySync(dayId: Long): List<PlanExercise> {
        return planExerciseDao.getExercisesForDaySync(dayId).map { it.toDomain() }
    }

    override suspend fun insertAndActivate(plan: TrainingPlanEntity): Long {
        return trainingPlanDao.insertAndActivatePlan(plan)
    }

    override suspend fun insertDay(day: PlanDayEntity): Long {
        return planDayDao.insertDay(day)
    }

    override suspend fun insertExercises(exercises: List<PlanExerciseEntity>) {
        planExerciseDao.insertExercises(exercises)
    }

    override suspend fun deleteExercisesForDay(dayId: Long) {
        planExerciseDao.deleteExercisesForDay(dayId)
    }

    override suspend fun deletePlan(planId: Long) {
        val entity = trainingPlanDao.getAllPlans().first().firstOrNull { it.id == planId }
        if (entity != null) {
            trainingPlanDao.deletePlan(entity)
        }
    }

    override suspend fun getPlanById(planId: Long): TrainingPlan {
        val entity = trainingPlanDao.getAllPlans().first().firstOrNull { it.id == planId }
            ?: throw Exception("Plano não encontrado")
        val days = getDaysForPlan(planId).first()
        return entity.toDomain(days)
    }

    override suspend fun getDayById(dayId: Long): PlanDay? {
        val dayEntity = planDayDao.getDayById(dayId) ?: return null
        val exercises = getExercisesForDaySync(dayId)
        return dayEntity.toDomain(exercises)
    }

    override suspend fun getAverageRestForDays(dayIds: List<Long>): Int {
        return planExerciseDao.getAverageRestForDays(dayIds)
    }
}

// Mappers
fun TrainingPlanEntity.toDomain(days: List<PlanDay> = emptyList()) = TrainingPlan(
    id = id,
    name = name,
    isActive = isActive,
    trainingGoal = TrainingGoal.valueOf(trainingGoal),
    trainingLevel = TrainingLevel.valueOf(trainingLevel),
    daysPerWeek = daysPerWeek,
    createdAt = createdAt,
    days = days
)

fun PlanDayEntity.toDomain(exercises: List<PlanExercise> = emptyList()) = PlanDay(
    id = id,
    planId = planId,
    dayNumber = dayNumber,
    dayName = dayName,
    focusMuscleGroups = Json.decodeFromString<List<String>>(focusMuscleGroups)
        .mapNotNull { runCatching { MuscleGroup.valueOf(it) }.getOrNull() },
    estimatedDurationMinutes = estimatedDurationMinutes,
    exercises = exercises
)

fun PlanExerciseEntity.toDomain() = PlanExercise(
    id = id,
    planDayId = planDayId,
    exerciseId = exerciseId,
    exercise = null,
    orderIndex = orderIndex,
    sets = sets,
    reps = reps,
    restSeconds = restSeconds,
    notes = notes
)
