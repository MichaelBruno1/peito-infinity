package com.example.peitoinfinity.domain.model

enum class TrainingLevel(val displayName: String, val description: String) {
    BEGINNER("Iniciante", "Treino há menos de 6 meses"),
    INTERMEDIATE("Intermediário", "Treino entre 6 meses e 2 anos"),
    ADVANCED("Avançado", "Treino há mais de 2 anos")
}
