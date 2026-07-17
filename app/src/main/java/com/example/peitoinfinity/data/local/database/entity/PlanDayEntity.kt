package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "plan_days",
    foreignKeys = [
        ForeignKey(
            entity = TrainingPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["planId"])]
)
data class PlanDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val dayNumber: Int,
    val dayName: String,
    val focusMuscleGroups: String, // Lista JSON de grupos musculares
    val estimatedDurationMinutes: Int
)
