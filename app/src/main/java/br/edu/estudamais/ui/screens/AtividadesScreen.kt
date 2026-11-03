package br.edu.estudamais.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.estudamais.model.Filtro
import br.edu.estudamais.ui.components.*
import br.edu.estudamais.ui.theme.EstudaMaisTheme
import br.edu.estudamais.ui.viewmodel.AtividadesUiState

@Composable
fun AtividadesScreen(ui: AtividadesUiState, onBusca: (String) -> Unit, onFiltro: (Filtro) -> Unit,
    onDetalhe: (Long) -> Unit, onLimpar: () -> Unit, onTentar: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Minhas atividades", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 20.dp))
        Text("Organize os prazos, cuide do seu ritmo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(value = ui.busca, onValueChange = onBusca, modifier = Modifier.fillMaxWidth(), singleLine = true,
            label = { Text("Buscar atividades") }, placeholder = { Text("Título, disciplina ou descrição") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = { if (ui.busca.isNotEmpty()) IconButton(onClick = { onBusca("") }) { Icon(Icons.Default.Close, "Limpar busca") } })
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Filtro.entries.forEach { filtro -> FilterChip(selected = ui.filtro == filtro, onClick = { onFiltro(filtro) }, label = { Text(filtro.rotulo) }) }
        }
        when {
            ui.carregando -> Carregando()
            ui.erro != null -> AvisoErro(ui.erro, onTentar)
            else -> {
                Text("${ui.visiveis.size} atividades encontradas", style = MaterialTheme.typography.labelLarge)
                LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 112.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (ui.visiveis.isEmpty()) item {
                        EstadoVazio(if (ui.atividades.isEmpty()) "Comece seu planejamento" else "Nenhum resultado",
                            if (ui.atividades.isEmpty()) "Toque no + para cadastrar sua primeira atividade." else "Experimente outra busca ou filtro.",
                            acao = if (ui.atividades.isNotEmpty()) "Limpar filtros" else null, onAcao = onLimpar)
                    }
                    items(ui.visiveis, key = { it.id }) { item -> AtividadeCard(item, ui.hoje, { onDetalhe(item.id) }) }
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AtividadesPreview() = EstudaMaisTheme { AtividadesScreen(AtividadesUiState(atividadesPreview, carregando = false), {}, {}, {}, {}, {}) }
