package com.example.peitoinfinity.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.peitoinfinity.data.local.database.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Insert
    suspend fun startSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions WHERE finishedAt IS NULL LIMIT 1")
    fun getActiveSession(): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE finishedAt IS NULL LIMIT 1")
    suspend fun getActiveSessionSync(): WorkoutSessionEntity?

    @Query("""
        SELECT * FROM workout_sessions 
        WHERE startedAt BETWEEN :startOfWeek AND :endOfWeek
        ORDER BY startedAt
    """)
    fun getSessionsForWeek(startOfWeek: Long, endOfWeek: Long): Flow<List<WorkoutSessionEntity>>

    @Query("""
        SELECT * FROM workout_sessions 
        WHERE startedAt BETWEEN :startOfWeek AND :endOfWeek
        ORDER BY startedAt
    """)
    suspend fun getSessionsForWeekSync(startOfWeek: Long, endOfWeek: Long): List<WorkoutSessionEntity>

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): WorkoutSessionEntity?
}
