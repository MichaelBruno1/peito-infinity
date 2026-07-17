package com.example.peitoinfinity.domain.repository

import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    fun getByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>>
    fun getAvailableExercises(): Flow<List<Exercise>>
    fun getAvailableByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>>
    fun search(query: String): Flow<List<Exercise>>
    suspend fun getById(id: String): Exercise?
    suspend fun getAllExercisesSync(): List<Exercise>
    suspend fun getCount(): Int
}
