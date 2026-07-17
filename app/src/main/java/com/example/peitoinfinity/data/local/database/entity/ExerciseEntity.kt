package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Index

@Entity(
    tableName = "exercises",
    indices = [Index(value = ["primaryMuscleGroup"])]
)
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val primaryMuscleGroup: String,
    val secondaryMuscleGroups: String, // Lista JSON
    val equipment: String,
    val exerciseType: String,
    val description: String,
    val difficulty: String
)
