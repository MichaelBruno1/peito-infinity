package com.example.peitoinfinity.data.repository

import com.example.peitoinfinity.data.local.database.dao.ExerciseLogDao
import com.example.peitoinfinity.data.local.database.entity.ExerciseLogEntity
import com.example.peitoinfinity.domain.model.ExerciseLog
import com.example.peitoinfinity.domain.repository.ExerciseLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseLogRepositoryImpl @Inject constructor(
    private val exerciseLogDao: ExerciseLogDao
) : ExerciseLogRepository {

    override fun getLogsForSession(sessionId: Long): Flow<List<ExerciseLog>> {
        return exerciseLogDao.getLogsForSession(sessionId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getLogsForExercise(exerciseId: String): Flow<List<ExerciseLog>> {
        return exerciseLogDao.getLogsForExercise(exerciseId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertLog(log: ExerciseLog): Long {
        return exerciseLogDao.insertLog(log.toEntity())
    }

    override suspend fun updateLog(log: ExerciseLog) {
        exerciseLogDao.updateLog(log.toEntity())
    }

    override suspend fun deleteLog(log: ExerciseLog) {
        exerciseLogDao.deleteLog(log.toEntity())
    }

    override suspend fun getTotalWeightForWeek(startMillis: Long, endMillis: Long): Float {
        return exerciseLogDao.getTotalWeightForWeek(startMillis, endMillis)
    }

    override suspend fun getTotalDistanceForWeek(startMillis: Long, endMillis: Long): Float {
        return exerciseLogDao.getTotalDistanceForWeek(startMillis, endMillis)
    }

    override suspend fun getAverageSpeedForWeek(startMillis: Long, endMillis: Long): Float {
        return exerciseLogDao.getAverageSpeedForWeek(startMillis, endMillis)
    }
}

// Mappers
fun ExerciseLogEntity.toDomain() = ExerciseLog(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    setNumber = setNumber,
    weightKg = weightKg,
    reps = reps,
    durationSeconds = durationSeconds,
    distanceMeters = distanceMeters,
    speedKmh = speedKmh,
    completedAt = completedAt
)

fun ExerciseLog.toEntity() = ExerciseLogEntity(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    setNumber = setNumber,
    weightKg = weightKg,
    reps = reps,
    durationSeconds = durationSeconds,
    distanceMeters = distanceMeters,
    speedKmh = speedKmh,
    completedAt = completedAt
)
