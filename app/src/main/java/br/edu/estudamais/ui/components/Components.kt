package br.edu.estudamais.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.edu.estudamais.model.*

@Composable
fun Etiqueta(texto: String, cor: Color = MaterialTheme.colorScheme.primaryContainer,
    textoCor: Color = MaterialTheme.colorScheme.onPrimaryContainer) {
    Surface(color = cor, contentColor = textoCor, shape = RoundedCornerShape(8.dp)) {
        Text(texto, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
    }
}
@Composable
fun PrioridadeEtiqueta(prioridade: Prioridade) {
    val cores = when (prioridade) {
        Prioridade.ALTA -> Color(0xFFFFE3DF) to Color(0xFF9E2C20)
        Prioridade.MEDIA -> Color(0xFFFFEFC6) to Color(0xFF765414)
        Prioridade.BAIXA -> Color(0xFFE3EDFF) to Color(0xFF315482)
    }
    Etiqueta("Prioridade ${prioridade.rotulo.lowercase()}", cores.first, cores.second)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AtividadeCard(atividade: Atividade, hoje: Long, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(atividade.disciplina.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(atividade.titulo, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis,
                textDecoration = if (atividade.concluida) TextDecoration.LineThrough else TextDecoration.None)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                PrioridadeEtiqueta(atividade.prioridade)
                Etiqueta(atividade.situacao(hoje),
                    cor = if (atividade.estaAtrasada(hoje)) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    textoCor = if (atividade.estaAtrasada(hoje)) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Entrega: ${formatarPrazo(atividade.prazoDia)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
@Composable
fun EstadoVazio(titulo: String, texto: String, modifier: Modifier = Modifier, acao: String? = null, onAcao: () -> Unit = {}) {
    Column(modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(24.dp)) {
            Icon(Icons.AutoMirrored.Outlined.MenuBook, null, Modifier.padding(20.dp).size(34.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (acao != null) Button(onClick = onAcao) { Text(acao) }
    }
}
@Composable
fun Carregando(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}
@Composable
fun AvisoErro(texto: String, onTentar: (() -> Unit)? = null) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(texto, color = MaterialTheme.colorScheme.onErrorContainer)
            if (onTentar != null) TextButton(onClick = onTentar) { Text("Tentar novamente") }
        }
    }
}
