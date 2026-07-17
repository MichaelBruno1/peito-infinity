package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.model.ExerciseLog
import com.example.peitoinfinity.domain.repository.ExerciseLogRepository
import javax.inject.Inject

class LogExerciseUseCase @Inject constructor(
    private val exerciseLogRepository: ExerciseLogRepository
) {
    /**
     * Registra uma série de exercício de musculação.
     */
    suspend fun logStrengthSet(
        sessionId: Long,
        exerciseId: String,
        setNumber: Int,
        weightKg: Float,
        reps: Int
    ): Result<Long> {
        return try {
            require(weightKg > 0) { "Carga deve ser maior que zero" }
            require(reps > 0) { "Repetições devem ser maior que zero" }

            val log = ExerciseLog(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                weightKg = weightKg,
                reps = reps
            )
            val id = exerciseLogRepository.insertLog(log)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra um exercício cardio.
     */
    suspend fun logCardio(
        sessionId: Long,
        exerciseId: String,
        speedKmh: Float?,
        durationSeconds: Int?,
        distanceMeters: Float?
    ): Result<Long> {
        return try {
            val log = ExerciseLog(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = 1,
                speedKmh = speedKmh,
                durationSeconds = durationSeconds,
                distanceMeters = distanceMeters
            )
            val id = exerciseLogRepository.insertLog(log)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
