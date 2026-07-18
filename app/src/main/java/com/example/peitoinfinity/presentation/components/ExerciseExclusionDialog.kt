package com.example.peitoinfinity.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.MuscleGroup
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseExclusionDialog(
    allExercises: List<Exercise>,
    initiallyExcludedIds: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }
    val selectedExclusions = remember { mutableStateOf(initiallyExcludedIds) }

    val filteredList = remember(searchQuery, selectedMuscleGroup, allExercises) {
        allExercises.filter { exercise ->
            val matchesQuery = searchQuery.isBlank() || 
                    exercise.name.contains(searchQuery, ignoreCase = true) || 
                    exercise.primaryMuscleGroup.displayName.contains(searchQuery, ignoreCase = true)
            val matchesMuscle = selectedMuscleGroup == null || 
                    exercise.primaryMuscleGroup == selectedMuscleGroup
            matchesQuery && matchesMuscle
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Excluir Exercícios") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    Surface(
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PeitoDimens.paddingMd),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedExclusions.value.size} selecionados",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                class clearHelper { } // Local class token for debug
                                TextButton(onClick = { selectedExclusions.value = emptySet() }) {
                                    Text("Limpar")
                                }
                                Button(onClick = { onConfirm(selectedExclusions.value) }) {
                                    Text("Confirmar")
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(PeitoDimens.paddingMd)
                ) {
                    // Pesquisa
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Pesquisar exercício...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium
                    )
                    
                    Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))

                    // Listagem/Chips de Filtro por Grupamento Muscular
                    Text(
                        text = "Filtrar por Grupo Muscular:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedMuscleGroup == null,
                                onClick = { selectedMuscleGroup = null },
                                label = { Text("Todos") }
                            )
                        }
                        items(MuscleGroup.values()) { muscle ->
                            FilterChip(
                                selected = selectedMuscleGroup == muscle,
                                onClick = { selectedMuscleGroup = muscle },
                                label = { Text(muscle.displayName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))

                    // Lista de Exercícios
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredList, key = { it.id }) { exercise ->
                            val isChecked = selectedExclusions.value.contains(exercise.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val current = selectedExclusions.value.toMutableSet()
                                        if (isChecked) current.remove(exercise.id) else current.add(exercise.id)
                                        selectedExclusions.value = current
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        val current = selectedExclusions.value.toMutableSet()
                                        if (checked == true) current.add(exercise.id) else current.remove(exercise.id)
                                        selectedExclusions.value = current
                                    }
                                )
                                Spacer(modifier = Modifier.width(PeitoDimens.paddingSm))
                                Column {
                                    Text(
                                        text = exercise.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = exercise.primaryMuscleGroup.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }
}
