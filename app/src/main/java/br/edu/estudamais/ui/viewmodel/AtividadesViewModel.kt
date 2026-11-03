package br.edu.estudamais.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.estudamais.data.repository.AtividadeRepository
import br.edu.estudamais.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

data class AtividadesUiState(
    val atividades: List<Atividade> = emptyList(),
    val busca: String = "",
    val filtro: Filtro = Filtro.TODAS,
    val hoje: Long = LocalDate.now().toEpochDay(),
    val carregando: Boolean = true,
    val erro: String? = null
) {
    val visiveis get() = filtrarAtividades(atividades, busca, filtro, hoje)
    val resumo get() = resumir(atividades, hoje)
    val proximas get() = filtrarAtividades(atividades, "", Filtro.PENDENTES, hoje)
        .filter { it.prazoDia >= hoje }.take(4)
}

@HiltViewModel
class AtividadesViewModel @Inject constructor(
    private val repository: AtividadeRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val _ui = MutableStateFlow(AtividadesUiState(
        busca = savedState["busca"] ?: "",
        filtro = Filtro.entries.firstOrNull { it.name == savedState.get<String>("filtro") } ?: Filtro.TODAS
    ))
    val ui: StateFlow<AtividadesUiState> = _ui.asStateFlow()
    private var observacao: Job? = null

    init {
        tentarNovamente()
        viewModelScope.launch {
            while (isActive) { delay(30_000); atualizarDia() }
        }
    }
    fun atualizarDia() { _ui.update { it.copy(hoje = LocalDate.now().toEpochDay()) } }
    fun buscar(texto: String) {
        savedState["busca"] = texto
        _ui.update { it.copy(busca = texto) }
    }
    fun filtrar(filtro: Filtro) {
        savedState["filtro"] = filtro.name
        _ui.update { it.copy(filtro = filtro) }
    }
    fun limparBusca() { buscar(""); filtrar(Filtro.TODAS) }
    fun tentarNovamente() {
        observacao?.cancel()
        _ui.update { it.copy(carregando = true, erro = null) }
        observacao = viewModelScope.launch {
            try {
                repository.observarTodas().collect { lista ->
                    _ui.update { it.copy(atividades = lista, carregando = false, erro = null) }
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) {
                _ui.update { it.copy(carregando = false, erro = "Não foi possível carregar as atividades.") }
            }
        }
    }
}
