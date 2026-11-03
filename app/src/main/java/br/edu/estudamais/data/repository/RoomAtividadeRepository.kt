package br.edu.estudamais.data.repository

import br.edu.estudamais.data.local.*
import br.edu.estudamais.model.Atividade
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomAtividadeRepository @Inject constructor(private val dao: AtividadeDao) : AtividadeRepository {
    override fun observarTodas() = dao.observarTodas().map { lista -> lista.map { it.paraModelo() } }
    override fun observarPorId(id: Long) = dao.observarPorId(id).map { it?.paraModelo() }
    override suspend fun buscarPorId(id: Long) = dao.buscarPorId(id)?.paraModelo()
    override suspend fun inserir(atividade: Atividade) = dao.inserir(atividade.paraEntity())
    override suspend fun atualizar(atividade: Atividade) {
        check(dao.atualizar(atividade.paraEntity()) == 1) { "Atividade não encontrada." }
    }
    override suspend fun alterarConclusao(id: Long, concluida: Boolean) {
        check(dao.alterarConclusao(id, concluida) == 1) { "Atividade não encontrada." }
    }
    override suspend fun excluir(id: Long) { dao.excluir(id) }
}
