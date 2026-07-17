# 04 — Telas e Navegação

Especificação de todas as telas, componentes e fluxo de navegação do PeitoInfinity.

## Navegação

### Rotas (Sealed Class `Screen`)

```kotlin
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Plan : Screen("plan")
    object PlanDayDetail : Screen("plan_day_detail/{dayId}") {
        fun createRoute(dayId: Long) = "plan_day_detail/$dayId"
    }
    object Workout : Screen("workout/{sessionId}") {
        fun createRoute(sessionId: Long) = "workout/$sessionId"
    }
    object Exercises : Screen("exercises")
    object Report : Screen("report")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}
```

### Bottom Navigation

O app possui uma barra de navegação inferior com 4 itens:

| Ícone | Label | Rota | Descrição |
|-------|-------|------|-----------|
| FitnessCenter | Treino | `plan` | Plano de treino ativo |
| FormatListBulleted | Exercícios | `exercises` | Catálogo de exercícios |
| BarChart | Relatório | `report` | Relatórios semanais |
| Settings | Ajustes | `settings` | Configurações |

### Fluxo de Navegação

```
App Start
    │
    ├── [Perfil existe?]
    │       │
    │       ├── NÃO → OnboardingScreen
    │       │           │
    │       │           └── Salvar perfil → PlanScreen (gerar primeiro plano)
    │       │
    │       └── SIM → PlanScreen
    │
    ├── PlanScreen
    │       │
    │       ├── [Plano ativo existe?]
    │       │       ├── NÃO → Botão "Gerar Plano" → Diálogo de exclusões → Loading → Plano gerado
    │       │       └── SIM → Exibir plano com dias de treino
    │       │
    │       ├── Tap em dia → PlanDayDetailScreen
    │       │       │
    │       │       ├── Ver exercícios do dia
    │       │       ├── "Iniciar Treino" → WorkoutScreen
    │       │       └── "Regenerar Este Treino" → Diálogo exclusões → Loading → Novo treino
    │       │
    │       └── FAB "Novo Plano" → Diálogo exclusões → Loading → Novo plano
    │
    ├── WorkoutScreen
    │       │
    │       ├── Lista de exercícios do dia
    │       ├── Cada exercício expandível para registrar séries
    │       │       ├── Musculação: campos Carga (kg) + Reps
    │       │       └── Cardio: campos Velocidade + Tempo + Distância
    │       └── "Finalizar Treino" → Voltar para PlanDayDetail
    │
    ├── ExerciseListScreen
    │       │
    │       ├── Barra de busca
    │       ├── Filtros por grupo muscular (chips)
    │       └── Lista de exercícios com cards
    │
    ├── ReportScreen
    │       │
    │       ├── Seletor de semana (anterior/próxima)
    │       └── Cards com métricas semanais
    │
    ├── SettingsScreen
    │       │
    │       ├── Modo IA (toggle Local/Externo)
    │       └── "Editar Perfil" → ProfileScreen
    │
    └── ProfileScreen
            │
            └── Formulário de edição do perfil
```

### Transições de Tela

Todas as transições entre telas devem usar animações suaves:

```kotlin
// Transição padrão para navegação horizontal (push/pop)
val defaultEnterTransition = fadeIn(
    animationSpec = tween(300)
) + slideInHorizontally(
    initialOffsetX = { it / 4 },
    animationSpec = tween(300)
)

val defaultExitTransition = fadeOut(
    animationSpec = tween(300)
) + slideOutHorizontally(
    targetOffsetX = { -it / 4 },
    animationSpec = tween(300)
)

val defaultPopEnterTransition = fadeIn(
    animationSpec = tween(300)
) + slideInHorizontally(
    initialOffsetX = { -it / 4 },
    animationSpec = tween(300)
)

val defaultPopExitTransition = fadeOut(
    animationSpec = tween(300)
) + slideOutHorizontally(
    targetOffsetX = { it / 4 },
    animationSpec = tween(300)
)

// Para Bottom Navigation — usar crossfade
val bottomNavTransition = fadeIn(tween(200)) to fadeOut(tween(200))
```

---

## Tela 1: OnboardingScreen

**Rota:** `onboarding`
**Exibida:** Apenas no primeiro acesso (quando não há perfil salvo)

### Layout

Tela de formulário multi-step em scroll vertical com os seguintes campos:

#### Step 1 — Dados Físicos
- **Título:** "Vamos começar!"
- **Subtítulo:** "Conte-nos sobre você para criar o treino ideal"
- **Campo:** Sexo — 2 cards selecionáveis (single select)
  - ♂️ **Masculino**
  - ♀️ **Feminino**
