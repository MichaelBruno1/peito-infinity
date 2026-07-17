package com.example.peitoinfinity.domain.model

enum class TrainingGoal(val displayName: String, val description: String) {
    WEIGHT_LOSS("Perda de Peso", "Emagrecer e definir"),
    MUSCLE_GAIN("Ganho de Massa", "Hipertrofia muscular"),
    STRENGTH_GAIN("Ganho de Força", "Aumentar cargas e performance"),
    MAINTENANCE("Manutenção", "Manter o peso e forma atual")
}
