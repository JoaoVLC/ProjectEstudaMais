package br.edu.estudamais.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.estudamais.data.repository.AtividadeRepository
import br.edu.estudamais.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FormularioUiState(
    val id: Long = 0,
    val titulo: String = "",
    val disciplina: String = "",
    val descricao: String = "",
    val prazo: Long? = null,
    val prioridade: Prioridade = Prioridade.MEDIA,
    val carregando: Boolean = false,
    val salvando: Boolean = false,
    val salvo: Boolean = false,
    val alterado: Boolean = false,
    val naoEncontrada: Boolean = false,
    val erros: ErrosFormulario = ErrosFormulario(),
    val erro: String? = null
)

@HiltViewModel
class FormularioViewModel @Inject constructor(
    private val repository: AtividadeRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val id: Long = savedState["id"] ?: 0L
    private val temRascunho = savedState.get<Boolean>("rascunho") == true
    private val _ui = MutableStateFlow(FormularioUiState(
        id = id, titulo = savedState["titulo"] ?: "", disciplina = savedState["disciplina"] ?: "",
        descricao = savedState["descricao"] ?: "", prazo = savedState["prazo"],
        prioridade = Prioridade.entries.firstOrNull { it.name == savedState.get<String>("prioridade") } ?: Prioridade.MEDIA,
        alterado = temRascunho, carregando = id > 0
    ))
    val ui: StateFlow<FormularioUiState> = _ui.asStateFlow()
    init { if (id > 0) carregar() }

    fun carregar() {
        _ui.update { it.copy(carregando = true, erro = null) }
        viewModelScope.launch {
            try {
                val item = repository.buscarPorId(id)
                _ui.update {
                    when {
                        item == null -> it.copy(carregando = false, naoEncontrada = true)
                        savedState.get<Boolean>("rascunho") == true -> it.copy(carregando = false)
                        else -> it.copy(titulo = item.titulo, disciplina = item.disciplina, descricao = item.descricao,
                            prazo = item.prazoDia, prioridade = item.prioridade, carregando = false)
                    }
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { _ui.update { it.copy(carregando = false, erro = "Falha ao abrir a atividade. Tente novamente.") } }
        }
    }
    private fun alterar(transformar: (FormularioUiState) -> FormularioUiState) {
        if (_ui.value.salvando || _ui.value.carregando || _ui.value.salvo) return
        val novo = transformar(_ui.value).copy(alterado = true, erros = ErrosFormulario(), erro = null)
        _ui.value = novo
        savedState["rascunho"] = true
        savedState["titulo"] = novo.titulo
        savedState["disciplina"] = novo.disciplina
        savedState["descricao"] = novo.descricao
        savedState["prazo"] = novo.prazo
        savedState["prioridade"] = novo.prioridade.name
    }
    fun titulo(valor: String) = alterar { it.copy(titulo = valor) }
    fun disciplina(valor: String) = alterar { it.copy(disciplina = valor) }
    fun descricao(valor: String) = alterar { it.copy(descricao = valor) }
    fun prazo(valor: Long) = alterar { it.copy(prazo = valor) }
    fun prioridade(valor: Prioridade) = alterar { it.copy(prioridade = valor) }

    fun salvar() {
        val atual = _ui.value
        if (atual.salvando || atual.carregando || atual.salvo || atual.naoEncontrada) return
        val erros = validarFormulario(atual.titulo, atual.disciplina, atual.descricao, atual.prazo)
        if (!erros.valido) { _ui.update { it.copy(erros = erros) }; return }
        // A trava é ativada antes da coroutine: dois cliques não geram dois registros.
        _ui.update { it.copy(salvando = true, erro = null) }
        viewModelScope.launch {
            try {
                val anterior = if (id > 0) checkNotNull(repository.buscarPorId(id)) else null
                val item = Atividade(id, atual.titulo.trim(), atual.disciplina.trim(), atual.descricao.trim(),
                    requireNotNull(atual.prazo), atual.prioridade, anterior?.concluida ?: false)
                if (id == 0L) repository.inserir(item) else repository.atualizar(item)
                _ui.update { it.copy(salvando = false, salvo = true, alterado = false) }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) {
                _ui.update { it.copy(salvando = false, erro = "Não foi possível salvar. Seus campos foram mantidos; tente novamente.") }
            }
        }
    }
}
