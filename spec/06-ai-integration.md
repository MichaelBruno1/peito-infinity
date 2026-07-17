# 06 — Integração com Inteligência Artificial

Especificação da integração com LLM para geração de planos de treino.

## Arquitetura de IA

O app suporta dois provedores de IA, alternáveis via configurações:

```
┌─────────────────────────────────────────────────┐
│              AiProvider (Interface)              │
├─────────────────────────────────────────────────┤
│  + generateTrainingPlan(request): Result<String> │
│  + regenerateWorkout(request): Result<String>    │
│  + isAvailable(): Boolean                        │
└──────────────┬──────────────┬───────────────────┘
               │              │
    ┌──────────▼──────┐  ┌───▼──────────────┐
    │ LocalAiProvider │  │ RemoteAiProvider  │
    │  (LiteRT-LM)    │  │  (Gemini API)     │
    └─────────────────┘  └──────────────────┘
```

### AiProvider Interface

```kotlin
interface AiProvider {
    /**
     * Gera uma resposta a partir de um prompt.
     * @param prompt O prompt completo para o LLM
     * @return Result com a resposta em texto (JSON)
     */
    suspend fun generate(prompt: String): Result<String>

    /**
     * Gera uma resposta com streaming.
     * @param prompt O prompt completo para o LLM
     * @return Flow de chunks de texto
     */
    fun generateStream(prompt: String): Flow<String>

    /**
     * Verifica se o provedor está disponível.
     */
    suspend fun isAvailable(): Boolean
}
```

---

## Provedor 1: LocalAiProvider (LiteRT-LM)

### Dependência

```kotlin
implementation("com.google.ai.edge.litertlm:litertlm-android:latest.release")
```

### Configuração

```kotlin
class LocalAiProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : AiProvider {

    private var engine: Engine? = null

    private suspend fun initEngine() {
        if (engine != null) return
        withContext(Dispatchers.IO) {
            val config = EngineConfig(
                modelPath = getModelPath(),
                backend = Backend.GPU // fallback para CPU se GPU não disponível
            )
            engine = Engine(config).also { it.initialize() }
        }
    }

    override suspend fun generate(prompt: String): Result<String> {
        return try {
            initEngine()
            val conversation = engine!!.createConversation()
            val response = StringBuilder()
            conversation.sendMessageAsync(prompt).collect { chunk ->
                response.append(chunk)
            }
            Result.success(response.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generateStream(prompt: String): Flow<String> = flow {
        initEngine()
        val conversation = engine!!.createConversation()
        conversation.sendMessageAsync(prompt).collect { chunk ->
            emit(chunk)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isAvailable(): Boolean {
        return try {
            val modelFile = File(getModelPath())
            modelFile.exists() && modelFile.length() > 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getModelPath(): String {
        // Modelo armazenado no diretório interno do app
        return "${context.filesDir}/models/gemma-4-E2B-it.litertlm"
    }
}
```

### Modelo Recomendado

- **Modelo:** Gemma 4 1B (ou Gemma 3 1B para dispositivos menos potentes)
- **Formato:** `.litertlm`
- **Download:** Via HuggingFace LiteRT Community ou Google AI Edge Gallery
- **Tamanho:** ~1-2 GB
- **Requisitos:** Dispositivo com 4GB+ RAM, GPU ou NPU

### Download e Gerenciamento do Modelo

O app deve implementar uma funcionalidade de download do modelo na primeira vez que o usuário selecionar "Modelo Local":

1. Verificar se modelo existe no storage interno
2. Se não existir, mostrar diálogo informando tamanho do download
3. Baixar modelo e salvar em `context.filesDir/models/`
4. Mostrar progresso de download
5. Após download, inicializar o engine

**Nota:** Para simplificar a implementação inicial, o modelo pode ser incluído como asset (se o tamanho permitir) ou baixado manualmente via ADB durante desenvolvimento.

---

## Provedor 2: RemoteAiProvider (Gemini API)

### Dependência

```kotlin
implementation("com.google.genai:google-genai-kotlin:0.1.0")
```

### Configuração

```kotlin
class RemoteAiProvider @Inject constructor() : AiProvider {

    private val client by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    override suspend fun generate(prompt: String): Result<String> {
        return try {
            val response = client.generateContent(prompt)
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generateStream(prompt: String): Flow<String> = flow {
        client.generateContentStream(prompt).collect { chunk ->
            chunk.text?.let { emit(it) }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isAvailable(): Boolean {
        return BuildConfig.GEMINI_API_KEY.isNotBlank()
    }
}
```

### Chave API

- **IMPORTANTE:** A chave API Gemini é embutida via `BuildConfig.GEMINI_API_KEY`
- Configurada em `local.properties` ou `secrets.properties`
- **NÃO** deve haver campo de input na UI para a chave
- O usuário apenas seleciona "Modelo Local" ou "Modelo Externo"

---

