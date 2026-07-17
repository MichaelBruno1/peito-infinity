package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "exercise_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["exerciseId"])
    ]
)
data class ExerciseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseId: String,
    val setNumber: Int,
    val weightKg: Float? = null,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val distanceMeters: Float? = null,
    val speedKmh: Float? = null,
    val completedAt: Long
)
