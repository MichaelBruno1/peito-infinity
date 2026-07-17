# 09 — Domain Layer

Especificação completa dos Models, Repository Interfaces e Use Cases da camada de domínio.

## Models (domain/model/)

### Enums

```kotlin
// TrainingGoal.kt
enum class TrainingGoal(val displayName: String, val description: String) {
    WEIGHT_LOSS("Perda de Peso", "Emagrecer e definir"),
    MUSCLE_GAIN("Ganho de Massa", "Hipertrofia muscular"),
    STRENGTH_GAIN("Ganho de Força", "Aumentar cargas e performance"),
    MAINTENANCE("Manutenção", "Manter o peso e forma atual")
}

// TrainingLevel.kt
enum class TrainingLevel(val displayName: String, val description: String) {
    BEGINNER("Iniciante", "Treino há menos de 6 meses"),
    INTERMEDIATE("Intermediário", "Treino entre 6 meses e 2 anos"),
    ADVANCED("Avançado", "Treino há mais de 2 anos")
}

// MuscleGroup.kt
enum class MuscleGroup(val displayName: String) {
    CHEST("Peito"),
    BACK("Costas"),
    SHOULDERS("Ombros"),
    BICEPS("Bíceps"),
    TRICEPS("Tríceps"),
    FOREARMS("Antebraço"),
    ABS("Abdômen"),
    OBLIQUES("Oblíquos"),
    LOWER_BACK("Lombar"),
    QUADRICEPS("Quadríceps"),
    HAMSTRINGS("Posterior de Coxa"),
    GLUTES("Glúteos"),
    CALVES("Panturrilha"),
    HIP_FLEXORS("Flexores do Quadril"),
    ADDUCTORS("Adutores"),
    ABDUCTORS("Abdutores"),
    TRAPS("Trapézio"),
    FULL_BODY("Corpo Inteiro"),
    CARDIO_SYSTEM("Cardio")
}

// Equipment.kt
enum class Equipment(val displayName: String) {
    BARBELL("Barra"),
    DUMBBELL("Halteres"),
    CABLE("Cabo"),
    MACHINE("Máquina"),
    SMITH_MACHINE("Smith"),
    BODYWEIGHT("Peso Corporal"),
    KETTLEBELL("Kettlebell"),
    RESISTANCE_BAND("Elástico"),
    EZ_BAR("Barra W"),
    TRAP_BAR("Barra Hexagonal"),
    BENCH("Banco"),
    PULL_UP_BAR("Barra Fixa"),
    DIP_BARS("Barras Paralelas"),
    TREADMILL("Esteira"),
    STATIONARY_BIKE("Bicicleta Ergométrica"),
    ELLIPTICAL("Elíptico"),
    ROWING_MACHINE("Remo"),
    BATTLE_ROPES("Cordas de Batalha"),
    MEDICINE_BALL("Medicine Ball"),
    SWISS_BALL("Bola Suíça"),
    TRX("TRX"),
    NONE("Nenhum")
}

// ExerciseType.kt
enum class ExerciseType(val displayName: String) {
    COMPOUND("Composto"),
    ISOLATION("Isolado"),
    CARDIO("Cardio"),
    FLEXIBILITY("Flexibilidade")
}

// Difficulty.kt
enum class Difficulty(val displayName: String) {
    BEGINNER("Iniciante"),
    INTERMEDIATE("Intermediário"),
    ADVANCED("Avançado")
}

// AiMode.kt
enum class AiMode(val displayName: String) {
    LOCAL("Modelo Local"),
    REMOTE("Modelo Externo")
}

// Gender.kt
enum class Gender(val displayName: String) {
    MALE("Masculino"),
    FEMALE("Feminino")
}
```

### Data Classes

