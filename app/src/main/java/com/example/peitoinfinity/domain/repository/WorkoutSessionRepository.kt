package com.example.peitoinfinity.domain.repository

import com.example.peitoinfinity.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutSessionRepository {
    fun getActiveSession(): Flow<WorkoutSession?>
    suspend fun getActiveSessionSync(): WorkoutSession?
    fun getAllSessions(): Flow<List<WorkoutSession>>
    fun getSessionsForWeek(startMillis: Long, endMillis: Long): Flow<List<WorkoutSession>>
    suspend fun startSession(planDayId: Long): Long
    suspend fun finishSession(sessionId: Long, notes: String? = null)
    suspend fun getSessionById(sessionId: Long): WorkoutSession?
    suspend fun getSessionsForWeekSync(startMillis: Long, endMillis: Long): List<WorkoutSession>
}
