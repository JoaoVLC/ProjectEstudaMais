package br.edu.estudamais.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.estudamais.model.Filtro
import br.edu.estudamais.ui.components.*
import br.edu.estudamais.ui.theme.*
import br.edu.estudamais.ui.viewmodel.AtividadesUiState

@Composable
fun InicioScreen(ui: AtividadesUiState, onNova: () -> Unit, onLista: (Filtro) -> Unit,
    onDetalhe: (Long) -> Unit, onTentar: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.AutoMirrored.Outlined.MenuBook, null, Modifier.size(30.dp), tint = MaterialTheme.colorScheme.primary)
                Text("Estuda+", style = MaterialTheme.typography.headlineMedium)
            }
            Text("Menos correria. Mais organização.", color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp))
        }
        when {
            ui.carregando -> item { Carregando() }
            ui.erro != null -> item { AvisoErro(ui.erro, onTentar) }
            else -> {
                item {
                    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = AzulProfundo, contentColor = Color.White)) {
                        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("SEU PLANO DE ESTUDOS", style = MaterialTheme.typography.labelLarge, color = VerdeSuave)
                            Text(if (ui.resumo.total == 0) "Um passo de cada vez." else "Você está avançando!", style = MaterialTheme.typography.headlineMedium)
                            Text(if (ui.resumo.total == 0) "Cadastre sua primeira atividade e acompanhe os próximos prazos."
                                else "${ui.resumo.concluidas} de ${ui.resumo.total} atividades concluídas.")
                            LinearProgressIndicator(progress = { ui.resumo.progresso }, modifier = Modifier.fillMaxWidth(),
                                color = VerdeSuave, trackColor = Color.White.copy(alpha = .16f))
                            Button(onClick = onNova, colors = ButtonDefaults.buttonColors(containerColor = VerdeSuave, contentColor = AzulProfundo)) {
                                Text("Nova atividade")
                            }
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResumoCard("Pendentes", ui.resumo.pendentes, Modifier.weight(1f)) { onLista(Filtro.PENDENTES) }
                        ResumoCard("Atrasadas", ui.resumo.atrasadas, Modifier.weight(1f)) { onLista(Filtro.ATRASADAS) }
                        ResumoCard("Concluídas", ui.resumo.concluidas, Modifier.weight(1f)) { onLista(Filtro.CONCLUIDAS) }
                    }
                    Text("Pendentes inclui as atrasadas.", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Próximas entregas", style = MaterialTheme.typography.titleLarge)
                        TextButton(onClick = { onLista(Filtro.TODAS) }) { Text("Ver todas") }
                    }
                }
                if (ui.proximas.isEmpty()) item {
                    EstadoVazio("Sem próximos prazos", "As atividades pendentes com prazo de hoje em diante aparecem aqui.")
                }
                items(ui.proximas, key = { it.id }) { item -> AtividadeCard(item, ui.hoje, { onDetalhe(item.id) }) }
            }
        }
    }
}
@Composable
private fun ResumoCard(titulo: String, quantidade: Int, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(quantidade.toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(titulo, style = MaterialTheme.typography.labelMedium)
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InicioPreview() = EstudaMaisTheme { InicioScreen(AtividadesUiState(atividadesPreview, carregando = false), {}, {}, {}, {}) }
