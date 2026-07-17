package com.example.peitoinfinity.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.peitoinfinity.domain.model.MuscleGroup

fun MuscleGroup.toColor(): Color {
    return when (this) {
        MuscleGroup.CHEST -> MuscleChest
        MuscleGroup.BACK -> MuscleBack
        MuscleGroup.SHOULDERS -> MuscleShoulders
        MuscleGroup.BICEPS -> MuscleBiceps
        MuscleGroup.TRICEPS -> MuscleTriceps
        MuscleGroup.QUADRICEPS -> MuscleQuadriceps
        MuscleGroup.HAMSTRINGS -> MuscleHamstrings
        MuscleGroup.GLUTES -> MuscleGlutes
        MuscleGroup.CALVES -> MuscleCalves
        MuscleGroup.ABS -> MuscleAbs
        MuscleGroup.FOREARMS -> MuscleForearms
        MuscleGroup.CARDIO_SYSTEM -> MuscleCardio
        MuscleGroup.TRAPS -> MuscleTraps
        else -> PastelBlue
    }
}
