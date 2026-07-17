# 03 — Modelo de Dados

Especificação completa do banco de dados local usando Room 3.0.0 (`androidx.room3`).

## Configuração do Room 3

- Package: `androidx.room3`
- KSP obrigatório (KAPT não é suportado)
- Plugin: `androidx.room3` no `build.gradle.kts`
- Database class: `PeitoInfinityDatabase`
- Database version: 1
- Export schema: true
- Schema directory: `$projectDir/schemas`

## Entidades

### 1. UserProfileEntity

Tabela: `user_profile`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Default | Descrição |
|-------|------------|-------------|----------|---------|----------|
| id | Int | INTEGER | Não | 1 (fixo) | PK — Apenas 1 perfil |
| gender | String | TEXT | Não | — | MALE, FEMALE |
| heightCm | Float | REAL | Não | — | Altura em centímetros |
| weightKg | Float | REAL | Não | — | Peso em quilogramas |
| trainingDaysPerWeek | Int | INTEGER | Não | — | Dias de treino por semana (1-7) |
| availableTimeMinutes | Int | INTEGER | Não | — | Tempo disponível por sessão em minutos |
| trainingLevel | String | TEXT | Não | — | BEGINNER, INTERMEDIATE, ADVANCED |
| trainingGoal | String | TEXT | Não | — | WEIGHT_LOSS, MUSCLE_GAIN, STRENGTH_GAIN, MAINTENANCE |
| createdAt | Long | INTEGER | Não | — | Timestamp de criação (millis) |
| updatedAt | Long | INTEGER | Não | — | Timestamp de atualização (millis) |

**Notas:**
- O app suporta apenas UM perfil de usuário (id = 1 fixo).
- Usar `@Insert(onConflict = OnConflictStrategy.REPLACE)`.
- Os enums `trainingLevel` e `trainingGoal` são armazenados como String (name do enum).

### 2. ExerciseEntity

Tabela: `exercises`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Descrição |
|-------|------------|-------------|----------|----------|
| id | String | TEXT | Não | PK — ex: `chest_bench_press_flat` |
| name | String | TEXT | Não | Nome em português (ex: "Supino Reto com Barra") |
| primaryMuscleGroup | String | TEXT | Não | Enum MuscleGroup como String |
| secondaryMuscleGroups | String | TEXT | Não | Lista serializada JSON (ex: `["TRICEPS","SHOULDERS"]`) |
| equipment | String | TEXT | Não | Enum Equipment como String |
| exerciseType | String | TEXT | Não | STRENGTH, CARDIO, COMPOUND, ISOLATION, FLEXIBILITY |
| description | String | TEXT | Não | Descrição breve do movimento |
| difficulty | String | TEXT | Não | BEGINNER, INTERMEDIATE, ADVANCED |

**Type Converters:** `secondaryMuscleGroups` armazenado como JSON array string. Criar TypeConverter para `List<String>` ↔ `String`.

**Index:** Criar index em `primaryMuscleGroup`.

### 3. TrainingPlanEntity

Tabela: `training_plans`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Descrição |
|-------|------------|-------------|----------|----------|
| id | Long | INTEGER | Não | PK autoGenerate |
| name | String | TEXT | Não | Nome do plano (ex: "Plano Hipertrofia v3") |
| isActive | Boolean | INTEGER | Não | Se é o plano ativo atual |
| trainingGoal | String | TEXT | Não | Objetivo usado na geração |
| trainingLevel | String | TEXT | Não | Nível usado na geração |
| daysPerWeek | Int | INTEGER | Não | Dias por semana |
| createdAt | Long | INTEGER | Não | Timestamp de criação (millis) |
| rawLlmResponse | String | TEXT | Sim | Resposta bruta da LLM (para debug) |

### 4. PlanDayEntity

Tabela: `plan_days`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Descrição |
|-------|------------|-------------|----------|----------|
| id | Long | INTEGER | Não | PK autoGenerate |
| planId | Long | INTEGER | Não | FK → training_plans.id |
| dayNumber | Int | INTEGER | Não | Número do dia (1, 2, 3...) |
| dayName | String | TEXT | Não | Nome (ex: "Treino A — Peito e Tríceps") |
| focusMuscleGroups | String | TEXT | Não | JSON array dos grupos foco |
| estimatedDurationMinutes | Int | INTEGER | Não | Duração estimada em minutos |

**Foreign Key:** `planId` referencia `training_plans(id)` com `onDelete = CASCADE`.

**Index:** Criar index em `planId`.

### 5. PlanExerciseEntity