## Módulo Hilt para IA

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    companion object {
        @Provides
        @Singleton
        fun provideLocalAiProvider(
            @ApplicationContext context: Context
        ): LocalAiProvider = LocalAiProvider(context)

        @Provides
        @Singleton
        fun provideRemoteAiProvider(): RemoteAiProvider = RemoteAiProvider()

        @Provides
        @Singleton
        fun provideAiProviderSelector(
            localProvider: LocalAiProvider,
            remoteProvider: RemoteAiProvider,
            preferences: AppPreferences
        ): AiProviderSelector = AiProviderSelector(
            localProvider, remoteProvider, preferences
        )
    }
}
```

### AiProviderSelector

```kotlin
class AiProviderSelector @Inject constructor(
    private val localProvider: LocalAiProvider,
    private val remoteProvider: RemoteAiProvider,
    private val preferences: AppPreferences
) {
    fun getCurrentProvider(): AiProvider {
        return when (preferences.getAiModeSync()) {
            AiMode.LOCAL -> localProvider
            AiMode.REMOTE -> remoteProvider
        }
    }

    suspend fun isCurrentProviderAvailable(): Boolean {
        return getCurrentProvider().isAvailable()
    }
}
```

---

## Construção de Prompts

### PromptBuilder

```kotlin
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
                "## $group\n" + exercises.joinToString("\n") { "- ${it.id}: ${it.name}" }
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
5. Para ${profile.trainingGoal.description}:
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
                exercise.primaryMuscleGroup in targetMuscleGroups.map { it.name } ||
                exercise.secondaryMuscleGroups.any { it in targetMuscleGroups.map { g -> g.name } }
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
  - Progressão de carga consistente"""

            TrainingLevel.ADVANCED -> """
