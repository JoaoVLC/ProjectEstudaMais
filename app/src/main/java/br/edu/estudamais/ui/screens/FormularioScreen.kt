package br.edu.estudamais.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.estudamais.model.*
import br.edu.estudamais.ui.components.*
import br.edu.estudamais.ui.theme.EstudaMaisTheme
import br.edu.estudamais.ui.viewmodel.FormularioUiState
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormularioScreen(ui: FormularioUiState, onTitulo: (String) -> Unit, onDisciplina: (String) -> Unit,
    onDescricao: (String) -> Unit, onPrazo: (Long) -> Unit, onPrioridade: (Prioridade) -> Unit,
    onSalvar: () -> Unit, onVoltar: () -> Unit, onTentar: () -> Unit, modifier: Modifier = Modifier) {
    var calendario by rememberSaveable { mutableStateOf(false) }
    var descartar by rememberSaveable { mutableStateOf(false) }
    val pedirVolta: () -> Unit = { if (!ui.salvando) { if (ui.alterado) descartar = true else onVoltar() } }
    BackHandler(enabled = ui.alterado || ui.salvando) { pedirVolta() }
    Scaffold(modifier = modifier,
        topBar = { TopAppBar(title = { Text(if (ui.id == 0L) "Nova atividade" else "Editar atividade") },
            navigationIcon = { IconButton(onClick = pedirVolta, enabled = !ui.salvando) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } }) },
        bottomBar = {
            if (!ui.carregando && !ui.naoEncontrada) Surface(shadowElevation = 4.dp) {
                Button(onClick = onSalvar, enabled = !ui.salvando && !ui.salvo,
                    modifier = Modifier.fillMaxWidth().imePadding().navigationBarsPadding().padding(20.dp)) {
                    if (ui.salvando) { CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)) }
                    Text(if (ui.salvando) "Salvando…" else "Salvar atividade")
                }
            }
        }
    ) { padding ->
        when {
            ui.carregando -> Carregando(Modifier.padding(padding))
            ui.naoEncontrada -> EstadoVazio("Atividade não encontrada", "Ela pode ter sido excluída.", modifier = Modifier.padding(padding), acao = "Voltar", onAcao = onVoltar)
            else -> Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("Transforme seus planos em próximos passos.", style = MaterialTheme.typography.titleLarge)
                Text("Título, disciplina, prazo e prioridade são obrigatórios. A descrição é opcional.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                ui.erro?.let { AvisoErro(it, if (ui.id > 0) onTentar else null) }
                OutlinedTextField(value = ui.titulo, onValueChange = onTitulo, label = { Text("Título *") },
                    placeholder = { Text("Ex.: revisar o conteúdo da prova") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    enabled = !ui.salvando, isError = ui.erros.titulo != null,
                    supportingText = { Text(ui.erros.titulo ?: "${ui.titulo.length}/100 caracteres") })
                OutlinedTextField(value = ui.disciplina, onValueChange = onDisciplina, label = { Text("Disciplina *") },
                    placeholder = { Text("Ex.: Mobile Development") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    enabled = !ui.salvando, isError = ui.erros.disciplina != null,
                    supportingText = { Text(ui.erros.disciplina ?: "${ui.disciplina.length}/60 caracteres") })
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Prazo de entrega *", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = { calendario = true }, enabled = !ui.salvando, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.CalendarMonth, null); Spacer(Modifier.width(8.dp))
                        Text(ui.prazo?.let(::formatarPrazo) ?: "Escolher data")
                    }
                    ui.erros.prazo?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Text("Prazos anteriores a hoje serão indicados como atrasados.", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column {
                    Text("Prioridade *", style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Prioridade.entries.forEach { prioridade ->
                            FilterChip(selected = ui.prioridade == prioridade, onClick = { onPrioridade(prioridade) },
                                enabled = !ui.salvando, label = { Text(prioridade.rotulo) })
                        }
                    }
                }
                OutlinedTextField(value = ui.descricao, onValueChange = onDescricao, label = { Text("Descrição (opcional)") },
                    placeholder = { Text("Anotações, orientações e o que precisa ser feito…") }, modifier = Modifier.fillMaxWidth(),
                    minLines = 4, enabled = !ui.salvando, isError = ui.erros.descricao != null,
                    supportingText = { Text(ui.erros.descricao ?: "${ui.descricao.length}/2.000 caracteres") })
            }
        }
    }
    if (calendario) {
        val picker = rememberDatePickerState(initialSelectedDateMillis = ui.prazo?.times(86_400_000L))
        DatePickerDialog(onDismissRequest = { calendario = false },
            confirmButton = { TextButton(enabled = picker.selectedDateMillis != null, onClick = {
                picker.selectedDateMillis?.let { onPrazo(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()) }
                calendario = false
            }) { Text("Confirmar") } },
            dismissButton = { TextButton(onClick = { calendario = false }) { Text("Cancelar") } }) { DatePicker(state = picker) }
    }
    if (descartar) AlertDialog(onDismissRequest = { descartar = false }, title = { Text("Descartar alterações?") },
        text = { Text("As alterações deste formulário ainda não foram salvas.") },
        confirmButton = { TextButton(onClick = { descartar = false; onVoltar() }) { Text("Descartar") } },
        dismissButton = { TextButton(onClick = { descartar = false }) { Text("Continuar editando") } })
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormularioPreview() = EstudaMaisTheme { FormularioScreen(FormularioUiState(), {}, {}, {}, {}, {}, {}, {}, {}) }