Tabela: `plan_exercises`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Descrição |
|-------|------------|-------------|----------|----------|
| id | Long | INTEGER | Não | PK autoGenerate |
| planDayId | Long | INTEGER | Não | FK → plan_days.id |
| exerciseId | String | TEXT | Não | FK → exercises.id |
| orderIndex | Int | INTEGER | Não | Ordem do exercício no treino (0, 1, 2...) |
| sets | Int | INTEGER | Não | Número de séries |
| reps | String | TEXT | Não | Repetições (ex: "12", "8-12", "até falha", "AMRAP") |
| restSeconds | Int | INTEGER | Não | Descanso entre séries em segundos |
| notes | String | TEXT | Sim | Observações/dicas da LLM |

**Foreign Keys:**
- `planDayId` → `plan_days(id)` com `onDelete = CASCADE`
- `exerciseId` → `exercises(id)` com `onDelete = RESTRICT`

**Index:** Criar index em `planDayId` e `exerciseId`.

### 6. WorkoutSessionEntity

Tabela: `workout_sessions`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Descrição |
|-------|------------|-------------|----------|----------|
| id | Long | INTEGER | Não | PK autoGenerate |
| planDayId | Long | INTEGER | Não | FK → plan_days.id |
| startedAt | Long | INTEGER | Não | Timestamp de início (millis) |
| finishedAt | Long | INTEGER | Sim | Timestamp de fim (null se em andamento) |
| notes | String | TEXT | Sim | Notas do usuário sobre a sessão |

**Foreign Key:** `planDayId` → `plan_days(id)` com `onDelete = RESTRICT`.

**Index:** Criar index em `planDayId`.

### 7. ExerciseLogEntity

Tabela: `exercise_logs`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Descrição |
|-------|------------|-------------|----------|----------|
| id | Long | INTEGER | Não | PK autoGenerate |
| sessionId | Long | INTEGER | Não | FK → workout_sessions.id |
| exerciseId | String | TEXT | Não | FK → exercises.id |
| setNumber | Int | INTEGER | Não | Número da série (1, 2, 3...) |
| weightKg | Float | REAL | Sim | Carga em kg (exercícios de musculação) |
| reps | Int | INTEGER | Sim | Repetições realizadas (musculação) |
| durationSeconds | Int | INTEGER | Sim | Duração em segundos (cardio) |
| distanceMeters | Float | REAL | Sim | Distância em metros (cardio) |
| speedKmh | Float | REAL | Sim | Velocidade em km/h (cardio) |
| completedAt | Long | INTEGER | Não | Timestamp de conclusão (millis) |

**Foreign Keys:**
- `sessionId` → `workout_sessions(id)` com `onDelete = CASCADE`
- `exerciseId` → `exercises(id)` com `onDelete = RESTRICT`

**Index:** Criar index em `sessionId` e `exerciseId`.

**Notas:**
- Para exercícios de musculação: preencher `weightKg` e `reps`, os demais ficam null
- Para exercícios aeróbicos: preencher `durationSeconds`, `distanceMeters` e `speedKmh`, os demais ficam null
- A UI deve apresentar campos diferentes conforme o `exerciseType` do exercício

### 8. ExerciseExclusionEntity

Tabela: `exercise_exclusions`

| Campo | Tipo Kotlin | Tipo SQLite | Nullable | Descrição |
|-------|------------|-------------|----------|----------|
| id | Long | INTEGER | Não | PK autoGenerate |
| exerciseId | String | TEXT | Não | FK → exercises.id |
| isGlobal | Boolean | INTEGER | Não | true = exclusão permanente, false = apenas para próxima geração |

**Foreign Key:** `exerciseId` → `exercises(id)` com `onDelete = CASCADE`.

**Unique:** Criar unique constraint em `exerciseId`.

## DAOs

### UserProfileDao

```kotlin
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfileEntity)
}
```

### ExerciseDao

```kotlin
@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY primaryMuscleGroup, name")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE primaryMuscleGroup = :muscleGroup ORDER BY name")
    fun getByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: String): ExerciseEntity?

    @Query("""
        SELECT * FROM exercises
        WHERE id NOT IN (SELECT exerciseId FROM exercise_exclusions)
        ORDER BY primaryMuscleGroup, name
    """)
    fun getAvailableExercises(): Flow<List<ExerciseEntity>>

    @Query("""
        SELECT * FROM exercises
        WHERE id NOT IN (SELECT exerciseId FROM exercise_exclusions)
        AND primaryMuscleGroup = :muscleGroup
        ORDER BY name
    """)
    fun getAvailableByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int

    @Query("""
        SELECT * FROM exercises
        WHERE name LIKE '%' || :query || '%'
        ORDER BY primaryMuscleGroup, name
    """)
    fun search(query: String): Flow<List<ExerciseEntity>>
}
```

