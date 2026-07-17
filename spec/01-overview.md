# 01 — Visão Geral do Projeto

## Nome do App
**PeitoInfinity**

## Descrição
Aplicativo Android nativo para geração de planos de treino de musculação personalizados utilizando inteligência artificial. O app utiliza LLM (Large Language Model) rodando localmente no dispositivo via Google AI Edge (LiteRT-LM) ou remotamente via API Gemini para gerar treinos adaptados ao perfil e objetivos do usuário.

## Stack Tecnológica

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Kotlin | 2.2.10 | Linguagem principal |
| Jetpack Compose | BOM 2026.02.01 | UI declarativa |
| Material 3 | via Compose BOM | Design system |
| Room 3 | 3.0.0 | Persistência local (package `androidx.room3`) |
| KSP | 2.2.10-1.0.x | Processamento de anotações para Room |
| Navigation Compose | 2.9.8 | Navegação entre telas |
| LiteRT-LM | latest.release | Inferência LLM on-device |
| Google GenAI Kotlin SDK | 0.1.0 | API Gemini remota |
| Hilt / Dagger | latest | Injeção de dependências |
| Kotlin Coroutines + Flow | latest | Programação assíncrona |
| DataStore Preferences | latest | Configurações do app |
| kotlinx.serialization | latest | Serialização JSON |
| AGP | 9.3.0 | Build system |
| Min SDK | 31 | Android 12+ |
| Target SDK | 36 | Android 16 |
| Compile SDK | 36.1 | Latest |

## Funcionalidades Principais

### 1. Cadastro de Perfil do Usuário
- Sexo (Masculino, Feminino)
- Altura (cm)
- Peso (kg)
- Dias disponíveis para treino (1-7)
- Tempo disponível por sessão (minutos)
- Nível de treinamento: Iniciante, Intermediário, Avançado

### 2. Objetivo de Treino
- Perda de peso
- Ganho de massa muscular
- Ganho de força
- Manutenção do peso atual

### 3. Geração de Plano de Treino via IA
- Plano completo personalizado baseado no perfil
- Exercícios com quantidade de repetições, séries, intervalo de descanso
- Tempo estimado de duração do treino (ex: 40min)
- Possibilidade de gerar novos planos ilimitadamente
- Regenerar treino específico de um grupo muscular
- Excluir exercícios específicos antes da geração

### 4. Registro de Progresso
- Registrar carga utilizada em cada exercício de musculação (kg)
- Para exercícios aeróbicos: velocidade (km/h), tempo (min) e distância (m/km)
- Histórico completo de treinos realizados

### 5. Relatórios Semanais
- Tempo total de treino na semana
- Peso total levantado (kg)
- Distância total percorrida (km)
- Velocidade média (km/h)
- Tempo médio de descanso entre séries

### 6. Catálogo de Exercícios
- Lista completa de exercícios pré-cadastrados
- Organizado por grupo muscular
- Busca e filtros por nome, grupo muscular e equipamento

### 7. Configurações de IA
- Alternar entre modelo local (LiteRT-LM) e API externa (Gemini)
- A chave API do Gemini é embutida internamente no código (BuildConfig)
- O usuário vê apenas a opção: "Modelo Local" ou "Modelo Externo"
- Não deve haver campo de input para chave API — é tudo interno

## Design
- Tema escuro (Dark Theme) obrigatório — sem opção de tema claro
- Paleta de cores pastéis suaves
- Transições suaves entre telas (animações de entrada/saída com fadeIn/fadeOut + slideIn/slideOut)
- Material 3 com Dynamic Color desabilitado (paleta fixa pastel)
- Cantos arredondados (rounded corners) em cards e botões
- Ícones Material Symbols

## Arquitetura
- **Clean Architecture** com camadas: Presentation → Domain → Data
- **MVVM** (Model-View-ViewModel) na camada de apresentação
- **Repository Pattern** para acesso a dados
- **Use Cases** para lógica de negócios
- **Injeção de dependências** com Hilt
- **Single Activity** com Navigation Compose
- **Unidirectional Data Flow** (UDF) nos ViewModels

## Estrutura de Pacotes

