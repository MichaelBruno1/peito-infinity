package com.example.peitoinfinity.data.local.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.ColumnTypeConverters
import com.example.peitoinfinity.data.local.database.dao.*
import com.example.peitoinfinity.data.local.database.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        ExerciseEntity::class,
        TrainingPlanEntity::class,
        PlanDayEntity::class,
        PlanExerciseEntity::class,
        WorkoutSessionEntity::class,
        ExerciseLogEntity::class,
        ExerciseExclusionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ColumnTypeConverters(Converters::class)
abstract class PeitoInfinityDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingPlanDao(): TrainingPlanDao
    abstract fun planDayDao(): PlanDayDao
    abstract fun planExerciseDao(): PlanExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun exerciseExclusionDao(): ExerciseExclusionDao
}
