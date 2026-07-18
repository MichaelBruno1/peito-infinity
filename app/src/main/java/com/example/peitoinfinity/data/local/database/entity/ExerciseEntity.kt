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
    val difficulty: String,

    // Biomechanical metadata fields
    val aliases: String = "[]", // Lista JSON
    val movementPattern: String = "",
    val plane: String = "",
    val jointAction: String = "",
    val stability: String = "",
    val rangeOfMotion: String = "",
    val unilateral: Boolean = false,
    val axialLoad: String = "",
    val spinalLoad: String = "",
    val primaryMuscles: String = "[]", // Lista JSON
    val secondaryMuscles: String = "[]", // Lista JSON
    val stabilizers: String = "[]", // Lista JSON
    val stimulusScore: Int = 0,
    val fatigueScore: Int = 0,
    val progressionScore: Int = 0,
    val learningCurve: Int = 0,
    val versatility: Int = 0,
    val shoulderFriendly: Boolean = true,
    val kneeFriendly: Boolean = true,
    val hipFriendly: Boolean = true,
    val lowerBackFriendly: Boolean = true,
    val injuryRisk: String = "",
    val stretchBias: String = "",
    val shortenedBias: String = "",
    val constantTension: String = "",
    val peakContraction: String = "",
    val priority: String = "",
    val recommendedOrder: Int = 0,
    val recommendedSets: String = "",
    val recommendedRepRange: String = "",
    val maxWeeklyFrequency: Int = 0,
    val redundantWith: String = "[]", // Lista JSON
    val goodPairings: String = "[]", // Lista JSON
    val avoidAfter: String = "[]", // Lista JSON
    val overallRating: Float = 0f
)
