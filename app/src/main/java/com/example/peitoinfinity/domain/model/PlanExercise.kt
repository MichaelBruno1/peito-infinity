package com.example.peitoinfinity.domain.model

data class PlanExercise(
    val id: Long = 0,
    val planDayId: Long,
    val exerciseId: String,
    val exercise: Exercise? = null,
    val orderIndex: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String? = null
)
