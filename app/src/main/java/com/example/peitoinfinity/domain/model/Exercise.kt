package com.example.peitoinfinity.domain.model

data class Exercise(
    val id: String,
    val name: String,
    val primaryMuscleGroup: MuscleGroup,
    val secondaryMuscleGroups: List<MuscleGroup>,
    val equipment: Equipment,
    val exerciseType: ExerciseType,
    val description: String,
    val difficulty: Difficulty
)
