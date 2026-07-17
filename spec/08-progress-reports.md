# 08 — Progresso e Relatórios

Especificação do registro de progresso de treinos e geração de relatórios semanais.

## Fluxo de Registro de Progresso

### 1. Iniciar Sessão de Treino

O usuário inicia uma sessão a partir da tela `PlanDayDetailScreen`:

```
Tap "Iniciar Treino"
    → Criar WorkoutSessionEntity (startedAt = now, finishedAt = null)
    → Navegar para WorkoutScreen
    → Iniciar timer
```

### 2. Registrar Exercícios

Durante o treino, para cada exercício do dia:

#### Exercícios de Musculação (COMPOUND, ISOLATION)

Para cada série planejada, o usuário registra:
- **Carga (kg):** Peso utilizado no exercício
- **Repetições:** Quantidade de repetições realizadas

Fluxo por série:
```
Série 1: Carga = 60kg, Reps = 12 → Botão "✓" → Salvar ExerciseLogEntity
Série 2: Carga = 65kg, Reps = 10 → Botão "✓" → Salvar ExerciseLogEntity
Série 3: Carga = 65kg, Reps = 8  → Botão "✓" → Salvar ExerciseLogEntity
```

Campos do `ExerciseLogEntity` preenchidos:
- `sessionId`: ID da sessão ativa
- `exerciseId`: ID do exercício
- `setNumber`: Número da série (1, 2, 3...)
- `weightKg`: Carga em kg
- `reps`: Repetições realizadas
- `durationSeconds`: null
- `distanceMeters`: null
- `speedKmh`: null
- `completedAt`: timestamp atual

#### Exercícios Aeróbicos/Cardio (CARDIO)

Para exercícios cardiovasculares, o usuário registra (uma única entrada):
- **Velocidade (km/h):** Velocidade média
- **Tempo (minutos):** Duração do exercício
- **Distância (metros ou km):** Distância percorrida

Campos do `ExerciseLogEntity` preenchidos:
- `sessionId`: ID da sessão ativa
- `exerciseId`: ID do exercício
- `setNumber`: 1 (sempre 1 para cardio)
- `weightKg`: null
- `reps`: null
- `durationSeconds`: tempo em segundos (convertido de minutos)
- `distanceMeters`: distância em metros
- `speedKmh`: velocidade em km/h
- `completedAt`: timestamp atual

### 3. Finalizar Sessão

```
Tap "Finalizar Treino"
    → Diálogo de confirmação: "Deseja finalizar o treino?"
    → Atualizar WorkoutSessionEntity (finishedAt = now)
    → Exibir resumo rápido da sessão
    → Navegar de volta para PlanDayDetailScreen
```

### Resumo Rápido da Sessão (após finalizar)

Exibir um BottomSheet ou Dialog com:
- Duração total da sessão
- Total de exercícios completados
- Peso total levantado (soma de weightKg × reps)
- Distância total percorrida (se houver cardio)

---

## Interface de Registro (WorkoutScreen)

### Layout por Exercício (Musculação)

```
┌────────────────────────────────────────────┐
│  💪 Supino Reto com Barra                  │
│  Meta: 4 × 8-12 | Descanso: 90s           │
│                                            │
│  ┌──────────────────────────────────────┐  │
│  │ Série 1: [60 kg]  [12 reps]    ✅   │  │
│  │ Série 2: [65 kg]  [10 reps]    ✅   │  │
│  │ Série 3: [__  kg]  [__  reps]   ➕  │  │
│  │ Série 4: [__  kg]  [__  reps]   ➕  │  │
│  └──────────────────────────────────────┘  │
│                                            │
│  Última vez: 60kg × 12, 65kg × 10...      │
└────────────────────────────────────────────┘
```

**Funcionalidades:**
- Pré-preencher carga da última sessão do mesmo exercício (se houver)
- Exibir histórico resumido do exercício (última sessão)
- Séries já registradas ficam com fundo verde claro (PastelGreen)
- Séries pendentes ficam com campos editáveis
- Validação: carga > 0 e reps > 0

### Layout por Exercício (Cardio)

```
┌────────────────────────────────────────────┐
│  🏃 Corrida na Esteira                     │
│  Meta: 20 min                              │
│                                            │
│  ┌──────────────────────────────────────┐  │
│  │ Velocidade: [__  km/h]              │  │
│  │ Tempo:      [__  min]               │  │
│  │ Distância:  [__  m]                 │  │
│  │                                      │  │
│  │      [✓ Registrar]                  │  │
│  └──────────────────────────────────────┘  │
│                                            │
│  Última vez: 8.5 km/h, 25 min, 3540m      │
└────────────────────────────────────────────┘
```

