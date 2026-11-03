package br.edu.estudamais.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.edu.estudamais.model.Atividade
import br.edu.estudamais.model.Prioridade

@Entity(tableName = "atividades")
data class AtividadeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val disciplina: String,
    val descricao: String,
    val prazoDia: Long,
    val prioridade: String,
    val concluida: Boolean
)
fun AtividadeEntity.paraModelo() = Atividade(id, titulo, disciplina, descricao, prazoDia,
    Prioridade.entries.firstOrNull { it.name == prioridade } ?: Prioridade.MEDIA, concluida)
fun Atividade.paraEntity() = AtividadeEntity(id, titulo, disciplina, descricao, prazoDia, prioridade.name, concluida)
