package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "training_plans")
data class TrainingPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isActive: Boolean,
    val trainingGoal: String,
    val trainingLevel: String,
    val daysPerWeek: Int,
    val createdAt: Long,
    val rawLlmResponse: String?
)