### TrainingPlanDao

```kotlin
@Dao
interface TrainingPlanDao {
    @Query("SELECT * FROM training_plans WHERE isActive = 1 LIMIT 1")
    fun getActivePlan(): Flow<TrainingPlanEntity?>

    @Query("SELECT * FROM training_plans ORDER BY createdAt DESC")
    fun getAllPlans(): Flow<List<TrainingPlanEntity>>

    @Insert
    suspend fun insertPlan(plan: TrainingPlanEntity): Long

    @Update
    suspend fun updatePlan(plan: TrainingPlanEntity)

    @Query("UPDATE training_plans SET isActive = 0")
    suspend fun deactivateAllPlans()

    @Delete
    suspend fun deletePlan(plan: TrainingPlanEntity)

    @Transaction
    suspend fun insertAndActivatePlan(plan: TrainingPlanEntity): Long {
        deactivateAllPlans()
        return insertPlan(plan.copy(isActive = true))
    }
}
```

### PlanDayDao

```kotlin
@Dao
interface PlanDayDao {
    @Query("SELECT * FROM plan_days WHERE planId = :planId ORDER BY dayNumber")
    fun getDaysForPlan(planId: Long): Flow<List<PlanDayEntity>>

    @Query("SELECT * FROM plan_days WHERE id = :dayId")
    suspend fun getDayById(dayId: Long): PlanDayEntity?

    @Insert
    suspend fun insertDays(days: List<PlanDayEntity>): List<Long>

    @Delete
    suspend fun deleteDay(day: PlanDayEntity)

    @Query("DELETE FROM plan_days WHERE planId = :planId AND id = :dayId")
    suspend fun deleteDayById(planId: Long, dayId: Long)
}
```

### PlanExerciseDao

```kotlin
@Dao
interface PlanExerciseDao {
    @Query("SELECT * FROM plan_exercises WHERE planDayId = :dayId ORDER BY orderIndex")
    fun getExercisesForDay(dayId: Long): Flow<List<PlanExerciseEntity>>

    @Insert
    suspend fun insertExercises(exercises: List<PlanExerciseEntity>)

    @Query("DELETE FROM plan_exercises WHERE planDayId = :dayId")
    suspend fun deleteExercisesForDay(dayId: Long)
}
```

### WorkoutSessionDao

```kotlin
@Dao
interface WorkoutSessionDao {
    @Insert
    suspend fun startSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions WHERE finishedAt IS NULL LIMIT 1")
    fun getActiveSession(): Flow<WorkoutSessionEntity?>

    @Query("""
        SELECT * FROM workout_sessions
        WHERE startedAt BETWEEN :startOfWeek AND :endOfWeek
        ORDER BY startedAt
    """)
    fun getSessionsForWeek(startOfWeek: Long, endOfWeek: Long): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): WorkoutSessionEntity?
}
```

### ExerciseLogDao

```kotlin
@Dao
interface ExerciseLogDao {
    @Insert
    suspend fun insertLog(log: ExerciseLogEntity): Long

    @Update
    suspend fun updateLog(log: ExerciseLogEntity)

    @Delete
    suspend fun deleteLog(log: ExerciseLogEntity)

    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY completedAt")
    fun getLogsForSession(sessionId: Long): Flow<List<ExerciseLogEntity>>

    @Query("""
        SELECT * FROM exercise_logs
        WHERE exerciseId = :exerciseId
        ORDER BY completedAt DESC
    """)
    fun getLogsForExercise(exerciseId: String): Flow<List<ExerciseLogEntity>>

    // --- Queries para Relatório Semanal ---

    @Query("""
        SELECT COALESCE(SUM(el.weightKg * el.reps), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.weightKg IS NOT NULL AND el.reps IS NOT NULL
    """)
    suspend fun getTotalWeightForWeek(startOfWeek: Long, endOfWeek: Long): Float

    @Query("""
        SELECT COALESCE(SUM(el.distanceMeters), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.distanceMeters IS NOT NULL
    """)
    suspend fun getTotalDistanceForWeek(startOfWeek: Long, endOfWeek: Long): Float

    @Query("""
        SELECT COALESCE(AVG(el.speedKmh), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.speedKmh IS NOT NULL
    """)
    suspend fun getAverageSpeedForWeek(startOfWeek: Long, endOfWeek: Long): Float

    @Query("""
        SELECT COALESCE(SUM(el.durationSeconds), 0)
        FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE ws.startedAt BETWEEN :startOfWeek AND :endOfWeek
        AND el.durationSeconds IS NOT NULL
    """)
    suspend fun getTotalCardioTimeForWeek(startOfWeek: Long, endOfWeek: Long): Int
}
```

