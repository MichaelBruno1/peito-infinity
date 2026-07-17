package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.peitoinfinity.presentation.components.ExerciseCard
import com.example.peitoinfinity.presentation.components.MuscleGroupSelector
import com.example.peitoinfinity.presentation.components.PeitoTopBar
import com.example.peitoinfinity.presentation.exercises.ExerciseListViewModel
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    viewModel: ExerciseListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PeitoTopBar(title = "Exercícios")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PeitoDimens.paddingMd),
                placeholder = { Text("Pesquisar exercício...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            // Muscle Group Selector
            MuscleGroupSelector(
                selectedGroup = uiState.selectedMuscleGroup,
                onGroupSelected = viewModel::updateSelectedMuscleGroup,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredExercises.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nenhum exercício encontrado.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = PeitoDimens.paddingMd,
                        vertical = PeitoDimens.paddingSm
                    ),
                    verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm)
                ) {
                    items(uiState.filteredExercises, key = { it.id }) { exercise ->
                        val isExcluded = uiState.excludedExerciseIds.contains(exercise.id)
                        ExerciseCard(
                            exercise = exercise,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (isExcluded) 0.5f else 1.0f),
                            trailingContent = {
                                IconButton(
                                    onClick = { viewModel.toggleExclusion(exercise.id) }
                                ) {
                                    Icon(
                                        imageVector = if (isExcluded) Icons.Default.Block else Icons.Default.Add,
                                        contentDescription = if (isExcluded) "Restaurar" else "Excluir",
                                        tint = if (isExcluded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
