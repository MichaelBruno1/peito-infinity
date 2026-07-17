package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.model.WeeklyReport
import com.example.peitoinfinity.domain.repository.ExerciseLogRepository
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import com.example.peitoinfinity.domain.repository.WorkoutSessionRepository
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

class GetWeeklyReportUseCase @Inject constructor(
    private val sessionRepository: WorkoutSessionRepository,
    private val exerciseLogRepository: ExerciseLogRepository,
    private val trainingPlanRepository: TrainingPlanRepository
) {
    suspend operator fun invoke(weekStart: LocalDate): WeeklyReport {
        val startMillis = weekStart.atStartOfDay()
            .toInstant(ZoneOffset.UTC).toEpochMilli()
        val endMillis = weekStart.plusDays(7).atStartOfDay()
            .toInstant(ZoneOffset.UTC).toEpochMilli()

        val sessions = sessionRepository.getSessionsForWeekSync(startMillis, endMillis)
        val completedSessions = sessions.filter { it.finishedAt != null }

        val totalTimeMinutes = completedSessions.sumOf {
            ((it.finishedAt!! - it.startedAt) / 60_000).toInt()
        }

        val totalWeight = exerciseLogRepository.getTotalWeightForWeek(startMillis, endMillis)
        val totalDistance = exerciseLogRepository.getTotalDistanceForWeek(startMillis, endMillis)
        val avgSpeed = exerciseLogRepository.getAverageSpeedForWeek(startMillis, endMillis)

        // Estimar descanso médio baseado nos exercícios do plano
        val avgRest = if (completedSessions.isNotEmpty()) {
            val dayIds = completedSessions.map { it.planDayId }
            trainingPlanRepository.getAverageRestForDays(dayIds)
        } else 0

        return WeeklyReport(
            weekStartDate = weekStart,
            weekEndDate = weekStart.plusDays(6),
            totalTrainingTimeMinutes = totalTimeMinutes,
            sessionsCount = completedSessions.size,
            totalWeightKg = totalWeight,
            totalDistanceKm = totalDistance / 1000f,
            averageSpeedKmh = avgSpeed,
            averageRestSeconds = avgRest,
            hasData = completedSessions.isNotEmpty()
        )
    }
}
