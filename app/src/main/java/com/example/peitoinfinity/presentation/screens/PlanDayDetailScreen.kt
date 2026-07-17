package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDayDetailScreen(
    dayId: Long,
    onBackClick: () -> Unit,
    onStartWorkout: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes do Treino (Dia $dayId)",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(PeitoDimens.paddingMd),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
            ) {
                Text(
                    text = "Exercícios do Dia",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Aqui serão listados os exercícios gerados pela IA para este dia de treino.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onStartWorkout(dayId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PeitoDimens.buttonHeight),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = "Iniciar Treino",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
