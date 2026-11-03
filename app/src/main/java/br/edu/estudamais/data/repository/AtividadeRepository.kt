package br.edu.estudamais.data.repository

import br.edu.estudamais.model.Atividade
import kotlinx.coroutines.flow.Flow

interface AtividadeRepository {
    fun observarTodas(): Flow<List<Atividade>>
    fun observarPorId(id: Long): Flow<Atividade?>
    suspend fun buscarPorId(id: Long): Atividade?
    suspend fun inserir(atividade: Atividade): Long
    suspend fun atualizar(atividade: Atividade)
    suspend fun alterarConclusao(id: Long, concluida: Boolean)
    suspend fun excluir(id: Long)
}
