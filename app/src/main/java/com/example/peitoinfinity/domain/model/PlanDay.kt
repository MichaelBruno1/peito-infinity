package com.example.peitoinfinity.domain.model

data class PlanDay(
    val id: Long = 0,
    val planId: Long,
    val dayNumber: Int,
    val dayName: String,
    val focusMuscleGroups: List<MuscleGroup>,
    val estimatedDurationMinutes: Int,
    val exercises: List<PlanExercise> = emptyList()
)
