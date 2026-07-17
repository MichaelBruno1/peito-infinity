package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val gender: String,
    val heightCm: Float,
    val weightKg: Float,
    val trainingDaysPerWeek: Int,
    val availableTimeMinutes: Int,
    val trainingLevel: String,
    val trainingGoal: String,
    val createdAt: Long,
    val updatedAt: Long
)
