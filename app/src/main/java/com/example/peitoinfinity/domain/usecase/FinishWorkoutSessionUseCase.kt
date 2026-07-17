package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.repository.WorkoutSessionRepository
import javax.inject.Inject

class FinishWorkoutSessionUseCase @Inject constructor(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(
        sessionId: Long,
        notes: String? = null
    ): Result<Unit> {
        return try {
            sessionRepository.finishSession(sessionId, notes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
