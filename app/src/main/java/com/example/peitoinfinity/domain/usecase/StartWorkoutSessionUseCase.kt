package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.repository.WorkoutSessionRepository
import javax.inject.Inject

class StartWorkoutSessionUseCase @Inject constructor(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(planDayId: Long): Result<Long> {
        return try {
            val activeSession = sessionRepository.getActiveSessionSync()
            if (activeSession != null) {
                return Result.failure(Exception("Já existe uma sessão ativa"))
            }
            val sessionId = sessionRepository.startSession(planDayId)
            Result.success(sessionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
