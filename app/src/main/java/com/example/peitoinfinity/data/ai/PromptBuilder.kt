package com.example.peitoinfinity.data.ai

import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.MuscleGroup
import com.example.peitoinfinity.domain.model.TrainingGoal
import com.example.peitoinfinity.domain.model.TrainingLevel
import com.example.peitoinfinity.domain.model.UserProfile

class PromptBuilder {

    /**
     * Gera o prompt para criação de plano de treino completo.
     */
    fun buildFullPlanPrompt(
        profile: UserProfile,
        availableExercises: List<Exercise>,
        excludedExerciseIds: Set<String>
    ): String {
        val filteredExercises = availableExercises
            .filter { it.id !in excludedExerciseIds }

        val exerciseListByGroup = filteredExercises
            .groupBy { it.primaryMuscleGroup }
            .entries
            .joinToString("\n") { (group, exercises) ->
                "## ${group.displayName}\n" + exercises.joinToString("\n") { "- ${it.id}: ${it.name}" }
            }
        return """
Você é um personal trainer especialista em musculação e treinamento físico.

## PERFIL DO ALUNO
- Sexo: ${profile.gender.displayName}
- Peso: ${profile.weightKg} kg
- Altura: ${profile.heightCm} cm
- Nível: ${profile.trainingLevel.displayName}
- Objetivo: ${profile.trainingGoal.displayName}
- Dias disponíveis por semana: ${profile.trainingDaysPerWeek}
- Tempo disponível por sessão: ${profile.availableTimeMinutes} minutos

## REGRAS
1. Crie um plano de treino com exatamente ${profile.trainingDaysPerWeek} dias de treino.
2. Cada dia deve ter duração estimada de NO MÁXIMO ${profile.availableTimeMinutes} minutos.
3. Use APENAS exercícios da lista fornecida abaixo (pelo ID exato).
4. Adapte séries, repetições e descanso ao nível e objetivo do aluno.
5. Para ${profile.trainingGoal.displayName}:
${getGoalGuidelines(profile.trainingGoal)}
6. Distribua os grupos musculares de forma equilibrada entre os dias.
7. Inclua aquecimento e exercícios compostos antes dos isolados.

## DIRETRIZES POR NÍVEL
${getLevelGuidelines(profile.trainingLevel)}

## EXERCÍCIOS DISPONÍVEIS (use APENAS estes IDs)
$exerciseListByGroup

## FORMATO DE RESPOSTA
Responda EXCLUSIVAMENTE em JSON válido, sem markdown, sem comentários, seguindo EXATAMENTE esta estrutura:

```json
{
  "planName": "Nome descritivo do plano",
  "days": [
    {
      "dayNumber": 1,
      "dayName": "Treino A — Peito e Tríceps",
      "focusMuscleGroups": ["CHEST", "TRICEPS"],
      "estimatedDurationMinutes": 45,
      "exercises": [
        {
          "exerciseId": "chest_bench_press_flat",
          "sets": 4,
          "reps": "8-12",
          "restSeconds": 90,
          "notes": "Manter escápulas retraídas"
        }
      ]
    }
  ]
}
```

IMPORTANTE: Responda APENAS com o JSON, sem nenhum texto antes ou depois.
""".trimIndent()
    }