```
com.example.peitoinfinity/
├── PeitoInfinityApplication.kt   # Application class @HiltAndroidApp
├── di/                            # Módulos Hilt (DI)
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── AiModule.kt
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── PeitoInfinityDatabase.kt
│   │   │   ├── Converters.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserProfileDao.kt
│   │   │   │   ├── ExerciseDao.kt
│   │   │   │   ├── TrainingPlanDao.kt
│   │   │   │   ├── PlanDayDao.kt
│   │   │   │   ├── PlanExerciseDao.kt
│   │   │   │   ├── WorkoutSessionDao.kt
│   │   │   │   ├── ExerciseLogDao.kt
│   │   │   │   └── ExerciseExclusionDao.kt
│   │   │   └── entity/
│   │   │       ├── UserProfileEntity.kt
│   │   │       ├── ExerciseEntity.kt
│   │   │       ├── TrainingPlanEntity.kt
│   │   │       ├── PlanDayEntity.kt
│   │   │       ├── PlanExerciseEntity.kt
│   │   │       ├── WorkoutSessionEntity.kt
│   │   │       ├── ExerciseLogEntity.kt
│   │   │       └── ExerciseExclusionEntity.kt
│   │   └── preferences/
│   │       └── AppPreferences.kt
│   ├── repository/
│   │   ├── UserProfileRepositoryImpl.kt
│   │   ├── ExerciseRepositoryImpl.kt
│   │   ├── TrainingPlanRepositoryImpl.kt
│   │   ├── WorkoutSessionRepositoryImpl.kt
│   │   └── ReportRepositoryImpl.kt
│   └── ai/
│       ├── AiProvider.kt           # Interface
│       ├── LocalAiProvider.kt      # LiteRT-LM
│       ├── RemoteAiProvider.kt     # Gemini API
│       └── PromptBuilder.kt        # Construtor de prompts
├── domain/
│   ├── model/
│   │   ├── UserProfile.kt
│   │   ├── Exercise.kt
│   │   ├── TrainingPlan.kt
│   │   ├── PlanDay.kt
│   │   ├── PlanExercise.kt
│   │   ├── WorkoutSession.kt
│   │   ├── ExerciseLog.kt
│   │   ├── WeeklyReport.kt
│   │   ├── TrainingGoal.kt         # enum
│   │   ├── TrainingLevel.kt        # enum
│   │   ├── MuscleGroup.kt          # enum
│   │   ├── Equipment.kt            # enum
│   │   ├── ExerciseType.kt         # enum
│   │   ├── Difficulty.kt           # enum
│   │   ├── Gender.kt                # enum (MALE, FEMALE)
│   │   └── AiMode.kt               # enum (LOCAL, REMOTE)
│   ├── repository/
│   │   ├── UserProfileRepository.kt
│   │   ├── ExerciseRepository.kt
│   │   ├── TrainingPlanRepository.kt
│   │   ├── WorkoutSessionRepository.kt
│   │   └── ReportRepository.kt
│   └── usecase/
│       ├── SaveUserProfileUseCase.kt
│       ├── GetUserProfileUseCase.kt
│       ├── GenerateTrainingPlanUseCase.kt
│       ├── RegenerateWorkoutUseCase.kt
│       ├── GetExercisesUseCase.kt
│       ├── LogExerciseUseCase.kt
│       ├── StartWorkoutSessionUseCase.kt
│       ├── FinishWorkoutSessionUseCase.kt
│       ├── GetWeeklyReportUseCase.kt
│       ├── GetTrainingPlanUseCase.kt
│       └── GetExerciseExclusionsUseCase.kt
├── presentation/
│   ├── MainActivity.kt
│   ├── PeitoInfinityApp.kt         # NavHost + Theme wrapper
│   ├── navigation/
│   │   ├── Screen.kt               # Sealed class com rotas
│   │   └── NavGraph.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   ├── Type.kt
│   │   └── Shape.kt
│   ├── components/
│   │   ├── PeitoTopBar.kt
│   │   ├── PeitoBottomBar.kt
│   │   ├── ExerciseCard.kt
│   │   ├── PlanDayCard.kt
│   │   ├── LoadingIndicator.kt
│   │   ├── ExerciseExclusionDialog.kt
│   │   ├── MuscleGroupSelector.kt
│   │   └── StatCard.kt
│   └── screens/
│       ├── onboarding/
│       │   ├── OnboardingScreen.kt
│       │   └── OnboardingViewModel.kt
│       ├── profile/
│       │   ├── ProfileScreen.kt
│       │   └── ProfileViewModel.kt
│       ├── plan/
│       │   ├── TrainingPlanScreen.kt
│       │   ├── TrainingPlanViewModel.kt
│       │   ├── PlanDayDetailScreen.kt
│       │   └── PlanDayDetailViewModel.kt
│       ├── workout/
│       │   ├── WorkoutScreen.kt
│       │   └── WorkoutViewModel.kt
│       ├── exercises/
│       │   ├── ExerciseListScreen.kt
│       │   └── ExerciseListViewModel.kt
│       ├── report/
│       │   ├── ReportScreen.kt
│       │   └── ReportViewModel.kt
│       └── settings/
│           ├── SettingsScreen.kt
│           └── SettingsViewModel.kt
└── util/
    ├── Extensions.kt
    ├── Constants.kt
    └── JsonParser.kt
```

## Fluxo Principal do Usuário

1. **Primeiro acesso** → Tela de Onboarding → Cadastro de perfil (sexo, altura, peso, dias, tempo, nível) e objetivo
2. **Home / Plano** → Visualizar plano de treino ativo ou gerar novo plano
3. **Geração de plano** → Exibir lista de exercícios para excluir (opcional) → LLM gera o plano → Exibir resultado
4. **Treino** → Selecionar dia de treino → Iniciar sessão → Registrar progresso (carga para musculação / velocidade+tempo+distância para cardio) → Finalizar sessão
5. **Relatórios** → Consultar resumo semanal (tempo, peso, distância, velocidade média)
6. **Exercícios** → Consultar catálogo completo com busca e filtros
7. **Configurações** → Alternar modo IA (Local / Externo) e editar perfil

## Notas para o Agente Codificador

- Este documento é a referência central do projeto.
- Consulte os demais documentos na pasta `spec/` para detalhes de cada módulo.
- A ordem de implementação sugerida está em `spec/10-implementation-guide.md`.
- O progresso deve ser registrado em `spec/11-progress.md`.
- Todo texto visível ao usuário deve estar em Português Brasileiro.
- Usar `kotlinx.serialization` (não Gson/Moshi) para serialização JSON.
- O app NÃO possui login/autenticação — é totalmente offline (exceto API Gemini quando selecionada).
