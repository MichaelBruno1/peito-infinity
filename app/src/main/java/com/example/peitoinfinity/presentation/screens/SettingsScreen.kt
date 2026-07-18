package com.example.peitoinfinity.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.peitoinfinity.domain.model.AiMode
import com.example.peitoinfinity.presentation.components.PeitoTopBar
import com.example.peitoinfinity.presentation.settings.SettingsViewModel
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onEditProfileClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showNameDialog by remember { mutableStateOf(false) }
    var modelNameInput by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            showNameDialog = true
        }
    }

    Scaffold(
        topBar = {
            PeitoTopBar(title = "Ajustes")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(PeitoDimens.paddingMd)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
        ) {
            // Seção Perfil
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditProfileClick() },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(PeitoDimens.paddingMd)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Editar Perfil",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ajuste seus dados biométricos e objetivos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    uiState.userProfile?.let { profile ->
                        Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                        
                        Text(
                            text = "Peso: ${profile.weightKg.toInt()} kg  •  Altura: ${profile.heightCm.toInt()} cm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Objetivo: ${profile.trainingGoal.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Seção Inteligência Artificial
            Text(
                text = "Inteligência Artificial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(PeitoDimens.paddingMd)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateAiMode(AiMode.LOCAL) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = uiState.aiMode == AiMode.LOCAL,
                            onClick = { viewModel.updateAiMode(AiMode.LOCAL) }
                        )
                        Spacer(modifier = Modifier.width(PeitoDimens.paddingSm))
                        Column {
                            Text(
                                text = "Modelo Local",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Roda no dispositivo (sem internet)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (uiState.isLocalModelAvailable) {
                                Text(
                                    text = "🟢 Modelo de IA local pronto para uso",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (uiState.isLocalModelDownloading) {
                                Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                                    LinearProgressIndicator(
                                        progress = { uiState.downloadProgress },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Baixando modelo: ${(uiState.downloadProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                Column(modifier = Modifier.padding(top = 4.dp)) {
                                    Text(
                                        text = "🔴 Arquivo do modelo não encontrado",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = { viewModel.downloadModel() },
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Baixar Modelo",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { fileLauncher.launch("*/*") },
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Escolher Arquivo",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    uiState.downloadError?.let { err ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Erro download: $err",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            if (uiState.isImporting) {
                                Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    LinearProgressIndicator(
                                        progress = { uiState.importProgress },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Importando modelo: ${(uiState.importProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            uiState.importError?.let { err ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Erro importação: $err",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateAiMode(AiMode.REMOTE) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = uiState.aiMode == AiMode.REMOTE,
                            onClick = { viewModel.updateAiMode(AiMode.REMOTE) }
                        )
                        Spacer(modifier = Modifier.width(PeitoDimens.paddingSm))
                        Column {
                            Text(
                                text = "Modelo Externo",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Usa servidor remoto (requer internet)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Seção Sobre
            Text(
                text = "Sobre",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(PeitoDimens.paddingMd)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Versão do app", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Nome do Modelo LLM", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Por favor, informe o nome exato do arquivo do modelo para salvá-lo corretamente no projeto.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelNameInput,
                        onValueChange = { modelNameInput = it },
                        placeholder = { Text("ex: gemma-4-1b.litertlm") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (modelNameInput.isNotBlank() && selectedUri != null) {
                            viewModel.importModelFile(context, selectedUri!!, modelNameInput.trim())
                            showNameDialog = false
                        }
                    },
                    enabled = modelNameInput.isNotBlank()
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