    /**
     * Gera o prompt para regenerar um dia específico do treino.
     */
    fun buildRegenerateDayPrompt(
        profile: UserProfile,
        targetMuscleGroups: List<MuscleGroup>,
        availableExercises: List<Exercise>,
        excludedExerciseIds: Set<String>
    ): String {
        val filteredExercises = availableExercises
            .filter { it.id !in excludedExerciseIds }
            .filter { exercise ->
                exercise.primaryMuscleGroup in targetMuscleGroups ||
                exercise.secondaryMuscleGroups.any { it in targetMuscleGroups }
            }

        val exerciseList = filteredExercises.joinToString("\n") { "- ${it.id}: ${it.name}" }
        val muscleGroupNames = targetMuscleGroups.joinToString(", ") { it.displayName }

        return """
Você é um personal trainer especialista em musculação.

## PERFIL DO ALUNO
- Sexo: ${profile.gender.displayName}
- Peso: ${profile.weightKg} kg
- Altura: ${profile.heightCm} cm
- Nível: ${profile.trainingLevel.displayName}
- Objetivo: ${profile.trainingGoal.displayName}
- Tempo disponível por sessão: ${profile.availableTimeMinutes} minutos

## TAREFA
Crie UM dia de treino focado em: $muscleGroupNames

## REGRAS
1. Duração máxima: ${profile.availableTimeMinutes} minutos.
2. Use APENAS exercícios da lista abaixo (pelo ID exato).
3. Adapte ao nível ${profile.trainingLevel.displayName} e objetivo ${profile.trainingGoal.displayName}.
${getGoalGuidelines(profile.trainingGoal)}

## EXERCÍCIOS DISPONÍVEIS
$exerciseList

## FORMATO DE RESPOSTA
Responda EXCLUSIVAMENTE em JSON válido:

```json
{
  "dayName": "Treino X — $muscleGroupNames",
  "focusMuscleGroups": [${targetMuscleGroups.joinToString(", ") { "\"${it.name}\"" }}],
  "estimatedDurationMinutes": 45,
  "exercises": [
    {
      "exerciseId": "id_do_exercicio",
      "sets": 4,
      "reps": "8-12",
      "restSeconds": 90,
      "notes": "Observação opcional"
    }
  ]
}
```

IMPORTANTE: Responda APENAS com o JSON.
""".trimIndent()
    }

    private fun getGoalGuidelines(goal: TrainingGoal): String {
        return when (goal) {
            TrainingGoal.WEIGHT_LOSS -> """
   - Priorize exercícios compostos e circuitos
   - Séries: 3-4 | Repetições: 12-20 | Descanso: 30-60s
   - Inclua exercícios cardio/aeróbicos quando disponíveis
   - Maior volume de treino, menor carga
   - Supersets e drop-sets são bem-vindos"""

            TrainingGoal.MUSCLE_GAIN -> """
   - Priorize exercícios compostos seguidos de isolados
   - Séries: 3-5 | Repetições: 8-12 | Descanso: 60-120s
   - Foco em tempo sob tensão
   - Progressão de carga é prioridade
   - Volume moderado a alto"""

            TrainingGoal.STRENGTH_GAIN -> """
   - Priorize exercícios compostos pesados
   - Séries: 4-6 | Repetições: 1-6 | Descanso: 120-300s
   - Foco em cargas pesadas, baixas repetições
   - Menos exercícios por sessão, mais séries por exercício
   - Periodização linear ou ondulada"""

            TrainingGoal.MAINTENANCE -> """
   - Equilíbrio entre compostos e isolados
   - Séries: 3-4 | Repetições: 8-15 | Descanso: 60-90s
   - Volume moderado
   - Variedade de exercícios
   - Manter intensidade sem buscar progressão agressiva"""
        }
    }

    private fun getLevelGuidelines(level: TrainingLevel): String {
        return when (level) {
            TrainingLevel.BEGINNER -> """
- Iniciante (< 6 meses de experiência):
  - Máximo 4-5 exercícios por dia
  - Priorizar exercícios básicos compostos
  - Evitar exercícios com técnica muito complexa
  - Séries mais leves, foco em aprendizado do movimento
  - Descanso mais longo entre séries"""

            TrainingLevel.INTERMEDIATE -> """
- Intermediário (6 meses a 2 anos):
  - 5-7 exercícios por dia
  - Mix de compostos e isolados
  - Pode incluir técnicas avançadas ocasionalmente
  - Progressão de carga Consistent"""

            TrainingLevel.ADVANCED -> """
- Avançado (> 2 anos):
  - 6-8+ exercícios por dia
  - Técnicas avançadas: drop-sets, supersets, rest-pause
  - Periodização é recomendada
  - Variação de estímulos"""
        }
    }
}
