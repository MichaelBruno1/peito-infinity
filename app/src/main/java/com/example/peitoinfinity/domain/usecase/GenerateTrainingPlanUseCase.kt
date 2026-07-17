package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.data.ai.AiProviderSelector
import com.example.peitoinfinity.data.ai.LlmResponseParser
import com.example.peitoinfinity.data.ai.PromptBuilder
import com.example.peitoinfinity.data.local.database.entity.PlanDayEntity
import com.example.peitoinfinity.data.local.database.entity.PlanExerciseEntity
import com.example.peitoinfinity.data.local.database.entity.TrainingPlanEntity
import com.example.peitoinfinity.domain.model.TrainingPlan
import com.example.peitoinfinity.domain.repository.ExerciseExclusionRepository
import com.example.peitoinfinity.domain.repository.ExerciseRepository
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import com.example.peitoinfinity.domain.repository.UserProfileRepository
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GenerateTrainingPlanUseCase @Inject constructor(
    private val aiSelector: AiProviderSelector,
    private val promptBuilder: PromptBuilder,
    private val responseParser: LlmResponseParser,
    private val userProfileRepository: UserProfileRepository,
    private val exerciseRepository: ExerciseRepository,
    private val trainingPlanRepository: TrainingPlanRepository,
    private val exerciseExclusionRepository: ExerciseExclusionRepository
) {
    suspend operator fun invoke(
        excludedExerciseIds: Set<String> = emptySet()
    ): Result<TrainingPlan> {
        return try {
            val profile = userProfileRepository.getProfileSync()
                ?: return Result.failure(Exception("Perfil não encontrado"))

            val exercises = exerciseRepository.getAllExercisesSync()

            val prompt = promptBuilder.buildFullPlanPrompt(
                profile = profile,
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
                    responseParser.parseFullPlan(text)
                }
            }

            if (parsedResponseResult.isFailure) {
                return Result.failure(parsedResponseResult.exceptionOrNull()!!)
            }

            val llmPlan = parsedResponseResult.getOrThrow()

            val validExerciseIds = exercises.map { it.id }.toSet()
            val validatedDays = llmPlan.days.map { day ->
                day.copy(
                    exercises = day.exercises.filter { it.exerciseId in validExerciseIds }
                )
            }

            val planEntity = TrainingPlanEntity(
                name = llmPlan.planName,
                isActive = true,
                trainingGoal = profile.trainingGoal.name,
                trainingLevel = profile.trainingLevel.name,
                daysPerWeek = profile.trainingDaysPerWeek,
                createdAt = System.currentTimeMillis(),
                rawLlmResponse = ""
            )

            val planId = trainingPlanRepository.insertAndActivate(planEntity)

            validatedDays.forEachIndexed { index, dayResponse ->
                val dayEntity = PlanDayEntity(
                    planId = planId,
                    dayNumber = dayResponse.dayNumber ?: (index + 1),
                    dayName = dayResponse.dayName,
                    focusMuscleGroups = Json.encodeToString(dayResponse.focusMuscleGroups),
                    estimatedDurationMinutes = dayResponse.estimatedDurationMinutes
                )
                val dayId = trainingPlanRepository.insertDay(dayEntity)

                val exerciseEntities = dayResponse.exercises.mapIndexed { exIndex, ex ->
                    PlanExerciseEntity(
                        planDayId = dayId,
                        exerciseId = ex.exerciseId,
                        orderIndex = exIndex,
                        sets = ex.sets,
                        reps = ex.reps,
                        restSeconds = ex.restSeconds,
                        notes = ex.notes
                    )
                }
                trainingPlanRepository.insertExercises(exerciseEntities)
            }

            exerciseExclusionRepository.clearNonGlobal()

            Result.success(trainingPlanRepository.getPlanById(planId))
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
