package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.MuscleGroup
import com.example.peitoinfinity.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    operator fun invoke(): Flow<List<Exercise>> = repository.getAllExercises()

    fun byMuscleGroup(group: MuscleGroup): Flow<List<Exercise>> =
        repository.getByMuscleGroup(group)

    fun search(query: String): Flow<List<Exercise>> =
        repository.search(query)
}