- Avançado (> 2 anos):
  - 6-8+ exercícios por dia
  - Técnicas avançadas: drop-sets, supersets, rest-pause
  - Periodização é recomendada
  - Variação de estímulos"""
        }
    }
}
```

---

## Parsing da Resposta da LLM

### Modelos de Resposta (JSON)

```kotlin
@Serializable
data class LlmPlanResponse(
    val planName: String,
    val days: List<LlmDayResponse>
)

@Serializable
data class LlmDayResponse(
    val dayNumber: Int? = null,
    val dayName: String,
    val focusMuscleGroups: List<String>,
    val estimatedDurationMinutes: Int,
    val exercises: List<LlmExerciseResponse>
)

@Serializable
data class LlmExerciseResponse(
    val exerciseId: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String? = null
)
```

### Parser

```kotlin
class LlmResponseParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Extrai JSON da resposta da LLM, removendo markdown e texto extra.
     */
    fun extractJson(rawResponse: String): String {
        // Tentar extrair JSON de blocos de código markdown
        val codeBlockRegex = """```(?:json)?\s*\n?([\s\S]*?)\n?```""".toRegex()
        val codeBlockMatch = codeBlockRegex.find(rawResponse)
        if (codeBlockMatch != null) {
            return codeBlockMatch.groupValues[1].trim()
        }

        // Tentar encontrar JSON puro (começa com { e termina com })
        val jsonRegex = """\{[\s\S]*\}""".toRegex()
        val jsonMatch = jsonRegex.find(rawResponse)
        if (jsonMatch != null) {
            return jsonMatch.value.trim()
        }

        return rawResponse.trim()
    }

    fun parseFullPlan(rawResponse: String): Result<LlmPlanResponse> {
        return try {
            val jsonStr = extractJson(rawResponse)
            val plan = json.decodeFromString<LlmPlanResponse>(jsonStr)
            Result.success(plan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun parseDayPlan(rawResponse: String): Result<LlmDayResponse> {
        return try {
            val jsonStr = extractJson(rawResponse)
            val day = json.decodeFromString<LlmDayResponse>(jsonStr)
            Result.success(day)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## Fluxo Completo de Geração

### GenerateTrainingPlanUseCase

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
    ): Result<TrainingPlan> {
        // 1. Obter perfil do usuário
        val profile = userProfileRepository.getProfileSync()
            ?: return Result.failure(Exception("Perfil não encontrado"))

        // 2. Obter exercícios disponíveis
        val exercises = exerciseRepository.getAllExercisesSync()

        // 3. Montar prompt
        val prompt = promptBuilder.buildFullPlanPrompt(
            profile = profile,
            availableExercises = exercises,
            excludedExerciseIds = excludedExerciseIds
        )

        // 4. Chamar IA
        val aiProvider = aiSelector.getCurrentProvider()
        val response = aiProvider.generate(prompt)

        if (response.isFailure) {
            return Result.failure(response.exceptionOrNull()!!)
        }

        // 5. Parsear resposta
        val parsed = responseParser.parseFullPlan(response.getOrThrow())

        if (parsed.isFailure) {
            return Result.failure(parsed.exceptionOrNull()!!)
        }

        val llmPlan = parsed.getOrThrow()

        // 6. Validar exercícios (verificar se IDs existem)
        val validExerciseIds = exercises.map { it.id }.toSet()
        val validatedDays = llmPlan.days.map { day ->
            day.copy(
                exercises = day.exercises.filter { it.exerciseId in validExerciseIds }
            )
        }

        // 7. Salvar no banco de dados
        val planEntity = TrainingPlanEntity(
            name = llmPlan.planName,
            isActive = true,
            trainingGoal = profile.trainingGoal.name,
            trainingLevel = profile.trainingLevel.name,
            daysPerWeek = profile.trainingDaysPerWeek,
            createdAt = System.currentTimeMillis(),
            rawLlmResponse = response.getOrThrow()
        )

        val planId = trainingPlanRepository.insertAndActivate(planEntity)

        // 8. Salvar dias e exercícios
        validatedDays.forEachIndexed { index, dayResponse ->
            val dayEntity = PlanDayEntity(
                planId = planId,
                dayNumber = dayResponse.dayNumber ?: (index + 1),
                dayName = dayResponse.dayName,
                focusMuscleGroups = Json.encodeToString(dayResponse.focusMuscleGroups),
                estimatedDurationMinutes = dayResponse.estimatedDurationMinutes
            )
            val dayId = trainingPlanRepository.insertDay(dayEntity)

            val exerciseEntities = dayResponse.exercises.mapIndexed { exIndex, ex ->
                PlanExerciseEntity(
                    planDayId = dayId,
                    exerciseId = ex.exerciseId,
                    orderIndex = exIndex,
                    sets = ex.sets,
                    reps = ex.reps,
                    restSeconds = ex.restSeconds,
                    notes = ex.notes
                )
            }
            trainingPlanRepository.insertExercises(exerciseEntities)
        }

        // 9. Limpar exclusões não-globais
        exerciseExclusionRepository.clearNonGlobal()

        // 10. Retornar plano criado
        return Result.success(trainingPlanRepository.getPlanById(planId))
    }
}
```

---

## Tratamento de Erros

| Erro | Causa | Ação do App |
|------|-------|-------------|
| Modelo não encontrado | LiteRT-LM sem modelo baixado | Exibir diálogo para download ou trocar para remoto |
| Sem internet | API Gemini sem conexão | Exibir erro + sugerir usar modelo local |
| Resposta inválida | LLM retornou JSON malformado | Retry automático (até 2x), depois exibir erro |
| Exercício inválido | LLM usou ID que não existe | Filtrar exercícios inválidos silenciosamente |
| Timeout | Inferência demorou demais | Timeout de 120s (local) / 60s (remoto) |
| API key inválida | Chave Gemini incorreta | Exibir erro genérico "Erro no serviço de IA" |
| Quota excedida | Rate limit da API Gemini | Exibir "Tente novamente em alguns minutos" |

### Retry Logic

```kotlin
suspend fun <T> retryWithBackoff(
    maxRetries: Int = 2,
    initialDelayMs: Long = 1000,
    block: suspend () -> Result<T>
): Result<T> {
    var lastException: Throwable? = null
    repeat(maxRetries + 1) { attempt ->
        val result = block()
        if (result.isSuccess) return result
        lastException = result.exceptionOrNull()
        if (attempt < maxRetries) {
            delay(initialDelayMs * (attempt + 1))
        }
    }
    return Result.failure(lastException ?: Exception("Falha após $maxRetries tentativas"))
}
```

---

## DataStore — Preferência de Modo IA

```kotlin
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val AI_MODE_KEY = stringPreferencesKey("ai_mode")
        private val Context.dataStore by preferencesDataStore(name = "peito_preferences")
    }

    val aiMode: Flow<AiMode> = dataStore.data.map { prefs ->
        val mode = prefs[AI_MODE_KEY] ?: AiMode.REMOTE.name
        AiMode.valueOf(mode)
    }

    suspend fun setAiMode(mode: AiMode) {
        dataStore.edit { prefs ->
            prefs[AI_MODE_KEY] = mode.name
        }
    }

    fun getAiModeSync(): AiMode {
        return runBlocking {
            aiMode.first()
        }
    }
}
```

---

## Notas para o Agente Codificador

1. **Chave API Gemini:** NUNCA exibir na UI. Acessar apenas via `BuildConfig.GEMINI_API_KEY`.
2. **Modelo local:** Para desenvolvimento, copiar o modelo via ADB: `adb push gemma.litertlm /data/data/com.example.peitoinfinity/files/models/`
3. **Timeout:** Implementar timeout de 120s para local e 60s para remoto.
4. **Validação:** SEMPRE validar os IDs de exercícios retornados pela LLM contra o banco local.
5. **Retry:** Implementar retry automático (2x) com backoff para respostas JSON inválidas.
6. **Streaming:** Na UI de loading, mostrar progresso parcial usando `generateStream()` quando possível.
7. **Fallback:** Se o provedor selecionado não estiver disponível, NÃO fazer fallback automático. Exibir erro e sugerir trocar o modo.
8. **Prompt:** O prompt inclui a lista completa de IDs de exercícios disponíveis para que a LLM use apenas exercícios válidos.
9. **JSON parsing:** Usar o `extractJson()` para lidar com LLMs que envolvem a resposta em markdown code blocks.
10. O modelo Gemini remoto sugerido é `gemini-2.0-flash` por ser rápido e econômico. Pode ser trocado para `gemini-2.5-flash` se necessário.
