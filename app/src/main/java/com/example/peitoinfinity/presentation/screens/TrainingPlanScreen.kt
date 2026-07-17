package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanScreen(
    onDayClick: (Long) -> Unit
) {
    val trainingDays = listOf(
        TrainingDayItem(1L, "Dia A: Peito e Tríceps", "Peitoral Superior, Peitoral Médio, Tríceps Pulley"),
        TrainingDayItem(2L, "Dia B: Costas e Bíceps", "Puxada Alta, Remada Curvada, Rosca Direta"),
        TrainingDayItem(3L, "Dia C: Pernas e Ombros", "Agachamento Livre, Leg Press, Elevação Lateral")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meu Plano de Treino",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(PeitoDimens.paddingMd),
            verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm)
        ) {
            items(trainingDays) { day ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDayClick(day.id) },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(PeitoDimens.paddingMd)
                    ) {
                        Text(
                            text = day.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = day.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

data class TrainingDayItem(
    val id: Long,
    val title: String,
    val subtitle: String
)
