package com.example.peitoinfinity.domain.repository

import com.example.peitoinfinity.domain.model.ExerciseLog
import kotlinx.coroutines.flow.Flow

interface ExerciseLogRepository {
    fun getLogsForSession(sessionId: Long): Flow<List<ExerciseLog>>
    fun getLogsForExercise(exerciseId: String): Flow<List<ExerciseLog>>
    suspend fun insertLog(log: ExerciseLog): Long
    suspend fun updateLog(log: ExerciseLog)
    suspend fun deleteLog(log: ExerciseLog)
    suspend fun getTotalWeightForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getTotalDistanceForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getAverageSpeedForWeek(startMillis: Long, endMillis: Long): Float
}
