package com.example.peitoinfinity.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.peitoinfinity.ui.theme.PeitoDimens
import kotlinx.coroutines.delay

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    messages: List<String> = listOf(
        "Analisando seu perfil...",
        "Calculando volume ideal...",
        "Selecionando exercícios...",
        "Montando sua divisão de treino...",
        "Quase pronto..."
    )
) {
    var currentMessageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            while (true) {
                delay(3000L) // Rotaciona a cada 3 segundos
                currentMessageIndex = (currentMessageIndex + 1) % messages.size
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loading_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = "Carregando...",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(PeitoDimens.iconXl)
                .scale(scale)
        )
        Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))
        if (messages.isNotEmpty()) {
            Text(
                text = messages[currentMessageIndex],
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PeitoDimens.paddingLg)
            )
        }
    }
}