```kotlin
// UserProfile.kt
data class UserProfile(
    val gender: Gender,
    val heightCm: Float,
    val weightKg: Float,
    val trainingDaysPerWeek: Int,
    val availableTimeMinutes: Int,
    val trainingLevel: TrainingLevel,
    val trainingGoal: TrainingGoal,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Exercise.kt
data class Exercise(
    val id: String,
    val name: String,
    val primaryMuscleGroup: MuscleGroup,
    val secondaryMuscleGroups: List<MuscleGroup>,
    val equipment: Equipment,
    val exerciseType: ExerciseType,
    val description: String,
    val difficulty: Difficulty
)

// TrainingPlan.kt
data class TrainingPlan(
    val id: Long = 0,
    val name: String,
    val isActive: Boolean,
    val trainingGoal: TrainingGoal,
    val trainingLevel: TrainingLevel,
    val daysPerWeek: Int,
    val createdAt: Long,
    val days: List<PlanDay> = emptyList()
)

// PlanDay.kt
data class PlanDay(
    val id: Long = 0,
    val planId: Long,
    val dayNumber: Int,
    val dayName: String,
    val focusMuscleGroups: List<MuscleGroup>,
    val estimatedDurationMinutes: Int,
    val exercises: List<PlanExercise> = emptyList()
)

// PlanExercise.kt
data class PlanExercise(
    val id: Long = 0,
    val planDayId: Long,
    val exerciseId: String,
    val exercise: Exercise? = null,  // Preenchido via JOIN
    val orderIndex: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String? = null
)

// WorkoutSession.kt
data class WorkoutSession(
    val id: Long = 0,
    val planDayId: Long,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val notes: String? = null,
    val logs: List<ExerciseLog> = emptyList()
)

// ExerciseLog.kt
data class ExerciseLog(
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: String,
    val setNumber: Int,
    val weightKg: Float? = null,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val distanceMeters: Float? = null,
    val speedKmh: Float? = null,
    val completedAt: Long = System.currentTimeMillis()
)

// WeeklyReport.kt
data class WeeklyReport(
    val weekStartDate: java.time.LocalDate,
    val weekEndDate: java.time.LocalDate,
    val totalTrainingTimeMinutes: Int,
    val sessionsCount: Int,
    val totalWeightKg: Float,
    val totalDistanceKm: Float,
    val averageSpeedKmh: Float,
    val averageRestSeconds: Int,
    val hasData: Boolean
)
```

---

## Repository Interfaces (domain/repository/)

```kotlin
// UserProfileRepository.kt
interface UserProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun getProfileSync(): UserProfile?
    suspend fun saveProfile(profile: UserProfile)
    suspend fun hasProfile(): Boolean
}

// ExerciseRepository.kt
interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    fun getByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>>
    fun getAvailableExercises(): Flow<List<Exercise>>
    fun getAvailableByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>>
    fun search(query: String): Flow<List<Exercise>>
    suspend fun getById(id: String): Exercise?
    suspend fun getAllExercisesSync(): List<Exercise>
    suspend fun getCount(): Int
}

// TrainingPlanRepository.kt
interface TrainingPlanRepository {
    fun getActivePlan(): Flow<TrainingPlan?>
    fun getAllPlans(): Flow<List<TrainingPlan>>
    fun getDaysForPlan(planId: Long): Flow<List<PlanDay>>
    fun getExercisesForDay(dayId: Long): Flow<List<PlanExercise>>
    suspend fun insertAndActivate(plan: TrainingPlanEntity): Long
    suspend fun insertDay(day: PlanDayEntity): Long
    suspend fun insertExercises(exercises: List<PlanExerciseEntity>)
    suspend fun deleteExercisesForDay(dayId: Long)
    suspend fun deletePlan(planId: Long)
    suspend fun getPlanById(planId: Long): TrainingPlan
    suspend fun getDayById(dayId: Long): PlanDay?
    suspend fun getAverageRestForDays(dayIds: List<Long>): Int
}

// WorkoutSessionRepository.kt
interface WorkoutSessionRepository {
    fun getActiveSession(): Flow<WorkoutSession?>
    fun getAllSessions(): Flow<List<WorkoutSession>>
    fun getSessionsForWeek(startMillis: Long, endMillis: Long): Flow<List<WorkoutSession>>
    suspend fun startSession(planDayId: Long): Long
    suspend fun finishSession(sessionId: Long, notes: String? = null)
    suspend fun getSessionById(sessionId: Long): WorkoutSession?
    suspend fun getSessionsForWeekSync(startMillis: Long, endMillis: Long): List<WorkoutSession>
}

// ReportRepository.kt
interface ReportRepository {
    suspend fun getTotalWeightForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getTotalDistanceForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getAverageSpeedForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getTotalCardioTimeForWeek(startMillis: Long, endMillis: Long): Int
}

// ExerciseLogRepository.kt
interface ExerciseLogRepository {
    fun getLogsForSession(sessionId: Long): Flow<List<ExerciseLog>>
    fun getLogsForExercise(exerciseId: String): Flow<List<ExerciseLog>>
    suspend fun insertLog(log: ExerciseLog): Long
    suspend fun updateLog(log: ExerciseLog)
    suspend fun deleteLog(log: ExerciseLog)
    suspend fun getTotalWeightForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getTotalDistanceForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getAverageSpeedForWeek(startMillis: Long, endMillis: Long): Float
}

// ExerciseExclusionRepository.kt
interface ExerciseExclusionRepository {
    fun getAll(): Flow<List<String>>  // Exercise IDs
    fun getGlobalExclusions(): Flow<List<String>>
    suspend fun getExcludedIds(): Set<String>
    suspend fun addExclusion(exerciseId: String, isGlobal: Boolean)
    suspend fun addExclusions(exerciseIds: List<String>, isGlobal: Boolean)
    suspend fun removeExclusion(exerciseId: String)
    suspend fun clearNonGlobal()
    suspend fun clearAll()
}
```

