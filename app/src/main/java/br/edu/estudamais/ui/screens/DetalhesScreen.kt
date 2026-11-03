package br.edu.estudamais.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.estudamais.model.*
import br.edu.estudamais.ui.components.*
import br.edu.estudamais.ui.theme.EstudaMaisTheme
import br.edu.estudamais.ui.viewmodel.DetalhesUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetalhesScreen(ui: DetalhesUiState, hoje: Long, onVoltar: () -> Unit, onEditar: (Long) -> Unit,
    onConcluir: () -> Unit, onExcluir: () -> Unit, onTentar: () -> Unit, modifier: Modifier = Modifier) {
    var confirmarExclusao by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = ui.ocupado) { /* Evita sair durante uma gravação. */ }
    Scaffold(modifier = modifier, topBar = { TopAppBar(title = { Text("Detalhes da atividade") },
        navigationIcon = { IconButton(onClick = onVoltar, enabled = !ui.ocupado) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ui.erro?.let { AvisoErro(it, onTentar) }
            val item = ui.atividade
            when {
                ui.carregando -> Carregando()
                item == null -> if (!ui.excluida && ui.erro == null) EstadoVazio("Atividade não encontrada", "Este registro não está mais disponível.", acao = "Voltar", onAcao = onVoltar)
                else -> {
                    Text(item.disciplina.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(item.titulo, style = MaterialTheme.typography.headlineLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrioridadeEtiqueta(item.prioridade)
                        Etiqueta(item.situacao(hoje))
                    }
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("PRAZO DE ENTREGA", style = MaterialTheme.typography.labelLarge)
                            Text(formatarPrazo(item.prazoDia), style = MaterialTheme.typography.titleLarge)
                            if (item.estaAtrasada(hoje)) Text("O prazo passou. Você ainda pode concluir ou reagendar.", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    Text("Sobre a atividade", style = MaterialTheme.typography.titleLarge)
                    Text(item.descricao.ifBlank { "Nenhuma descrição adicionada." }, style = MaterialTheme.typography.bodyLarge)
                    HorizontalDivider()
                    Button(onClick = onConcluir, enabled = !ui.ocupado, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Check, null); Spacer(Modifier.width(8.dp))
                        Text(if (item.concluida) "Marcar como pendente" else "Marcar como concluída")
                    }
                    OutlinedButton(onClick = { onEditar(item.id) }, enabled = !ui.ocupado, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Edit, null); Spacer(Modifier.width(8.dp)); Text("Editar atividade")
                    }
                    TextButton(onClick = { confirmarExclusao = true }, enabled = !ui.ocupado, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Icon(Icons.Default.Delete, null); Spacer(Modifier.width(8.dp)); Text("Excluir atividade")
                    }
                    if (ui.ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text("Registro #${item.id} • salvo neste aparelho", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
    if (confirmarExclusao) AlertDialog(onDismissRequest = { confirmarExclusao = false },
        title = { Text("Excluir atividade?") }, text = { Text("Essa ação remove o registro do aparelho e não pode ser desfeita.") },
        confirmButton = { TextButton(onClick = { confirmarExclusao = false; onExcluir() }) { Text("Excluir") } },
        dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") } })
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetalhesPreview() = EstudaMaisTheme { DetalhesScreen(DetalhesUiState(atividadesPreview.first(), carregando = false), hojePreview, {}, {}, {}, {}, {}) }
