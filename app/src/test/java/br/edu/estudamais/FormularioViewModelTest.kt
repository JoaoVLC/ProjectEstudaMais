package br.edu.estudamais

import androidx.lifecycle.SavedStateHandle
import br.edu.estudamais.data.repository.AtividadeRepository
import br.edu.estudamais.model.*
import br.edu.estudamais.ui.viewmodel.FormularioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private class FakeRepository : AtividadeRepository {
    val itens = MutableStateFlow<List<Atividade>>(emptyList())
    var inclusoes = 0
    var falhar = false
    override fun observarTodas() = itens.asStateFlow()
    override fun observarPorId(id: Long) = itens.map { lista -> lista.firstOrNull { it.id == id } }
    override suspend fun buscarPorId(id: Long) = itens.value.firstOrNull { it.id == id }
    override suspend fun inserir(atividade: Atividade): Long {
        if (falhar) error("Falha simulada")
        inclusoes++
        val id = inclusoes.toLong()
        itens.value = itens.value + atividade.copy(id = id)
        return id
    }
    override suspend fun atualizar(atividade: Atividade) { itens.value = itens.value.map { if (it.id == atividade.id) atividade else it } }
    override suspend fun alterarConclusao(id: Long, concluida: Boolean) {
        itens.value = itens.value.map { if (it.id == id) it.copy(concluida = concluida) else it }
    }
    override suspend fun excluir(id: Long) { itens.value = itens.value.filterNot { it.id == id } }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FormularioViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun preparar() { Dispatchers.setMain(dispatcher) }
    @After fun encerrar() { Dispatchers.resetMain() }
    private fun preencher(vm: FormularioViewModel) {
        vm.titulo("  Revisar Kotlin  "); vm.disciplina("  Mobile  "); vm.prazo(20_000L)
    }
    @Test fun naoSalvaFormularioVazio() = runTest(dispatcher) {
        val repo = FakeRepository(); val vm = FormularioViewModel(repo, SavedStateHandle())
        vm.salvar(); advanceUntilIdle()
        assertEquals(0, repo.inclusoes); assertFalse(vm.ui.value.erros.valido)
    }
    @Test fun salvaLimpaEspacosEBloqueiaCliqueDuplo() = runTest(dispatcher) {
        val repo = FakeRepository(); val vm = FormularioViewModel(repo, SavedStateHandle())
        preencher(vm); vm.salvar(); vm.salvar(); advanceUntilIdle()
        assertEquals(1, repo.inclusoes); assertEquals("Revisar Kotlin", repo.itens.value.single().titulo)
        assertEquals("Mobile", repo.itens.value.single().disciplina); assertTrue(vm.ui.value.salvo)
    }
    @Test fun edicaoMantemIdEConclusao() = runTest(dispatcher) {
        val repo = FakeRepository()
        repo.itens.value = listOf(Atividade(5, "Original", "Mobile", prazoDia = 20_000, concluida = true))
        val vm = FormularioViewModel(repo, SavedStateHandle(mapOf("id" to 5L)))
        advanceUntilIdle(); vm.titulo("Editada"); vm.salvar(); advanceUntilIdle()
        assertEquals(5L, repo.itens.value.single().id); assertTrue(repo.itens.value.single().concluida)
        assertEquals("Editada", repo.itens.value.single().titulo); assertEquals(0, repo.inclusoes)
    }
    @Test fun falhaMantemCamposEPermiteTentarNovamente() = runTest(dispatcher) {
        val repo = FakeRepository(); repo.falhar = true
        val vm = FormularioViewModel(repo, SavedStateHandle())
        preencher(vm); vm.salvar(); advanceUntilIdle()
        assertNotNull(vm.ui.value.erro); assertFalse(vm.ui.value.salvando); assertEquals("  Revisar Kotlin  ", vm.ui.value.titulo)
        repo.falhar = false; vm.salvar(); advanceUntilIdle(); assertEquals(1, repo.inclusoes)
    }
    @Test fun recuperaRascunhoDoSavedState() = runTest(dispatcher) {
        val handle = SavedStateHandle(mapOf("rascunho" to true, "titulo" to "Rascunho", "prazo" to 20_000L))
        val vm = FormularioViewModel(FakeRepository(), handle)
        assertEquals("Rascunho", vm.ui.value.titulo); assertEquals(20_000L, vm.ui.value.prazo)
        assertTrue(vm.ui.value.alterado)
    }
}
