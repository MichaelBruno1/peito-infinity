# 05 — Design System

Especificação do tema visual do PeitoInfinity: tema escuro com tons pastéis.

## Paleta de Cores

### Cores Pastéis Base (Dark Theme)

O app usa exclusivamente tema escuro. As cores pastéis são usadas como acentos sobre fundos escuros.

```kotlin
// Color.kt

// ========================================
// Fundos (Dark)
// ========================================
val DarkBackground = Color(0xFF0F1119)         // Fundo principal
val DarkSurface = Color(0xFF1A1D2B)            // Cards, superfícies
val DarkSurfaceVariant = Color(0xFF232738)      // Superfícies elevadas
val DarkSurfaceContainer = Color(0xFF1E2130)    // Container

// ========================================
// Cores Pastéis (Accent/Brand)
// ========================================
val PastelBlue = Color(0xFF8BB8E8)              // Primary
val PastelBlueDark = Color(0xFF5A8FC4)          // Primary variant
val PastelBlueContainer = Color(0xFF1E3A5F)     // Primary container

val PastelPurple = Color(0xFFB39DDB)            // Secondary
val PastelPurpleDark = Color(0xFF9575CD)        // Secondary variant
val PastelPurpleContainer = Color(0xFF352A4D)   // Secondary container

val PastelMint = Color(0xFF80CBC4)              // Tertiary
val PastelMintDark = Color(0xFF4DB6AC)          // Tertiary variant
val PastelMintContainer = Color(0xFF1A3A37)     // Tertiary container

// ========================================
// Cores de Estado
// ========================================
val PastelGreen = Color(0xFFA5D6A7)            // Sucesso
val PastelGreenContainer = Color(0xFF1B3A1E)
val PastelRed = Color(0xFFEF9A9A)              // Erro
val PastelRedContainer = Color(0xFF3A1B1B)
val PastelYellow = Color(0xFFFFF59D)           // Aviso
val PastelYellowContainer = Color(0xFF3A3A1B)
val PastelOrange = Color(0xFFFFCC80)           // Info/Warning

// ========================================
// Texto
// ========================================
val TextPrimary = Color(0xFFE8EAF0)            // Texto principal
val TextSecondary = Color(0xFFA0A4B8)          // Texto secundário
val TextTertiary = Color(0xFF6B7080)           // Texto terciário/disabled
val TextOnPrimary = Color(0xFF0F1119)          // Texto sobre primary

// ========================================
// Cores por Grupo Muscular (para chips e badges)
// ========================================
val MuscleChest = Color(0xFFEF9A9A)            // Peito — Rosa pastel
val MuscleBack = Color(0xFF90CAF9)             // Costas — Azul pastel
val MuscleShoulders = Color(0xFFA5D6A7)        // Ombros — Verde pastel
val MuscleBiceps = Color(0xFFCE93D8)           // Bíceps — Roxo pastel
val MuscleTriceps = Color(0xFFFFCC80)          // Tríceps — Laranja pastel
val MuscleQuadriceps = Color(0xFF80DEEA)       // Quadríceps — Ciano pastel
val MuscleHamstrings = Color(0xFFBCAAA4)       // Posterior — Marrom pastel
val MuscleGlutes = Color(0xFFF48FB1)           // Glúteos — Pink pastel
val MuscleCalves = Color(0xFFB0BEC5)           // Panturrilha — Cinza pastel
val MuscleAbs = Color(0xFFFFF59D)             // Abdômen — Amarelo pastel
val MuscleForearms = Color(0xFFE6EE9C)         // Antebraço — Lima pastel
val MuscleCardio = Color(0xFF80CBC4)           // Cardio — Teal pastel
val MuscleTraps = Color(0xFFB39DDB)            // Trapézio — Lavanda pastel
```

### Material 3 Color Scheme

```kotlin
// Theme.kt

private val PeitoInfinityDarkColorScheme = darkColorScheme(
    primary = PastelBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PastelBlueContainer,
    onPrimaryContainer = PastelBlue,

    secondary = PastelPurple,
    onSecondary = TextOnPrimary,
    secondaryContainer = PastelPurpleContainer,
    onSecondaryContainer = PastelPurple,

    tertiary = PastelMint,
    onTertiary = TextOnPrimary,
    tertiaryContainer = PastelMintContainer,
    onTertiaryContainer = PastelMint,

    error = PastelRed,
    onError = TextOnPrimary,
    errorContainer = PastelRedContainer,
    onErrorContainer = PastelRed,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = Color(0xFF3A3F52),
    outlineVariant = Color(0xFF2A2E3E),
    inverseSurface = Color(0xFFE8EAF0),
    inverseOnSurface = DarkBackground,
    inversePrimary = PastelBlueDark,
    surfaceTint = PastelBlue
)

@Composable
fun PeitoInfinityTheme(
    content: @Composable () -> Unit
) {
    // Sempre dark — sem opção de tema claro
    val colorScheme = PeitoInfinityDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PeitoTypography,
        shapes = PeitoShapes,
        content = content
    )
}
```

