package com.example.peitoinfinity.domain.model

data class WorkoutSession(
    val id: Long = 0,
    val planDayId: Long,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val notes: String? = null,
    val logs: List<ExerciseLog> = emptyList()
)
