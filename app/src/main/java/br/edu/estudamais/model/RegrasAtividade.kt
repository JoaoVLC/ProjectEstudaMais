package br.edu.estudamais.model

import java.text.Normalizer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Regras puras: testadas sem Android, banco de dados ou interface.
fun Atividade.estaAtrasada(hoje: Long) = !concluida && prazoDia < hoje
fun Atividade.situacao(hoje: Long) = when {
    concluida -> "Concluída"
    estaAtrasada(hoje) -> "Atrasada"
    prazoDia == hoje -> "Para hoje"
    else -> "Pendente"
}
fun formatarPrazo(dia: Long): String = LocalDate.ofEpochDay(dia)
    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")))

private fun String.paraBusca() = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "").lowercase(Locale.ROOT).trim()

fun filtrarAtividades(itens: List<Atividade>, busca: String, filtro: Filtro, hoje: Long): List<Atividade> {
    val termo = busca.paraBusca()
    return itens.filter { item ->
        val corresponde = termo.isEmpty() || listOf(item.titulo, item.disciplina, item.descricao)
            .any { termo in it.paraBusca() }
        corresponde && when (filtro) {
            Filtro.TODAS -> true
            Filtro.PENDENTES -> !item.concluida
            Filtro.ATRASADAS -> item.estaAtrasada(hoje)
            Filtro.CONCLUIDAS -> item.concluida
        }
    }.sortedWith(compareBy<Atividade> { it.concluida }.thenBy { it.prazoDia }
        .thenByDescending { it.prioridade.peso }.thenByDescending { it.id })
}

data class Resumo(val total: Int, val pendentes: Int, val atrasadas: Int, val concluidas: Int) {
    val progresso: Float get() = if (total == 0) 0f else concluidas.toFloat() / total
}
fun resumir(itens: List<Atividade>, hoje: Long) = Resumo(
    total = itens.size,
    pendentes = itens.count { !it.concluida },
    atrasadas = itens.count { it.estaAtrasada(hoje) },
    concluidas = itens.count { it.concluida }
)

data class ErrosFormulario(
    val titulo: String? = null,
    val disciplina: String? = null,
    val descricao: String? = null,
    val prazo: String? = null
) {
    val valido: Boolean get() = listOf(titulo, disciplina, descricao, prazo).all { it == null }
}
fun validarFormulario(titulo: String, disciplina: String, descricao: String, prazo: Long?) = ErrosFormulario(
    titulo = when { titulo.isBlank() -> "Informe o título da atividade."; titulo.trim().length > 100 -> "Use até 100 caracteres."; else -> null },
    disciplina = when { disciplina.isBlank() -> "Informe a disciplina."; disciplina.trim().length > 60 -> "Use até 60 caracteres."; else -> null },
    descricao = if (descricao.trim().length > 2000) "Use até 2.000 caracteres." else null,
    prazo = if (prazo == null) "Escolha um prazo." else null
)
