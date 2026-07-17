package com.example.peitoinfinity.data.repository

import com.example.peitoinfinity.data.local.database.dao.ExerciseExclusionDao
import com.example.peitoinfinity.data.local.database.entity.ExerciseExclusionEntity
import com.example.peitoinfinity.domain.repository.ExerciseExclusionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseExclusionRepositoryImpl @Inject constructor(
    private val exerciseExclusionDao: ExerciseExclusionDao
) : ExerciseExclusionRepository {

    override fun getAll(): Flow<List<String>> {
        return exerciseExclusionDao.getAll().map { list ->
            list.map { it.exerciseId }
        }
    }

    override fun getGlobalExclusions(): Flow<List<String>> {
        return exerciseExclusionDao.getGlobalExclusions().map { list ->
            list.map { it.exerciseId }
        }
    }

    override suspend fun getExcludedIds(): Set<String> {
        return exerciseExclusionDao.getExcludedExerciseIds().toSet()
    }

    override suspend fun addExclusion(exerciseId: String, isGlobal: Boolean) {
        val entity = ExerciseExclusionEntity(
            exerciseId = exerciseId,
            isGlobal = isGlobal
        )
        exerciseExclusionDao.insert(entity)
    }

    override suspend fun addExclusions(exerciseIds: List<String>, isGlobal: Boolean) {
        val entities = exerciseIds.map {
            ExerciseExclusionEntity(
                exerciseId = it,
                isGlobal = isGlobal
            )
        }
        exerciseExclusionDao.insertAll(entities)
    }

    override suspend fun removeExclusion(exerciseId: String) {
        exerciseExclusionDao.deleteByExerciseId(exerciseId)
    }

    override suspend fun clearNonGlobal() {
        exerciseExclusionDao.clearNonGlobalExclusions()
    }

    override suspend fun clearAll() {
        exerciseExclusionDao.clearAll()
    }
}
