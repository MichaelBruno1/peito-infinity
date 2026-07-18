package com.example.peitoinfinity.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.example.peitoinfinity.data.local.database.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY primaryMuscleGroup, name")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE primaryMuscleGroup = :muscleGroup ORDER BY name")
    fun getByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: String): ExerciseEntity?

    @Query("""
        SELECT * FROM exercises 
        WHERE id NOT IN (SELECT exerciseId FROM exercise_exclusions)
        ORDER BY primaryMuscleGroup, name
    """)
    fun getAvailableExercises(): Flow<List<ExerciseEntity>>

    @Query("""
        SELECT * FROM exercises 
        WHERE id NOT IN (SELECT exerciseId FROM exercise_exclusions)
        AND primaryMuscleGroup = :muscleGroup
        ORDER BY name
    """)
    fun getAvailableByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("DELETE FROM exercises WHERE id NOT IN (:ids)")
    suspend fun deleteExcept(ids: List<String>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int

    @Query("""
        SELECT * FROM exercises 
        WHERE name LIKE '%' || :query || '%'
        ORDER BY primaryMuscleGroup, name
    """)
    fun search(query: String): Flow<List<ExerciseEntity>>
}
