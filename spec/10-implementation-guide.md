# 10 — Guia de Implementação

Instruções detalhadas para o agente codificador implementar o projeto PeitoInfinity.

## Regras Gerais

1. **Linguagem:** Todo o código deve ser em Kotlin.
2. **UI:** Todo texto visível ao usuário deve estar em Português Brasileiro.
3. **Comentários:** Comentários no código podem ser em inglês ou português.
4. **Imports:** Usar `androidx.room3.*` (NÃO `androidx.room.*`).
5. **Serialização:** Usar `kotlinx.serialization` (NÃO Gson ou Moshi).
6. **DI:** Usar Hilt com `@HiltViewModel`, `@Inject constructor`, `@Module`, `@InstallIn`.
7. **Async:** Usar Kotlin Coroutines + Flow. NÃO usar LiveData.
8. **State:** Usar `StateFlow` nos ViewModels, coletar com `collectAsStateWithLifecycle()`.
9. **Navegação:** Usar Navigation Compose 2.9.8.
10. **Tema:** SOMENTE tema escuro. Sem opção de tema claro.
11. **Testes:** Não é necessário implementar testes nesta fase.

## Ordem de Implementação

A implementação deve seguir esta ordem rigorosa para minimizar erros de compilação:

### Fase 1: Fundação (prioridade ALTA)

#### 1.1 — Configuração de Build
- [ ] Atualizar `gradle/libs.versions.toml` conforme `spec/02-dependencies.md`
- [ ] Atualizar `build.gradle.kts` (raiz) conforme `spec/02-dependencies.md`
- [ ] Atualizar `app/build.gradle.kts` conforme `spec/02-dependencies.md`
- [ ] Atualizar `AndroidManifest.xml` conforme `spec/02-dependencies.md`
- [ ] Criar `PeitoInfinityApplication.kt` com `@HiltAndroidApp`
- [ ] Sincronizar Gradle e resolver erros de dependências

#### 1.2 — Domain Models e Enums
- [ ] Criar todos os enums em `domain/model/`: TrainingGoal, TrainingLevel, MuscleGroup, Equipment, ExerciseType, Difficulty, AiMode
- [ ] Criar todas as data classes em `domain/model/`: UserProfile, Exercise, TrainingPlan, PlanDay, PlanExercise, WorkoutSession, ExerciseLog, WeeklyReport
- [ ] Conforme especificado em `spec/09-domain-layer.md`

#### 1.3 — Database (Room 3)
- [ ] Criar todas as Entities em `data/local/database/entity/` conforme `spec/03-data-model.md`
- [ ] Criar `Converters.kt` (TypeConverters)
- [ ] Criar todos os DAOs em `data/local/database/dao/` conforme `spec/03-data-model.md`
- [ ] Criar `PeitoInfinityDatabase.kt` conforme `spec/03-data-model.md`
- [ ] Criar `ExerciseData.kt` com todos os 182 exercícios conforme `spec/07-exercise-database.md`
- [ ] Implementar pré-população via `RoomDatabase.Callback`
- [ ] Compilar e verificar se o Room gera sem erros

#### 1.4 — Repository Interfaces
- [ ] Criar todas as interfaces de repositório em `domain/repository/` conforme `spec/09-domain-layer.md`

#### 1.5 — Repository Implementations
- [ ] Criar `UserProfileRepositoryImpl.kt` com mappers Entity ↔ Domain
- [ ] Criar `ExerciseRepositoryImpl.kt`
- [ ] Criar `TrainingPlanRepositoryImpl.kt`
- [ ] Criar `WorkoutSessionRepositoryImpl.kt`
- [ ] Criar `ReportRepositoryImpl.kt` (ou ExerciseLogRepositoryImpl)
- [ ] Criar `ExerciseExclusionRepositoryImpl.kt`

#### 1.6 — Dependency Injection
- [ ] Criar `DatabaseModule.kt` (provê Database e todos os DAOs)
- [ ] Criar `AppModule.kt` (provê Repositories, DataStore, etc)
- [ ] Compilar e verificar DI

### Fase 2: Tema e Navegação (prioridade ALTA)

#### 2.1 — Design System
- [ ] Criar `presentation/theme/Color.kt` conforme `spec/05-design-system.md`
- [ ] Criar `presentation/theme/Type.kt` com fonte Inter
- [ ] Criar `presentation/theme/Shape.kt`
- [ ] Criar `presentation/theme/Theme.kt` com PeitoInfinityTheme
- [ ] Criar `util/Constants.kt` com PeitoDimens

#### 2.2 — Navegação
- [ ] Criar `presentation/navigation/Screen.kt` (sealed class com rotas)
- [ ] Criar `presentation/navigation/NavGraph.kt` com NavHost
- [ ] Criar `presentation/PeitoInfinityApp.kt` (Theme + NavHost + BottomBar)
- [ ] Atualizar `presentation/MainActivity.kt` com @AndroidEntryPoint e setContent

### Fase 3: Telas Básicas (prioridade ALTA)

#### 3.1 — Componentes Reutilizáveis
- [ ] Criar `PeitoTopBar.kt`
- [ ] Criar `PeitoBottomBar.kt`
- [ ] Criar `ExerciseCard.kt`
- [ ] Criar `PlanDayCard.kt`
- [ ] Criar `LoadingIndicator.kt`
- [ ] Criar `StatCard.kt`
- [ ] Criar `MuscleGroupSelector.kt`
- [ ] Criar `ExerciseExclusionDialog.kt`

