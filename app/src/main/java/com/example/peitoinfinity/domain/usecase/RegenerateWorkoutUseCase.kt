package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.data.ai.AiProviderSelector
import com.example.peitoinfinity.data.ai.LlmResponseParser
import com.example.peitoinfinity.data.ai.PromptBuilder
import com.example.peitoinfinity.data.local.database.entity.PlanExerciseEntity
import com.example.peitoinfinity.domain.model.PlanDay
import com.example.peitoinfinity.domain.repository.ExerciseRepository
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import com.example.peitoinfinity.domain.repository.UserProfileRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class RegenerateWorkoutUseCase @Inject constructor(
    private val aiSelector: AiProviderSelector,
    private val promptBuilder: PromptBuilder,
    private val responseParser: LlmResponseParser,
    private val userProfileRepository: UserProfileRepository,
    private val exerciseRepository: ExerciseRepository,
    private val trainingPlanRepository: TrainingPlanRepository
) {
    suspend operator fun invoke(
        planDayId: Long,
        excludedExerciseIds: Set<String> = emptySet()
    ): Result<PlanDay> {
        return try {
            val profile = userProfileRepository.getProfileSync()
                ?: return Result.failure(Exception("Perfil não encontrado"))

            val currentDay = trainingPlanRepository.getDayById(planDayId)
                ?: return Result.failure(Exception("Dia de treino não encontrado"))

            val exercises = exerciseRepository.getAllExercisesSync()

            val prompt = promptBuilder.buildRegenerateDayPrompt(
                profile = profile,
                targetMuscleGroups = currentDay.focusMuscleGroups,
                availableExercises = exercises,
                excludedExerciseIds = excludedExerciseIds
            )

            val aiProvider = aiSelector.getCurrentProvider()
            val parsedResponseResult = retryWithBackoff(maxRetries = 2) {
                val responseResult = aiProvider.generate(prompt)
                if (responseResult.isFailure) {
                    Result.failure(responseResult.exceptionOrNull()!!)
                } else {
                    val text = responseResult.getOrThrow()
                    responseParser.parseDayPlan(text)
                }
            }

            if (parsedResponseResult.isFailure) {
                return Result.failure(parsedResponseResult.exceptionOrNull()!!)
            }

            val llmDay = parsedResponseResult.getOrThrow()

            val validExerciseIds = exercises.map { it.id }.toSet()
            val validatedExercises = llmDay.exercises.filter { it.exerciseId in validExerciseIds }

            trainingPlanRepository.deleteExercisesForDay(planDayId)

            val exerciseEntities = validatedExercises.mapIndexed { index, ex ->
                PlanExerciseEntity(
                    planDayId = planDayId,
                    exerciseId = ex.exerciseId,
                    orderIndex = index,
                    sets = ex.sets,
                    reps = ex.reps,
                    restSeconds = ex.restSeconds,
                    notes = ex.notes
                )
            }
            trainingPlanRepository.insertExercises(exerciseEntities)

            val updatedDay = trainingPlanRepository.getDayById(planDayId)
                ?: return Result.failure(Exception("Falha ao recuperar o dia atualizado"))

            Result.success(updatedDay)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun <T> retryWithBackoff(
        maxRetries: Int = 2,
        initialDelayMs: Long = 1000,
        block: suspend () -> Result<T>
    ): Result<T> {
        var lastException: Throwable? = null
        repeat(maxRetries + 1) { attempt ->
            val result = block()
            if (result.isSuccess) return result
            lastException = result.exceptionOrNull()
            if (attempt < maxRetries) {
                delay(initialDelayMs * (attempt + 1))
            }
        }
        return Result.failure(lastException ?: Exception("Falha após $maxRetries tentativas"))
    }
}
