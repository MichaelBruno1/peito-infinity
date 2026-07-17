package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.peitoinfinity.ui.theme.PeitoDimens

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PeitoDimens.paddingMd),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
        ) {
            Text(
                text = "PeitoInfinity",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Gere treinos personalizados com inteligência artificial para o seu perfil e objetivos.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PeitoDimens.paddingMd)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onFinishOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PeitoDimens.buttonHeight),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Começar",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
