package com.example.peitoinfinity.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.example.peitoinfinity.data.local.database.entity.ExerciseExclusionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseExclusionDao {
    @Query("SELECT * FROM exercise_exclusions")
    fun getAll(): Flow<List<ExerciseExclusionEntity>>

    @Query("SELECT * FROM exercise_exclusions WHERE isGlobal = 1")
    fun getGlobalExclusions(): Flow<List<ExerciseExclusionEntity>>

    @Query("SELECT exerciseId FROM exercise_exclusions")
    suspend fun getExcludedExerciseIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exclusion: ExerciseExclusionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exclusions: List<ExerciseExclusionEntity>)

    @Delete
    suspend fun delete(exclusion: ExerciseExclusionEntity)

    @Query("DELETE FROM exercise_exclusions WHERE exerciseId = :exerciseId")
    suspend fun deleteByExerciseId(exerciseId: String)

    @Query("DELETE FROM exercise_exclusions WHERE isGlobal = 0")
    suspend fun clearNonGlobalExclusions()

    @Query("DELETE FROM exercise_exclusions")
    suspend fun clearAll()
}
