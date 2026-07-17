package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "plan_exercises",
    foreignKeys = [
        ForeignKey(
            entity = PlanDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["planDayId"],
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
        Index(value = ["planDayId"]),
        Index(value = ["exerciseId"])
    ]
)
data class PlanExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planDayId: Long,
    val exerciseId: String,
    val orderIndex: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String?
)