---

## Relatório Semanal

### Cálculos

O relatório é gerado para uma semana específica (segunda a domingo).

#### 1. Tempo Total de Treino

```kotlin
// Soma da duração de todas as sessões finalizadas na semana
val totalTimeMinutes = sessions
    .filter { it.finishedAt != null }
    .sumOf { (it.finishedAt!! - it.startedAt) / 60_000 }
```

#### 2. Sessões Realizadas

```kotlin
val sessionsCount = sessions.count { it.finishedAt != null }
```

#### 3. Peso Total Levantado

```kotlin
// Soma de (carga × repetições) de todos os logs de musculação da semana
// Via query SQL:
// SELECT COALESCE(SUM(el.weightKg * el.reps), 0)
// FROM exercise_logs el
// INNER JOIN workout_sessions ws ON el.sessionId = ws.id
// WHERE ws.startedAt BETWEEN :start AND :end
// AND el.weightKg IS NOT NULL AND el.reps IS NOT NULL
```

Exibir em kg. Se > 1000kg, exibir em toneladas (ex: "2.5 ton").

#### 4. Distância Total Percorrida

```kotlin
// Soma de distanceMeters de todos os logs cardio da semana
// Converter para km na exibição
// Via query SQL:
// SELECT COALESCE(SUM(el.distanceMeters), 0)
// FROM exercise_logs el
// INNER JOIN workout_sessions ws ON el.sessionId = ws.id
// WHERE ws.startedAt BETWEEN :start AND :end
// AND el.distanceMeters IS NOT NULL
```

#### 5. Velocidade Média

```kotlin
// Média de speedKmh dos logs cardio da semana
// Via query SQL:
// SELECT COALESCE(AVG(el.speedKmh), 0)
// FROM exercise_logs el
// INNER JOIN workout_sessions ws ON el.sessionId = ws.id
// WHERE ws.startedAt BETWEEN :start AND :end
// AND el.speedKmh IS NOT NULL
```

#### 6. Tempo Médio de Descanso

```kotlin
// Média dos restSeconds dos exercícios executados na semana
// Estimativa baseada nos exercícios do plano que foram concluídos
val avgRestSeconds = completedPlanExercises
    .map { it.restSeconds }
    .average()
    .toInt()
```

### Modelo de Dados do Relatório

```kotlin
data class WeeklyReport(
    val weekStartDate: LocalDate,     // Segunda-feira
    val weekEndDate: LocalDate,       // Domingo
    val totalTrainingTimeMinutes: Int, // Tempo total em minutos
    val sessionsCount: Int,           // Número de sessões
    val totalWeightKg: Float,         // Peso total levantado
    val totalDistanceKm: Float,       // Distância total em km
    val averageSpeedKmh: Float,       // Velocidade média
    val averageRestSeconds: Int,      // Descanso médio em segundos
    val hasData: Boolean              // Se há dados na semana
)
```

### GetWeeklyReportUseCase

```kotlin
class GetWeeklyReportUseCase @Inject constructor(
    private val sessionRepository: WorkoutSessionRepository,
    private val exerciseLogRepository: ExerciseLogRepository,
    private val planExerciseRepository: PlanExerciseRepository
) {
    suspend operator fun invoke(weekStart: LocalDate): WeeklyReport {
        val startMillis = weekStart.atStartOfDay()
            .toInstant(ZoneOffset.UTC).toEpochMilli()
        val endMillis = weekStart.plusDays(7).atStartOfDay()
            .toInstant(ZoneOffset.UTC).toEpochMilli()

        val sessions = sessionRepository.getSessionsForWeekSync(startMillis, endMillis)
        val completedSessions = sessions.filter { it.finishedAt != null }

        val totalTimeMinutes = completedSessions.sumOf {
            ((it.finishedAt!! - it.startedAt) / 60_000).toInt()
        }

        val totalWeight = exerciseLogRepository.getTotalWeightForWeek(startMillis, endMillis)
        val totalDistance = exerciseLogRepository.getTotalDistanceForWeek(startMillis, endMillis)
        val avgSpeed = exerciseLogRepository.getAverageSpeedForWeek(startMillis, endMillis)

        // Estimar descanso médio baseado nos exercícios do plano
        val avgRest = if (completedSessions.isNotEmpty()) {
            // Buscar restSeconds dos plan_exercises relacionados
            val exerciseIds = completedSessions.map { it.planDayId }
            val avgRestFromPlan = planExerciseRepository
                .getAverageRestForDays(exerciseIds)
            avgRestFromPlan
        } else 0

        return WeeklyReport(
            weekStartDate = weekStart,
            weekEndDate = weekStart.plusDays(6),
            totalTrainingTimeMinutes = totalTimeMinutes,
            sessionsCount = completedSessions.size,
            totalWeightKg = totalWeight,
            totalDistanceKm = totalDistance / 1000f,
            averageSpeedKmh = avgSpeed,
            averageRestSeconds = avgRest,
            hasData = completedSessions.isNotEmpty()
        )
    }
}
```

