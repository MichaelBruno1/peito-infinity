package com.example.peitoinfinity.data.repository

import com.example.peitoinfinity.data.local.database.dao.ExerciseDao
import com.example.peitoinfinity.data.local.database.entity.ExerciseEntity
import com.example.peitoinfinity.domain.model.*
import com.example.peitoinfinity.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises().map { list -> list.map { it.toDomain() } }
    }

    override fun getByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>> {
        return exerciseDao.getByMuscleGroup(muscleGroup.name).map { list -> list.map { it.toDomain() } }
    }

    override fun getAvailableExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAvailableExercises().map { list -> list.map { it.toDomain() } }
    }

    override fun getAvailableByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>> {
        return exerciseDao.getAvailableByMuscleGroup(muscleGroup.name).map { list -> list.map { it.toDomain() } }
    }

    override fun search(query: String): Flow<List<Exercise>> {
        return exerciseDao.search(query).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getById(id: String): Exercise? {
        return exerciseDao.getById(id)?.toDomain()
    }

    override suspend fun getAllExercisesSync(): List<Exercise> {
        return exerciseDao.getAllExercises().map { list -> list.map { it.toDomain() } }.first()
    }

    override suspend fun getCount(): Int {
        return exerciseDao.getCount()
    }
}

// Mappers
fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    primaryMuscleGroup = MuscleGroup.valueOf(primaryMuscleGroup),
    secondaryMuscleGroups = Json.decodeFromString<List<String>>(secondaryMuscleGroups)
        .mapNotNull { runCatching { MuscleGroup.valueOf(it) }.getOrNull() },
    equipment = Equipment.valueOf(equipment),
    exerciseType = ExerciseType.valueOf(exerciseType),
    description = description,
    difficulty = Difficulty.valueOf(difficulty)
)
