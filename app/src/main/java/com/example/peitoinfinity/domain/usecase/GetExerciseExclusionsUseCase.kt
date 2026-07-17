package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.repository.ExerciseExclusionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExerciseExclusionsUseCase @Inject constructor(
    private val repository: ExerciseExclusionRepository
) {
    fun getAll(): Flow<List<String>> = repository.getAll()
    fun getGlobal(): Flow<List<String>> = repository.getGlobalExclusions()
    suspend fun getExcludedIds(): Set<String> = repository.getExcludedIds()
}