---

## Use Cases (domain/usecase/)

### SaveUserProfileUseCase

```kotlin
class SaveUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile): Result<Unit> {
        return try {
            // Validações
            require(profile.heightCm in 100f..250f) { "Altura inválida" }
            require(profile.weightKg in 30f..300f) { "Peso inválido" }
            require(profile.trainingDaysPerWeek in 1..7) { "Dias inválidos" }
            require(profile.availableTimeMinutes in 20..180) { "Tempo inválido" }

            repository.saveProfile(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### GetUserProfileUseCase

```kotlin
class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> = repository.getProfile()

    suspend fun hasProfile(): Boolean = repository.hasProfile()
}
```

### GenerateTrainingPlanUseCase

Já detalhado em `spec/06-ai-integration.md`. Resumo:

```kotlin
class GenerateTrainingPlanUseCase @Inject constructor(
    private val aiSelector: AiProviderSelector,
    private val promptBuilder: PromptBuilder,
    private val responseParser: LlmResponseParser,
    private val userProfileRepository: UserProfileRepository,
    private val exerciseRepository: ExerciseRepository,
    private val trainingPlanRepository: TrainingPlanRepository,
    private val exerciseExclusionRepository: ExerciseExclusionRepository
) {
    suspend operator fun invoke(
        excludedExerciseIds: Set<String> = emptySet()
    ): Result<TrainingPlan>

    // Etapas:
    // 1. Obter perfil
    // 2. Obter exercícios disponíveis
    // 3. Montar prompt
    // 4. Chamar IA
    // 5. Parsear resposta
    // 6. Validar exercícios
    // 7. Salvar no banco
    // 8. Limpar exclusões não-globais
    // 9. Retornar plano
}
```

### RegenerateWorkoutUseCase

```kotlin
class RegenerateWorkoutUseCase @Inject constructor(
    private val aiSelector: AiProviderSelector,
    private val promptBuilder: PromptBuilder,
    private val responseParser: LlmResponseParser,
    private val userProfileRepository: UserProfileRepository,
    private val exerciseRepository: ExerciseRepository,
    private val trainingPlanRepository: TrainingPlanRepository
) {
    suspend operator fun invoke(
        planDayId: Long,
        excludedExerciseIds: Set<String> = emptySet()
    ): Result<PlanDay> {
        // 1. Obter perfil
        // 2. Obter o dia atual para saber os grupos musculares foco
        // 3. Obter exercícios disponíveis para esses grupos
        // 4. Montar prompt de regeneração de dia
        // 5. Chamar IA
        // 6. Parsear resposta
        // 7. Deletar exercícios antigos do dia
        // 8. Inserir novos exercícios
        // 9. Retornar dia atualizado
    }
}
```

### GetExercisesUseCase

```kotlin
class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    operator fun invoke(): Flow<List<Exercise>> = repository.getAllExercises()

    fun byMuscleGroup(group: MuscleGroup): Flow<List<Exercise>> =
        repository.getByMuscleGroup(group)

    fun search(query: String): Flow<List<Exercise>> =
        repository.search(query)
}
```

### LogExerciseUseCase

```kotlin
class LogExerciseUseCase @Inject constructor(
    private val exerciseLogRepository: ExerciseLogRepository,
    private val exerciseRepository: ExerciseRepository
) {
    /**
     * Registra uma série de exercício de musculação.
     */
    suspend fun logStrengthSet(
        sessionId: Long,
        exerciseId: String,
        setNumber: Int,
        weightKg: Float,
        reps: Int
    ): Result<Long> {
        return try {
            require(weightKg > 0) { "Carga deve ser maior que zero" }
            require(reps > 0) { "Repetições devem ser maior que zero" }

            val log = ExerciseLog(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                weightKg = weightKg,
                reps = reps
            )
            val id = exerciseLogRepository.insertLog(log)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra um exercício cardio.
     */
    suspend fun logCardio(
        sessionId: Long,
        exerciseId: String,
        speedKmh: Float?,
        durationSeconds: Int?,
        distanceMeters: Float?
    ): Result<Long> {
        return try {
            val log = ExerciseLog(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = 1,
                speedKmh = speedKmh,
                durationSeconds = durationSeconds,
                distanceMeters = distanceMeters
            )
            val id = exerciseLogRepository.insertLog(log)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### StartWorkoutSessionUseCase

```kotlin
class StartWorkoutSessionUseCase @Inject constructor(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(planDayId: Long): Result<Long> {
        return try {
            // Verificar se não há sessão ativa
            val activeSession = sessionRepository.getActiveSessionSync()
            if (activeSession != null) {
                return Result.failure(Exception("Já existe uma sessão ativa"))
            }
            val sessionId = sessionRepository.startSession(planDayId)
            Result.success(sessionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### FinishWorkoutSessionUseCase

```kotlin
class FinishWorkoutSessionUseCase @Inject constructor(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(
        sessionId: Long,
        notes: String? = null
    ): Result<Unit> {
        return try {
            sessionRepository.finishSession(sessionId, notes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### GetWeeklyReportUseCase

Já detalhado em `spec/08-progress-reports.md`.

```kotlin
class GetWeeklyReportUseCase @Inject constructor(
    private val sessionRepository: WorkoutSessionRepository,
    private val reportRepository: ReportRepository,
    private val planRepository: TrainingPlanRepository
) {
    suspend operator fun invoke(weekStart: LocalDate): WeeklyReport
}
```

### GetTrainingPlanUseCase

```kotlin
class GetTrainingPlanUseCase @Inject constructor(
    private val repository: TrainingPlanRepository,
    private val exerciseRepository: ExerciseRepository
) {
    /**
     * Obtém o plano ativo com todos os dias e exercícios.
     */
    fun getActivePlan(): Flow<TrainingPlan?> = repository.getActivePlan()

    /**
     * Obtém os dias do plano com exercícios preenchidos.
     */
    fun getDaysWithExercises(planId: Long): Flow<List<PlanDay>> {
        return repository.getDaysForPlan(planId).map { days ->
            days.map { day ->
                val exercises = repository.getExercisesForDaySync(day.id)
                val enrichedExercises = exercises.map { pe ->
                    val exercise = exerciseRepository.getById(pe.exerciseId)
                    pe.copy(exercise = exercise)
                }
                day.copy(exercises = enrichedExercises)
            }
        }
    }
}
```

### GetExerciseExclusionsUseCase

```kotlin
class GetExerciseExclusionsUseCase @Inject constructor(
    private val repository: ExerciseExclusionRepository
) {
    fun getAll(): Flow<List<String>> = repository.getAll()
    fun getGlobal(): Flow<List<String>> = repository.getGlobalExclusions()
    suspend fun getExcludedIds(): Set<String> = repository.getExcludedIds()
}
```

---

## Mappers (data/repository/ — dentro dos Impl)

### Entity → Domain

```kotlin
// Dentro de cada RepositoryImpl
fun UserProfileEntity.toDomain() = UserProfile(
    gender = Gender.valueOf(gender),
    heightCm = heightCm,
    weightKg = weightKg,
    trainingDaysPerWeek = trainingDaysPerWeek,
    availableTimeMinutes = availableTimeMinutes,
    trainingLevel = TrainingLevel.valueOf(trainingLevel),
    trainingGoal = TrainingGoal.valueOf(trainingGoal),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = 1, // Fixo — apenas 1 perfil
    gender = gender.name,
    heightCm = heightCm,
    weightKg = weightKg,
    trainingDaysPerWeek = trainingDaysPerWeek,
    availableTimeMinutes = availableTimeMinutes,
    trainingLevel = trainingLevel.name,
    trainingGoal = trainingGoal.name,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)

fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    primaryMuscleGroup = MuscleGroup.valueOf(primaryMuscleGroup),
    secondaryMuscleGroups = Json.decodeFromString<List<String>>(secondaryMuscleGroups)
        .mapNotNull { runCatching { MuscleGroup.valueOf(it) }.getOrNull() },
    equipment = Equipment.valueOf(equipment),
    exerciseType = ExerciseType.valueOf(exerciseType),
    description = description,
    difficulty = Difficulty.valueOf(difficulty)
)

// Padrão similar para as demais entidades...
```

---

## Notas para o Agente Codificador

1. Todas as interfaces de repository ficam em `domain/repository/`, as implementações em `data/repository/`.
2. Use Cases recebem repositórios via construtor (Hilt `@Inject constructor`).
3. Cada Use Case deve ter um único ponto de entrada — `operator fun invoke()` ou método específico.
4. Os mappers Entity ↔ Domain devem ser funções de extensão no arquivo do RepositoryImpl correspondente.
5. Validações de negócio ficam nos Use Cases, NÃO nos ViewModels.
6. Os enums devem ter `displayName` para exibição na UI e usar `name` para persistência no banco.
7. O `TrainingPlan.days` e `PlanDay.exercises` são preenchidos em queries separadas (não usar @Relation do Room — montar manualmente no repositório ou use case).
8. Todas as propriedades dos domain models são imutáveis (data class).