### ExerciseExclusionDao

```kotlin
@Dao
interface ExerciseExclusionDao {
    @Query("SELECT * FROM exercise_exclusions")
    fun getAll(): Flow<List<ExerciseExclusionEntity>>

    @Query("SELECT * FROM exercise_exclusions WHERE isGlobal = 1")
    fun getGlobalExclusions(): Flow<List<ExerciseExclusionEntity>>

    @Query("SELECT exerciseId FROM exercise_exclusions")
    suspend fun getExcludedExerciseIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exclusion: ExerciseExclusionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exclusions: List<ExerciseExclusionEntity>)

    @Delete
    suspend fun delete(exclusion: ExerciseExclusionEntity)

    @Query("DELETE FROM exercise_exclusions WHERE exerciseId = :exerciseId")
    suspend fun deleteByExerciseId(exerciseId: String)

    @Query("DELETE FROM exercise_exclusions WHERE isGlobal = 0")
    suspend fun clearNonGlobalExclusions()

    @Query("DELETE FROM exercise_exclusions")
    suspend fun clearAll()
}
```

## Database Class

```kotlin
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
@TypeConverters(Converters::class)
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
```

## Type Converters

```kotlin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList()
        else json.decodeFromString(value)
}
```

## Diagrama ER (Entidade-Relacionamento)

```
┌──────────────────┐
│  user_profile    │
│  (1 registro)    │
└──────────────────┘
        │ gera
        ▼
┌──────────────────┐        ┌──────────────────┐
│  training_plans  │───────▶│    plan_days      │
│  (N planos)      │ 1:N    │  (N dias)         │
└──────────────────┘        └──────────────────┘
                                    │ 1:N
                                    ▼
                            ┌──────────────────┐
                            │  plan_exercises   │
                            │  (N exercícios)   │
                            └──────────────────┘
                                    │ N:1
                                    ▼
┌──────────────────┐        ┌──────────────────┐
│exercise_exclusions│◀──────│    exercises      │
│  (N exclusões)   │  N:1   │ (pré-populada)   │
└──────────────────┘        └──────────────────┘
                                    ▲ N:1
                                    │
┌──────────────────┐        ┌──────────────────┐
│ workout_sessions │───────▶│  exercise_logs    │
│  (N sessões)     │ 1:N    │  (N registros)   │
└──────────────────┘        └──────────────────┘
        ▲ N:1
        │
┌──────────────────┐
│    plan_days     │
└──────────────────┘
```

## Pré-população de Exercícios

Na primeira execução do app, todos os exercícios de `spec/07-exercise-database.md` devem ser inseridos no banco de dados. Implementar via `RoomDatabase.Callback`:

```kotlin
class ExercisePrepopulateCallback(
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        scope.launch(Dispatchers.IO) {
            // Inserir todos os exercícios
            val db = /* referência ao database */
            if (db.exerciseDao().getCount() == 0) {
                db.exerciseDao().insertAll(ExerciseData.allExercises)
            }
        }
    }
}
```

Os dados dos exercícios devem ficar em um object `ExerciseData` no package `data.local.database`, contendo uma lista estática de todos os `ExerciseEntity`.

## Notas para o Agente Codificador

1. **Room 3.0.0** usa package `androidx.room3`, NÃO `androidx.room`.
2. **KSP** é obrigatório. KAPT não é suportado pelo Room 3.
3. Pré-popular exercícios usando `RoomDatabase.Callback` no `onCreate`.
4. Usar `kotlinx.serialization` para TypeConverters de listas JSON.
5. Todos os DAOs retornam `Flow` para observação reativa, exceto operações de escrita que são `suspend`.
6. Indices são obrigatórios nos campos FK para performance.
7. O campo `reps` em `PlanExerciseEntity` é `String` para suportar formatos como "8-12", "até falha", "AMRAP".
8. Usar `COALESCE` nas queries de agregação para evitar null.
9. Timestamps são sempre em milissegundos (epoch millis via `System.currentTimeMillis()`).
10. Criar o `ExerciseData` object com base na lista completa de `spec/07-exercise-database.md`.