## Tipografia

```kotlin
// Type.kt
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val interFamily = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Bold),
)

val PeitoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
```

## Shapes

```kotlin
// Shape.kt

val PeitoShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)
```

## Dimensões e Espaçamentos

```kotlin
// Em Constants.kt ou Dimens object

object PeitoDimens {
    // Padding
    val paddingXs = 4.dp
    val paddingSm = 8.dp
    val paddingMd = 16.dp
    val paddingLg = 24.dp
    val paddingXl = 32.dp

    // Card
    val cardElevation = 2.dp
    val cardBorderWidth = 1.dp

    // Bottom Bar
    val bottomBarHeight = 80.dp

    // Button
    val buttonHeight = 56.dp
    val buttonCornerRadius = 16.dp

    // Icon
    val iconSm = 20.dp
    val iconMd = 24.dp
    val iconLg = 32.dp
    val iconXl = 48.dp
}
```

## Animações e Transições

### Transições entre Telas
Conforme definido em `spec/04-screens.md`:
- Navegação horizontal: slide + fade (300ms)
- Bottom Navigation: crossfade (200ms)

### Micro-animações

```kotlin
// Animação de aparecimento de cards (staggered)
@Composable
fun AnimatedCard(
    index: Int,
    content: @Composable () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L) // Stagger de 50ms por card
        visible.value = true
    }
    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(300)
        )
    ) {
        content()
    }
}

// Animação de count-up para métricas
@Composable
fun AnimatedCounter(
    targetValue: Float,
    durationMillis: Int = 1000,
    decimalPlaces: Int = 1
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(targetValue) {
        animatable.animateTo(
            targetValue = targetValue,
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    }
    // Usar animatable.value para exibir
}

// Animação de loading (pulsing)
@Composable
fun PulsingLoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    // Icon com Modifier.scale(scale)
}
```

### Animações de Expand/Collapse

```kotlin
// Para cards expandíveis
Modifier.animateContentSize(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)
```

## Gradientes

```kotlin
// Gradientes para cards de dias de treino
val chestGradient = Brush.linearGradient(
    colors = listOf(
        MuscleChest.copy(alpha = 0.15f),
        DarkSurface
    )
)

// Gradiente genérico para cards de estatísticas
fun statCardGradient(color: Color) = Brush.linearGradient(
    colors = listOf(
        color.copy(alpha = 0.12f),
        DarkSurface
    ),
    start = Offset.Zero,
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
```

## Estilo dos Cards

```kotlin
// Card padrão
@Composable
fun PeitoCard(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = PeitoShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = accentColor?.let {
            BorderStroke(1.dp, it.copy(alpha = 0.3f))
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        content = content
    )
}
```

## Ícones por Grupo Muscular

Usar Material Symbols (via `material-icons-extended`). Mapeamento sugerido:

| Grupo | Ícone Material | Cor |
|-------|---------------|-----|
| Peito | FitnessCenter | MuscleChest |
| Costas | Accessibility | MuscleBack |
| Ombros | SportsGymnastics | MuscleShoulders |
| Bíceps | FitnessCenter | MuscleBiceps |
| Tríceps | FitnessCenter | MuscleTriceps |
| Quadríceps | DirectionsRun | MuscleQuadriceps |
| Posterior | DirectionsRun | MuscleHamstrings |
| Glúteos | SelfImprovement | MuscleGlutes |
| Panturrilha | DirectionsWalk | MuscleCalves |
| Abdômen | SelfImprovement | MuscleAbs |
| Antebraço | FitnessCenter | MuscleForearms |
| Cardio | FavoriteBorder | MuscleCardio |
| Trapézio | Accessibility | MuscleTraps |

## Notas para o Agente Codificador

1. **Somente tema escuro** — não implementar tema claro.
2. **Dynamic Color** deve ser **desabilitado** — usar a paleta pastel fixa.
3. A fonte **Inter** deve ser carregada via Google Fonts Provider. Se não for possível, usar como fallback a família `FontFamily.SansSerif`.
4. Todas as cores de grupo muscular devem ser usadas consistentemente em toda a app (chips, badges, gradientes de cards, etc).
5. As animações staggered devem ser usadas em listas de cards (exercícios, dias de treino).
6. O `LoadingIndicator` durante geração de plano deve ter mensagens rotativas.
7. Testar contraste de cores para garantir acessibilidade (WCAG AA minimum).
8. Os gradientes nos cards devem ser sutis (alpha baixo) para não competir com o conteúdo.
