package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = PlanDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["planDayId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["planDayId"])]
)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planDayId: Long,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val notes: String? = null
)
