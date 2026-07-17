package com.example.peitoinfinity.di

import com.example.peitoinfinity.data.repository.*
import com.example.peitoinfinity.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        impl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindTrainingPlanRepository(
        impl: TrainingPlanRepositoryImpl
    ): TrainingPlanRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutSessionRepository(
        impl: WorkoutSessionRepositoryImpl
    ): WorkoutSessionRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindExerciseLogRepository(
        impl: ExerciseLogRepositoryImpl
    ): ExerciseLogRepository

    @Binds
    @Singleton
    abstract fun bindExerciseExclusionRepository(
        impl: ExerciseExclusionRepositoryImpl
    ): ExerciseExclusionRepository
}