---

## Interface do Relatório (ReportScreen)

### Seletor de Semana

```
    <  Semana de 14/07 - 20/07  >
```

- Botões `<` e `>` para navegar entre semanas
- Não permitir navegar para semanas futuras
- Exibir "Esta semana" quando for a semana atual

### Cards de Métricas

Layout em grid 2 colunas:

```
┌─────────────────┐ ┌─────────────────┐
│ ⏱️ Tempo Total  │ │ 🏋️ Sessões     │
│    2h 15min     │ │      4          │
│                 │ │                 │
└─────────────────┘ └─────────────────┘
┌─────────────────┐ ┌─────────────────┐
│ 💪 Peso Total   │ │ 🏃 Distância   │
│   12.450 kg     │ │   8.5 km        │
│                 │ │                 │
└─────────────────┘ └─────────────────┘
┌─────────────────┐ ┌─────────────────┐
│ ⚡ Vel. Média   │ │ ⏳ Desc. Médio  │
│   8.2 km/h     │ │     75s         │
│                 │ │                 │
└─────────────────┘ └─────────────────┘
```

Cada `StatCard` deve:
- Ter gradiente suave com cor do ícone
- Animação de count-up ao aparecer (ver `spec/05-design-system.md`)
- Formatar números adequadamente (2h 15min, 12.450 kg, 8.5 km)

### Estado Vazio

Quando não há dados para a semana selecionada:

```
┌────────────────────────────────────────┐
│                                        │
│         [ícone de calendário]          │
│                                        │
│   Nenhum treino registrado             │
│   nesta semana                         │
│                                        │
│   Inicie um treino para ver            │
│   seu progresso aqui!                  │
│                                        │
└────────────────────────────────────────┘
```

---

## Formatação de Valores

```kotlin
object ValueFormatter {
    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 -> "${hours}h ${mins}min"
            else -> "${mins}min"
        }
    }

    fun formatWeight(kg: Float): String {
        return when {
            kg >= 1000 -> String.format("%.1f ton", kg / 1000f)
            kg >= 1 -> String.format("%.0f kg", kg)
            else -> String.format("%.1f kg", kg)
        }
    }

    fun formatDistance(km: Float): String {
        return when {
            km >= 1 -> String.format("%.1f km", km)
            else -> String.format("%.0f m", km * 1000)
        }
    }

    fun formatSpeed(kmh: Float): String = String.format("%.1f km/h", kmh)

    fun formatRest(seconds: Int): String {
        return when {
            seconds >= 60 -> "${seconds / 60}min ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }

    fun formatTimer(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }
}
```

---

## Notas para o Agente Codificador

1. O timer da sessão de treino deve contar usando `LaunchedEffect` + `delay(1000)`.
2. Pré-preencher campos de carga com valores da última sessão do mesmo exercício.
3. As queries de relatório devem considerar timestamps em milissegundos.
4. A semana começa na **segunda-feira** e termina no **domingo**.
5. Usar `LocalDate` do `java.time` para manipulação de datas.
6. Os valores do relatório devem ser calculados por queries SQL otimizadas (não carregar todos os logs em memória).
7. O `completedAt` do ExerciseLog é preenchido automaticamente com `System.currentTimeMillis()` ao registrar.
8. Exercícios do tipo FLEXIBILITY não registram carga nem métricas de cardio — apenas marcar como concluído.
9. A sessão ativa (finishedAt = null) deve aparecer como badge na BottomBar do item "Treino".
10. Se o app for fechado durante uma sessão, ao reabrir, verificar se há sessão ativa e perguntar se deseja continuar ou finalizar.