- **Campo:** Altura (cm) — NumberField com sufixo "cm"
- **Campo:** Peso (kg) — NumberField com sufixo "kg"

#### Step 2 — Disponibilidade
- **Título:** "Sua rotina de treinos"
- **Campo:** Dias por semana — Slider 1-7 com labels
- **Campo:** Tempo disponível — Slider 20-120min com step de 10min

#### Step 3 — Nível
- **Título:** "Qual seu nível de experiência?"
- **Opções:** 3 cards selecionáveis (single select)
  - 🟢 **Iniciante** — "Treino há menos de 6 meses"
  - 🟡 **Intermediário** — "Treino entre 6 meses e 2 anos"
  - 🔴 **Avançado** — "Treino há mais de 2 anos"

#### Step 4 — Objetivo
- **Título:** "Qual seu objetivo principal?"
- **Opções:** 4 cards selecionáveis (single select)
  - 🔥 **Perda de Peso** — "Emagrecer e definir"
  - 💪 **Ganho de Massa** — "Hipertrofia muscular"
  - 🏋️ **Ganho de Força** — "Aumentar cargas e performance"
  - ⚖️ **Manutenção** — "Manter o peso e forma atual"

#### Botão
- **Texto:** "Criar Meu Plano" (habilitado apenas com todos os campos preenchidos)
- **Ação:** Salvar perfil → Navegar para PlanScreen

### ViewModel State

```kotlin
data class OnboardingUiState(
    val currentStep: Int = 1,
    val gender: Gender? = null,
    val heightCm: String = "",
    val weightKg: String = "",
    val trainingDaysPerWeek: Int = 3,
    val availableTimeMinutes: Int = 60,
    val trainingLevel: TrainingLevel? = null,
    val trainingGoal: TrainingGoal? = null,
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
```

---

## Tela 2: TrainingPlanScreen (Home)

**Rota:** `plan`
**Bottom Nav:** Primeiro item (Treino)

### Layout

#### Quando NÃO há plano ativo:
- Ilustração/ícone central
- **Texto:** "Nenhum plano de treino ativo"
- **Subtexto:** "Gere um plano personalizado com IA"
- **Botão:** "Gerar Plano de Treino" (primário, grande)

#### Quando HÁ plano ativo:
- **Header:** Nome do plano + data de criação
- **Info chips:** Objetivo, Nível, Dias/semana
- **Lista de dias:** Cards com informações de cada dia de treino

Cada **PlanDayCard** exibe:
- Número do dia ("Treino A", "Treino B"...)
- Nome/foco do dia ("Peito e Tríceps")
- Duração estimada (ex: "~45min")
- Número de exercícios
- Grupos musculares (chips coloridos)
- Tap → navega para PlanDayDetailScreen

- **FAB:** ícone de refresh/add → Opções:
  - "Gerar Novo Plano Completo"
  - "Regenerar Treino Específico" → abre seletor de dia

### Fluxo de Geração de Plano

1. Usuário toca "Gerar Plano"
2. Abre `ExerciseExclusionDialog`:
   - **Título:** "Excluir exercícios?"
   - **Subtítulo:** "Selecione exercícios que você NÃO quer no plano"
   - Lista de exercícios agrupados por grupo muscular
   - Cada exercício com checkbox
   - Busca por nome
   - Botão "Gerar Plano" / "Pular" (gerar sem exclusões)
3. Loading screen com animação e mensagem "Gerando seu plano personalizado..."
4. Plano exibido

### ViewModel State

```kotlin
data class TrainingPlanUiState(
    val activePlan: TrainingPlan? = null,
    val planDays: List<PlanDay> = emptyList(),
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val showExclusionDialog: Boolean = false,
    val excludedExercises: Set<String> = emptySet(), // IDs dos exercícios excluídos
    val regeneratingDayId: Long? = null, // null = plano inteiro, Long = dia específico
    val error: String? = null,
    val generationProgress: String? = null // Mensagem de progresso
)
```

---

## Tela 3: PlanDayDetailScreen

**Rota:** `plan_day_detail/{dayId}`

### Layout

- **TopBar:** Nome do dia (ex: "Treino A — Peito e Tríceps") com back arrow
- **Info:** Duração estimada, número de exercícios
- **Lista de exercícios:** Ordenados por `orderIndex`

Cada exercício exibe:
- Nome do exercício
- Séries × Repetições (ex: "4 × 12")
- Intervalo de descanso (ex: "60s")
- Notas da LLM (se houver) em texto menor/italic
- Equipamento necessário (chip)

- **Botão primário:** "Iniciar Treino" → cria WorkoutSession → navega para WorkoutScreen
- **Botão secundário:** "Regenerar Este Treino" → ExclusionDialog → Loading → Novo treino para este dia

