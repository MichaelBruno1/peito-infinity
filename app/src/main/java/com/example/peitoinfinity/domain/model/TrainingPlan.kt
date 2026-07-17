package com.example.peitoinfinity.domain.model

data class TrainingPlan(
    val id: Long = 0,
    val name: String,
    val isActive: Boolean,
    val trainingGoal: TrainingGoal,
    val trainingLevel: TrainingLevel,
    val daysPerWeek: Int,
    val createdAt: Long,
    val days: List<PlanDay> = emptyList()
)
