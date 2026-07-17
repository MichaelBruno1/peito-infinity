# PeitoInfinity — Especificação Técnica

Documentação completa do projeto **PeitoInfinity**: app Android nativo de geração de planos de treino de musculação com inteligência artificial.

## Índice de Documentos

| # | Documento | Descrição |
|---|-----------|-----------|
| 01 | [Visão Geral](./01-overview.md) | Stack tecnológica, arquitetura, estrutura de pacotes e fluxo principal |
| 02 | [Dependências](./02-dependencies.md) | Todas as dependências Gradle e configurações de build |
| 03 | [Modelo de Dados](./03-data-model.md) | Entidades Room, DAOs, relacionamentos e TypeConverters |
| 04 | [Telas e Navegação](./04-screens.md) | Especificação de cada tela, componentes e fluxo de navegação |
| 05 | [Design System](./05-design-system.md) | Tema escuro, paleta pastel, tipografia, shapes e animações |
| 06 | [Integração IA](./06-ai-integration.md) | LiteRT-LM, Gemini API, prompts e parsing de respostas |
| 07 | [Banco de Exercícios](./07-exercise-database.md) | Lista completa de exercícios pré-cadastrados |
| 08 | [Progresso e Relatórios](./08-progress-reports.md) | Registro de treinos e geração de relatórios semanais |
| 09 | [Domain Layer](./09-domain-layer.md) | Use Cases, Models e Repository Interfaces |
| 10 | [Guia de Implementação](./10-implementation-guide.md) | Ordem de implementação, checklist e instruções para o agente codificador |
| 11 | [Progresso](./11-progress.md) | Registro de progresso da implementação |

## Convenções

- **Linguagem do código**: Kotlin
- **Linguagem da UI**: Português Brasileiro
- **Linguagem da documentação**: Português Brasileiro
- **Package base**: `com.example.peitoinfinity`
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 36 (Android 16)
