package com.example.peitoinfinity.di

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.example.peitoinfinity.data.local.database.ExerciseData
import com.example.peitoinfinity.data.local.database.PeitoInfinityDatabase
import com.example.peitoinfinity.data.local.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PeitoInfinityDatabase {
        val databaseScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        var database: PeitoInfinityDatabase? = null
        val callback = object : RoomDatabase.Callback() {
            override suspend fun onCreate(connection: SQLiteConnection) {
                super.onCreate(connection)
                databaseScope.launch {
                    var attempts = 0
                    while (database == null && attempts < 100) {
                        delay(50)
                        attempts++
                    }
                    database?.let { db ->
                        db.exerciseDao().insertAll(ExerciseData.allExercises)
                    }
                }
            }

            override suspend fun onOpen(connection: SQLiteConnection) {
                super.onOpen(connection)
                databaseScope.launch {
                    var attempts = 0
                    while (database == null && attempts < 100) {
                        delay(50)
                        attempts++
                    }
                    database?.let { db ->
                        try {
                            val newIds = ExerciseData.allExercises.map { it.id }
                            db.exerciseDao().insertAll(ExerciseData.allExercises)
                            db.exerciseDao().deleteExcept(newIds)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        return Room.databaseBuilder(
            context,
            PeitoInfinityDatabase::class.java,
            "peito_infinity_database"
        )
        .addCallback(callback)
        .fallbackToDestructiveMigration()
        .build().also {
            database = it
        }
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(db: PeitoInfinityDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    @Singleton
    fun provideExerciseDao(db: PeitoInfinityDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    @Singleton
    fun provideTrainingPlanDao(db: PeitoInfinityDatabase): TrainingPlanDao = db.trainingPlanDao()

    @Provides
    @Singleton
    fun providePlanDayDao(db: PeitoInfinityDatabase): PlanDayDao = db.planDayDao()

    @Provides
    @Singleton
    fun providePlanExerciseDao(db: PeitoInfinityDatabase): PlanExerciseDao = db.planExerciseDao()

    @Provides
    @Singleton
    fun provideWorkoutSessionDao(db: PeitoInfinityDatabase): WorkoutSessionDao = db.workoutSessionDao()

    @Provides
    @Singleton
    fun provideExerciseLogDao(db: PeitoInfinityDatabase): ExerciseLogDao = db.exerciseLogDao()

    @Provides
    @Singleton
    fun provideExerciseExclusionDao(db: PeitoInfinityDatabase): ExerciseExclusionDao = db.exerciseExclusionDao()
}