### ViewModel State

```kotlin
data class PlanDayDetailUiState(
    val planDay: PlanDay? = null,
    val exercises: List<PlanExercise> = emptyList(),
    val isLoading: Boolean = true,
    val isRegenerating: Boolean = false,
    val showExclusionDialog: Boolean = false,
    val excludedExercises: Set<String> = emptySet(),
    val error: String? = null
)
```

---

## Tela 4: WorkoutScreen

**Rota:** `workout/{sessionId}`

### Layout

- **TopBar:** "Treino em Andamento" com timer running
- **Timer:** Tempo decorrido desde o início da sessão (HH:MM:SS)
- **Lista de exercícios do dia** (expandíveis)

Cada exercício:
- Header com nome, séries planejadas e checkbox de conclusão
- **Expandido:** Formulário para cada série
  - **Musculação:**
    - Série # (1, 2, 3...)
    - Campo: Carga (kg) — NumberDecimalField
    - Campo: Reps — NumberField
    - Botão "✓ Registrar Série"
  - **Cardio:**
    - Campo: Velocidade (km/h) — NumberDecimalField
    - Campo: Tempo (min) — NumberField
    - Campo: Distância (m) — NumberField
    - Botão "✓ Registrar"

- **Indicador de progresso:** "3 de 8 exercícios concluídos"
- **Botão:** "Finalizar Treino" → confirma → salva → volta para PlanDayDetail

### ViewModel State

```kotlin
data class WorkoutUiState(
    val session: WorkoutSession? = null,
    val exercises: List<WorkoutExerciseUi> = emptyList(),
    val elapsedTimeSeconds: Long = 0,
    val completedExercisesCount: Int = 0,
    val totalExercisesCount: Int = 0,
    val isFinishing: Boolean = false,
    val error: String? = null
)

data class WorkoutExerciseUi(
    val planExercise: PlanExercise,
    val exercise: Exercise,
    val logs: List<ExerciseLog> = emptyList(),
    val isExpanded: Boolean = false,
    val currentWeightKg: String = "",
    val currentReps: String = "",
    val currentSpeedKmh: String = "",
    val currentDurationMin: String = "",
    val currentDistanceM: String = "",
    val isCompleted: Boolean = false
)
```

---

## Tela 5: ExerciseListScreen

**Rota:** `exercises`
**Bottom Nav:** Segundo item (Exercícios)

### Layout

- **TopBar:** "Exercícios"
- **SearchBar:** Campo de busca com ícone de lupa
- **Filtros:** Row horizontal scrollável de FilterChips por grupo muscular:
  - Todos, Peito, Costas, Ombros, Bíceps, Tríceps, Quadríceps, Posterior, Glúteos, Panturrilha, Abdômen, Antebraço, Cardio
- **Lista:** LazyColumn com ExerciseCards agrupados por grupo muscular

Cada **ExerciseCard** exibe:
- Nome do exercício
- Grupo muscular principal (badge colorido)
- Grupos secundários (chips menores)
- Equipamento necessário
- Nível de dificuldade (ícone/cor)
- Descrição (colapsável)

### ViewModel State

```kotlin
data class ExerciseListUiState(
    val exercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscleGroup: MuscleGroup? = null, // null = todos
    val isLoading: Boolean = true
)
```

---

## Tela 6: ReportScreen

**Rota:** `report`
**Bottom Nav:** Terceiro item (Relatório)

### Layout

- **TopBar:** "Relatório Semanal"
- **Seletor de semana:** `< Semana de 14/07 - 20/07 >`
- **Cards de métricas** (grid 2×3 ou lista):

| Métrica | Ícone | Unidade | Cálculo |
|---------|-------|---------|---------|
| Tempo Total de Treino | Timer | h:mm | Soma (finishedAt - startedAt) de todas as sessões da semana |
| Sessões Realizadas | FitnessCenter | un | Count de sessões finalizadas |
| Peso Total Levantado | FitnessCenter | kg | Soma (weightKg × reps) de todos os logs |
| Distância Total | DirectionsRun | km | Soma distanceMeters / 1000 |
| Velocidade Média | Speed | km/h | Média speedKmh dos logs cardio |
| Tempo Médio Descanso | HourglassEmpty | seg | Estimativa baseada em restSeconds dos exercícios |

- **Mensagem quando sem dados:** "Nenhum treino registrado nesta semana"

### ViewModel State

