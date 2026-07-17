package com.example.peitoinfinity.domain.model

data class ExerciseLog(
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: String,
    val setNumber: Int,
    val weightKg: Float? = null,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val distanceMeters: Float? = null,
    val speedKmh: Float? = null,
    val completedAt: Long = System.currentTimeMillis()
)