#### 3.2 — Tela de Onboarding
- [ ] Criar `OnboardingViewModel.kt`
- [ ] Criar `OnboardingScreen.kt` com formulário multi-step
- [ ] Conforme especificado em `spec/04-screens.md`

#### 3.3 — Tela de Perfil
- [ ] Criar `ProfileViewModel.kt`
- [ ] Criar `ProfileScreen.kt`

#### 3.4 — Tela de Configurações
- [ ] Criar `AppPreferences.kt` (DataStore)
- [ ] Criar `SettingsViewModel.kt`
- [ ] Criar `SettingsScreen.kt` com toggle de modo IA

#### 3.5 — Tela de Exercícios
- [ ] Criar `ExerciseListViewModel.kt`
- [ ] Criar `ExerciseListScreen.kt` com busca e filtros

### Fase 4: Integração IA (prioridade ALTA)

#### 4.1 — Providers de IA
- [ ] Criar `data/ai/AiProvider.kt` (interface)
- [ ] Criar `data/ai/LocalAiProvider.kt` (LiteRT-LM)
- [ ] Criar `data/ai/RemoteAiProvider.kt` (Gemini API)
- [ ] Criar `data/ai/PromptBuilder.kt`
- [ ] Criar `data/ai/LlmResponseParser.kt`
- [ ] Criar `data/ai/AiProviderSelector.kt`
- [ ] Criar `di/AiModule.kt`
- [ ] Conforme especificado em `spec/06-ai-integration.md`

#### 4.2 — Use Cases de IA
- [ ] Criar `GenerateTrainingPlanUseCase.kt`
- [ ] Criar `RegenerateWorkoutUseCase.kt`

### Fase 5: Telas de Plano e Treino (prioridade ALTA)

#### 5.1 — Plano de Treino
- [ ] Criar `TrainingPlanViewModel.kt`
- [ ] Criar `TrainingPlanScreen.kt`
- [ ] Criar `PlanDayDetailViewModel.kt`
- [ ] Criar `PlanDayDetailScreen.kt`

#### 5.2 — Treino (Workout)
- [ ] Criar `WorkoutViewModel.kt` com timer
- [ ] Criar `WorkoutScreen.kt` com registro de séries
- [ ] Implementar registro de musculação (carga + reps)
- [ ] Implementar registro de cardio (velocidade + tempo + distância)

### Fase 6: Relatórios (prioridade MÉDIA)

#### 6.1 — Use Cases de Relatório
- [ ] Criar `GetWeeklyReportUseCase.kt`
- [ ] Criar `LogExerciseUseCase.kt`
- [ ] Criar demais Use Cases restantes

#### 6.2 — Tela de Relatório
- [ ] Criar `ReportViewModel.kt`
- [ ] Criar `ReportScreen.kt` com cards de métricas
- [ ] Implementar seletor de semana

### Fase 7: Polish (prioridade MÉDIA)

#### 7.1 — Animações
- [ ] Implementar transições de tela suaves
- [ ] Implementar staggered animations nos cards
- [ ] Implementar count-up animation nas métricas
- [ ] Implementar expand/collapse animation nos exercícios
- [ ] Implementar loading com mensagens rotativas

#### 7.2 — Edge Cases
- [ ] Tratar sessão ativa ao reabrir app
- [ ] Tratar erro de IA com retry
- [ ] Tratar modelo local não disponível
- [ ] Tratar sem internet para modo remoto
- [ ] Validar inputs nos formulários

---

## Registro de Progresso

O agente codificador deve atualizar o arquivo `spec/11-progress.md` após completar cada item.

Formato de atualização:
```markdown
- [x] Item completado ← data
- [/] Item em andamento
- [ ] Item pendente
```

---

## Verificação

Após completar cada fase, o agente deve:

1. **Compilar** o projeto: `./gradlew assembleDebug`
2. **Verificar** que não há erros de compilação
3. **Listar** quaisquer warnings importantes
4. **Atualizar** o progresso em `spec/11-progress.md`

---

## Padrão de ViewModel

Todos os ViewModels devem seguir este padrão:

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val someUseCase: SomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExampleUiState())
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                someUseCase().collect { data ->
                    _uiState.update { it.copy(data = data, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onAction(action: ExampleAction) {
        when (action) {
            is ExampleAction.Something -> handleSomething()
        }
    }
}
```

## Padrão de Screen Composable

```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExampleContent(
        state = uiState,
        onAction = viewModel::onAction,
        onNavigate = onNavigate
    )
}

@Composable
private fun ExampleContent(
    state: ExampleUiState,
    onAction: (ExampleAction) -> Unit,
    onNavigate: (String) -> Unit
) {
    // UI implementation
}
```

---

## Referências de Especificação

| Aspecto | Documento |
|---------|-----------|
| Visão geral e arquitetura | `spec/01-overview.md` |
| Dependências e build | `spec/02-dependencies.md` |
| Banco de dados Room | `spec/03-data-model.md` |
| Telas e navegação | `spec/04-screens.md` |
| Tema e design | `spec/05-design-system.md` |
| Integração IA | `spec/06-ai-integration.md` |
| Lista de exercícios | `spec/07-exercise-database.md` |
| Progresso e relatórios | `spec/08-progress-reports.md` |
| Domain layer | `spec/09-domain-layer.md` |
| Este guia | `spec/10-implementation-guide.md` |
| Registro de progresso | `spec/11-progress.md` |
