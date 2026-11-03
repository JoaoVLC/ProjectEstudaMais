package br.edu.estudamais.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.estudamais.data.repository.AtividadeRepository
import br.edu.estudamais.model.Atividade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DetalhesUiState(
    val atividade: Atividade? = null,
    val carregando: Boolean = true,
    val ocupado: Boolean = false,
    val excluida: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class DetalhesViewModel @Inject constructor(
    private val repository: AtividadeRepository,
    savedState: SavedStateHandle
) : ViewModel() {
    private val id: Long = checkNotNull(savedState["id"])
    private val _ui = MutableStateFlow(DetalhesUiState())
    val ui: StateFlow<DetalhesUiState> = _ui.asStateFlow()
    private var observacao: Job? = null
    init { carregar() }
    fun carregar() {
        observacao?.cancel()
        _ui.update { it.copy(carregando = true, erro = null) }
        observacao = viewModelScope.launch {
            try {
                repository.observarPorId(id).collect { item ->
                    _ui.update { it.copy(atividade = item, carregando = false) }
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { _ui.update { it.copy(carregando = false, erro = "Não foi possível abrir a atividade.") } }
        }
    }
    fun alternarConclusao() {
        val item = _ui.value.atividade ?: return
        executar { repository.alterarConclusao(id, !item.concluida) }
    }
    fun excluir() = executar {
        repository.excluir(id)
        _ui.update { it.copy(excluida = true) }
    }
    private fun executar(operacao: suspend () -> Unit) {
        if (_ui.value.ocupado || _ui.value.excluida) return
        _ui.update { it.copy(ocupado = true, erro = null) }
        viewModelScope.launch {
            try { operacao() }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { _ui.update { it.copy(erro = "A operação falhou. Tente novamente.") } }
            finally { _ui.update { it.copy(ocupado = false) } }
        }
    }
}
