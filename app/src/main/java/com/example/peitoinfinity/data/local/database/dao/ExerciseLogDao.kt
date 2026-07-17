package com.example.peitoinfinity.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.peitoinfinity.data.local.database.entity.ExerciseLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseLogDao {
    @Insert
    suspend fun insertLog(log: ExerciseLogEntity): Long

    @Update
    suspend fun updateLog(log: ExerciseLogEntity)

    @Delete
    suspend fun deleteLog(log: ExerciseLogEntity)

    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY completedAt")
    fun getLogsForSession(sessionId: Long): Flow<List<ExerciseLogEntity>>

    @Query("""
        SELECT * FROM exercise_logs 
        WHERE exerciseId = :exerciseId 
        ORDER BY completedAt DESC
    """)
    fun getLogsForExercise(exerciseId: String): Flow<List<ExerciseLogEntity>>

    @Query("""
        SELECT COALESCE(SUM(el.weightKg * el.reps), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.weightKg IS NOT NULL AND el.reps IS NOT NULL
    """)
    suspend fun getTotalWeightForWeek(startOfWeek: Long, endOfWeek: Long): Float

    @Query("""
        SELECT COALESCE(SUM(el.distanceMeters), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.distanceMeters IS NOT NULL
    """)
    suspend fun getTotalDistanceForWeek(startOfWeek: Long, endOfWeek: Long): Float

    @Query("""
        SELECT COALESCE(AVG(el.speedKmh), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.speedKmh IS NOT NULL
    """)
    suspend fun getAverageSpeedForWeek(startOfWeek: Long, endOfWeek: Long): Float

    @Query("""
        SELECT COALESCE(SUM(el.durationSeconds), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.durationSeconds IS NOT NULL
    """)
    suspend fun getTotalCardioTimeForWeek(startOfWeek: Long, endOfWeek: Long): Int
}