```kotlin
data class ReportUiState(
    val weekStartDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val weekEndDate: LocalDate = LocalDate.now().with(DayOfWeek.SUNDAY),
    val totalTrainingTimeMinutes: Int = 0,
    val sessionsCount: Int = 0,
    val totalWeightKg: Float = 0f,
    val totalDistanceKm: Float = 0f,
    val averageSpeedKmh: Float = 0f,
    val averageRestSeconds: Int = 0,
    val isLoading: Boolean = true,
    val hasData: Boolean = false
)
```

---

## Tela 7: SettingsScreen

**Rota:** `settings`
**Bottom Nav:** Quarto item (Ajustes)

### Layout

- **TopBar:** "Ajustes"
- **Seção: Inteligência Artificial**
  - **Toggle/Radio:** "Modelo de IA"
    - 🏠 **Modelo Local** — "Roda no dispositivo (sem internet)"
    - ☁️ **Modelo Externo** — "Usa servidor remoto (requer internet)"
  - **Status:** Indicador se modelo local está disponível/baixado
- **Seção: Perfil**
  - **Item:** "Editar Perfil" → navega para ProfileScreen
  - **Info:** Exibe resumo do perfil atual (altura, peso, nível, objetivo)
- **Seção: Sobre**
  - **Item:** Versão do app
  - **Item:** Licenças

### ViewModel State

```kotlin
data class SettingsUiState(
    val aiMode: AiMode = AiMode.LOCAL,
    val isLocalModelAvailable: Boolean = false,
    val isLocalModelDownloading: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null
)
```

**IMPORTANTE:** O campo de chave API do Gemini NÃO deve aparecer na UI. A chave é embutida via BuildConfig.

---

## Tela 8: ProfileScreen

**Rota:** `profile`
**Acesso:** Via SettingsScreen

### Layout

Formulário de edição com os mesmos campos do Onboarding, mas pré-preenchidos com dados atuais:

- Sexo (cards selecionáveis: Masculino / Feminino)
- Altura (cm)
- Peso (kg)
- Dias por semana (slider)
- Tempo disponível (slider)
- Nível de treinamento (cards selecionáveis)
- Objetivo (cards selecionáveis)
- **Botão:** "Salvar Alterações"

### ViewModel State

```kotlin
data class ProfileUiState(
    val gender: Gender? = null,
    val heightCm: String = "",
    val weightKg: String = "",
    val trainingDaysPerWeek: Int = 3,
    val availableTimeMinutes: Int = 60,
    val trainingLevel: TrainingLevel? = null,
    val trainingGoal: TrainingGoal? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
```

---

## Componentes Reutilizáveis

### PeitoTopBar
- TopAppBar customizado com título e ações opcionais
- Cor de fundo: `Surface` do tema
- Animação de elevação ao scrollar

### PeitoBottomBar
- NavigationBar com 4 itens
- Indicador animado ao trocar de tab
- Badges opcionais (ex: sessão ativa)

### ExerciseCard
- Card com informações do exercício
- Borda com cor do grupo muscular
- Animação de expand/collapse para descrição

### PlanDayCard
- Card com gradiente suave baseado nos grupos musculares
- Chips para grupos musculares
- Ícone indicando se há sessão completa naquele dia

### LoadingIndicator
- Animação de loading customizada (pulsing dumbbell ou similar)
- Mensagem de texto rotativa ("Analisando seu perfil...", "Selecionando exercícios...", etc)

### ExerciseExclusionDialog
- FullScreenDialog ou BottomSheet
- Lista agrupada por grupo muscular
- Checkboxes para seleção
- Barra de busca no topo
- Contador de exclusões selecionadas
- Botões "Pular" e "Confirmar"

### MuscleGroupSelector
- Row horizontal scrollável com FilterChips
- Cada chip com cor do grupo muscular
- Seleção única (com "Todos" como opção)

### StatCard
- Card com ícone, label, valor e unidade
- Fundo com gradiente suave
- Animação de contagem (count-up) ao aparecer

---

## Notas para o Agente Codificador

1. Usar `NavHost` com `composable()` para cada rota.
2. Cada tela deve ter seu próprio ViewModel anotado com `@HiltViewModel`.
3. Os ViewModels usam `StateFlow<UiState>` para gerenciar estado.
4. Coletar state nos composables com `collectAsStateWithLifecycle()`.
5. Implementar transições suaves conforme especificado na seção de animações.
6. Bottom Navigation deve preservar o estado de cada tab (usar `rememberSaveable`).
7. O `WorkoutScreen` deve manter um timer ativo mesmo com a tela em background.
8. O `ExerciseExclusionDialog` deve ser reutilizado tanto na geração de plano completo quanto na regeneração de dia específico.
9. Todos os textos visíveis devem estar em Português Brasileiro.
10. Usar `Modifier.animateContentSize()` nos cards expandíveis.
