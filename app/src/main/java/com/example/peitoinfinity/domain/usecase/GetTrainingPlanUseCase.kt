package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.model.PlanDay
import com.example.peitoinfinity.domain.model.TrainingPlan
import com.example.peitoinfinity.domain.repository.ExerciseRepository
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTrainingPlanUseCase @Inject constructor(
    private val repository: TrainingPlanRepository,
    private val exerciseRepository: ExerciseRepository
) {
    /**
     * Obtém o plano ativo com todos os dias e exercícios.
     */
    fun getActivePlan(): Flow<TrainingPlan?> = repository.getActivePlan()

    /**
     * Obtém os dias do plano com exercícios preenchidos.
     */
    fun getDaysWithExercises(planId: Long): Flow<List<PlanDay>> {
        return repository.getDaysForPlan(planId).map { days ->
            days.map { day ->
                val exercises = repository.getExercisesForDaySync(day.id)
                val enrichedExercises = exercises.map { pe ->
                    val exercise = exerciseRepository.getById(pe.exerciseId)
                    pe.copy(exercise = exercise)
                }
                day.copy(exercises = enrichedExercises)
            }
        }
    }
}
