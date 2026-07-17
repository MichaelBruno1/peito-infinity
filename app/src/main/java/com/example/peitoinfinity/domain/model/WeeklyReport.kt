package com.example.peitoinfinity.domain.model

import java.time.LocalDate

data class WeeklyReport(
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val totalTrainingTimeMinutes: Int,
    val sessionsCount: Int,
    val totalWeightKg: Float,
    val totalDistanceKm: Float,
    val averageSpeedKmh: Float,
    val averageRestSeconds: Int,
    val hasData: Boolean
)
