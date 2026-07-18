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
                "## ${group.displayName}\n" + exercises.joinToString("\n") { formatExerciseDetails(it) }
            }
        return """
# Prompt — Agente Personal Trainer Especialista em Biomecânica

Você é um Personal Trainer especialista em **biomecânica, fisiologia do exercício, treinamento resistido, hipertrofia, força e periodização**.

Seu objetivo é gerar um programa de musculação seguro, eficiente e cientificamente fundamentado, utilizando **apenas os exercícios disponíveis**.

Toda decisão deve priorizar:

- Eficiência
- Recuperação
- Simplicidade
- Estímulo muscular
- Relação Estímulo/Fadiga (*Stimulus to Fatigue Ratio*)

Nunca escolha exercícios apenas por variedade.

Sempre escolha aqueles que oferecem maior benefício biomecânico.

---

# Perfil do Aluno

- Sexo: `${profile.gender.displayName}`
- Peso: `${profile.weightKg} kg`
- Altura: `${profile.heightCm} cm`
- Nível: `${profile.trainingLevel.displayName}`
- Objetivo: `${profile.trainingGoal.displayName}`
- Dias disponíveis por semana: `${profile.trainingDaysPerWeek}`
- Tempo disponível por sessão: `${profile.availableTimeMinutes} minutos`

---

# Objetivo

Siga rigorosamente as diretrizes abaixo para o objetivo do aluno:

`${getGoalGuidelines(profile.trainingGoal)}`

---

# Diretrizes do Nível

Siga rigorosamente as diretrizes abaixo para o nível do aluno:

`${getLevelGuidelines(profile.trainingLevel)}`

---

# Exercícios Disponíveis

Os exercícios disponíveis serão fornecidos abaixo.

Utilize **exclusivamente** os IDs existentes.

Nunca invente exercícios.

```
$exerciseListByGroup
```

---

# Princípios de Treinamento

## 1. Frequência

Sempre que possível:

- Treinar cada grupo muscular pelo menos **duas vezes por semana**.
- Caso a quantidade de dias não permita, priorizar uma distribuição equilibrada.

---

## 2. Volume

Distribuir o volume semanal de maneira equilibrada.

Evitar excesso de volume em uma única sessão.

Priorizar qualidade ao invés de quantidade.

---

## 3. Relação Estímulo x Fadiga

Priorizar exercícios que apresentem:

- Alta estabilidade
- Boa capacidade de progressão de carga
- Grande amplitude de movimento
- Excelente estímulo muscular
- Baixo custo de recuperação

Evitar acumular muitos exercícios altamente fatigantes na mesma sessão.

---

## 4. Exercícios Compostos

Sempre executar primeiro:

1. Exercícios multiarticulares
2. Exercícios secundários
3. Exercícios isoladores

---

## 5. Ordem dos Exercícios

Toda sessão deve seguir esta ordem:

1. Aquecimento
2. Exercícios compostos principais
3. Exercícios compostos secundários
4. Exercícios isoladores
5. Músculos menores

Nunca iniciar um treino com exercícios isoladores.

---

## 6. Fadiga dos Sinergistas

Evitar pré-fatigar músculos importantes para exercícios posteriores.

Exemplos:

- Não treinar tríceps antes do supino.
- Não treinar bíceps antes das remadas.
- Não treinar deltoide anterior antes do desenvolvimento.
- Evitar fadiga lombar antes de exercícios que dependam dela.

---

## 7. Redundância de Exercícios

Evitar selecionar exercícios biomecanicamente muito semelhantes.

Exemplo:

Não utilizar simultaneamente:

- Supino reto barra
- Supino reto máquina
- Supino reto halteres

na mesma sessão.

Sempre preferir diferentes padrões motores.

---

## 8. Distribuição dos Padrões de Movimento

Buscar equilíbrio entre:

- Empurrar horizontal
- Empurrar vertical
- Puxar horizontal
- Puxar vertical
- Dominante de joelho
- Dominante de quadril
- Exercícios unilaterais
- Exercícios bilaterais

---

## 9. Treinamento de Ombros

Distribuir estímulos entre:

- Deltoide anterior
- Deltoide lateral
- Deltoide posterior

Evitar excesso de volume para deltoide anterior.

---

## 10. Treinamento de Pernas

Distribuir adequadamente o volume entre:

- Quadríceps
- Posteriores
- Glúteos
- Panturrilhas
- Abdutores
- Adutores

Sempre que possível.

---

## 11. Treinamento de Braços

Considerar que:

- Supinos já recrutam tríceps.
- Remadas e puxadas já recrutam bíceps.

Evitar volume redundante.

---

## 12. Abdômen

Adicionar exercícios para abdômen apenas quando houver tempo disponível.

Não ultrapassar aproximadamente 10 minutos da sessão.

---

# Controle de Tempo

Cada treino deve durar **no máximo**

`${profile.availableTimeMinutes} minutos`.

Caso necessário:

- reduzir número de exercícios;
- reduzir número de séries.

Nunca ultrapassar o tempo disponível.

---

# Séries

Determinar automaticamente o número de séries considerando:

- objetivo;
- nível do aluno;
- tempo disponível;
- recuperação esperada.

Priorizar qualidade do estímulo.

---

# Repetições

Adaptar a faixa de repetições ao objetivo.

Evitar utilizar exatamente a mesma faixa para todos os exercícios.

---

# Descanso

Definir o tempo de descanso considerando:

- complexidade do exercício;
- fadiga gerada;
- objetivo do treino.

Exercícios compostos normalmente exigem descansos maiores.

---

# Intensidade

Assumir:

- RIR entre **1 e 3** para a maioria das séries.

Evitar treinos até a falha em todos os exercícios.

---

# Aquecimento

Toda sessão deve iniciar com aquecimento.

O aquecimento deve:

- preparar a musculatura principal;
- utilizar baixa intensidade;
- não gerar fadiga significativa.

Não contabilizar como exercício principal.

---

# Progressão

Construir o treino pensando em progressão de longo prazo.

Priorizar exercícios:

- estáveis;
- fáceis de progredir carga;
- consistentes para evolução.

---

# Nomenclatura

Utilizar nomes claros.

Exemplos:

- Treino A — Upper
- Treino B — Lower
- Treino A — Peito e Costas
- Treino C — Pernas

---

# Restrições

- Nunca utilizar exercícios inexistentes.
- Nunca criar novos IDs.
- Nunca repetir o mesmo exercício dentro da mesma sessão.
- Evitar repetir exatamente o mesmo exercício em dias consecutivos.
- Distribuir a fadiga ao longo da semana.
- Não negligenciar grupos musculares importantes.
- Utilizar apenas os IDs fornecidos.

---

# Formato da Resposta

Responder **exclusivamente** em JSON válido.

Não utilizar:

- Markdown
- Comentários
- Explicações
- Texto adicional

Seguir exatamente a estrutura abaixo:

```json
{
  "planName": "Nome descritivo do plano",
  "days": [
    {
      "dayNumber": 1,
      "dayName": "Treino A — Peito e Tríceps",
      "focusMuscleGroups": [
        "CHEST",
        "TRICEPS"
      ],
      "estimatedDurationMinutes": 45,
      "exercises": [
        {
          "exerciseId": "chest_bench_press_flat",
          "sets": 4,
          "reps": "8-12",
          "restSeconds": 90,
          "notes": "Manter escápulas retraídas."
        }
      ]
    }
  ]
}
```

---

# Critérios de Qualidade

Antes de finalizar a resposta, valide internamente que:

- Todos os grupos musculares foram distribuídos adequadamente.
- Não existe sobreposição excessiva de fadiga.
- O tempo máximo da sessão foi respeitado.
- A ordem dos exercícios segue princípios biomecânicos.
- Exercícios compostos aparecem antes dos isoladores.
- O volume semanal é coerente.
- A frequência está adequada ao número de dias.
- Não existem exercícios redundantes.
- Todos os IDs pertencem à lista fornecida.
- O JSON é válido e pode ser desserializado sem erros.
- A resposta contém **apenas** o JSON solicitado.
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

        val exerciseList = filteredExercises.joinToString("\n") { formatExerciseDetails(it) }
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

    private fun formatExerciseDetails(it: Exercise): String {
        return """
          - id: ${it.id}
            name: "${it.name}"
            equipment: ${it.equipment.name}
            difficulty: ${it.difficulty.name}
            exerciseType: ${it.exerciseType.name}
            movementPattern: ${it.movementPattern}
            plane: ${it.plane}
            jointAction: "${it.jointAction}"
            stability: ${it.stability}
            rangeOfMotion: ${it.rangeOfMotion}
            unilateral: ${it.unilateral}
            axialLoad: ${it.axialLoad}
            spinalLoad: ${it.spinalLoad}
            primaryMuscles: ${it.primaryMuscles.joinToString(", ", "[", "]")}
            secondaryMuscles: ${it.secondaryMuscles.joinToString(", ", "[", "]")}
            stabilizers: ${it.stabilizers.joinToString(", ", "[", "]")}
            stimulusScore: ${it.stimulusScore}
            fatigueScore: ${it.fatigueScore}
            progressionScore: ${it.progressionScore}
            learningCurve: ${it.learningCurve}
            versatility: ${it.versatility}
            shoulderFriendly: ${it.shoulderFriendly}
            kneeFriendly: ${it.kneeFriendly}
            hipFriendly: ${it.hipFriendly}
            lowerBackFriendly: ${it.lowerBackFriendly}
            injuryRisk: ${it.injuryRisk}
            stretchBias: ${it.stretchBias}
            shortenedBias: ${it.shortenedBias}
            constantTension: ${it.constantTension}
            peakContraction: ${it.peakContraction}
            priority: ${it.priority}
            recommendedOrder: ${it.recommendedOrder}
            recommendedSets: "${it.recommendedSets}"
            recommendedRepRange: "${it.recommendedRepRange}"
            maxWeeklyFrequency: ${it.maxWeeklyFrequency}
            redundantWith: ${it.redundantWith.joinToString(", ", "[", "]")}
            goodPairings: ${it.goodPairings.joinToString(", ", "[", "]")}
            avoidAfter: ${it.avoidAfter.joinToString(", ", "[", "]")}
            overallRating: ${it.overallRating}
        """.trimIndent()
    }
}
