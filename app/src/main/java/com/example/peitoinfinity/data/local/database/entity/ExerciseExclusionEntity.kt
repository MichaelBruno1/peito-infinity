package com.example.peitoinfinity.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "exercise_exclusions",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["exerciseId"], unique = true)]
)
data class ExerciseExclusionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val isGlobal: Boolean
)
