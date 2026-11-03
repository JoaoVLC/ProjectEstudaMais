package br.edu.estudamais.model

data class Atividade(
    val id: Long = 0,
    val titulo: String,
    val disciplina: String,
    val descricao: String = "",
    // Dia civil (não um horário): evita mudar o prazo por causa do fuso.
    val prazoDia: Long,
    val prioridade: Prioridade = Prioridade.MEDIA,
    val concluida: Boolean = false
)

enum class Prioridade(val rotulo: String, val peso: Int) {
    BAIXA("Baixa", 1), MEDIA("Média", 2), ALTA("Alta", 3)
}
enum class Filtro(val rotulo: String) {
    TODAS("Todas"), PENDENTES("Pendentes"), ATRASADAS("Atrasadas"), CONCLUIDAS("Concluídas")
}
