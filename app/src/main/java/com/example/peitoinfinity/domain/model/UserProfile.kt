package com.example.peitoinfinity.domain.model

data class UserProfile(
    val gender: Gender,
    val heightCm: Float,
    val weightKg: Float,
    val trainingDaysPerWeek: Int,
    val availableTimeMinutes: Int,
    val trainingLevel: TrainingLevel,
    val trainingGoal: TrainingGoal,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
