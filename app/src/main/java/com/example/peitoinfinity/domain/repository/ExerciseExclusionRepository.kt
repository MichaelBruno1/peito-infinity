package com.example.peitoinfinity.domain.repository

import kotlinx.coroutines.flow.Flow

interface ExerciseExclusionRepository {
    fun getAll(): Flow<List<String>>  // Exercise IDs
    fun getGlobalExclusions(): Flow<List<String>>
    suspend fun getExcludedIds(): Set<String>
    suspend fun addExclusion(exerciseId: String, isGlobal: Boolean)
    suspend fun addExclusions(exerciseIds: List<String>, isGlobal: Boolean)
    suspend fun removeExclusion(exerciseId: String)
    suspend fun clearNonGlobal()
    suspend fun clearAll()
}
