package br.edu.estudamais

import br.edu.estudamais.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class RegrasAtividadeTest {
    private val hoje = LocalDate.of(2026, 10, 29).toEpochDay()
    private fun item(id: Long = 1, prazo: Long = hoje, concluida: Boolean = false, prioridade: Prioridade = Prioridade.MEDIA) =
        Atividade(id, "Revisão de Kotlin", "Programação", "Descrição da atividade", prazo, prioridade, concluida)

    @Test fun prazoHojeNaoEstaAtrasado() { assertFalse(item().estaAtrasada(hoje)) }
    @Test fun ontemEstaAtrasado() { assertTrue(item(prazo = hoje - 1).estaAtrasada(hoje)) }
    @Test fun concluidaNaoFicaAtrasada() { assertFalse(item(prazo = hoje - 1, concluida = true).estaAtrasada(hoje)) }
    @Test fun statusHoje() { assertEquals("Para hoje", item().situacao(hoje)) }
    @Test fun buscaIgnoraAcentosECaixa() { assertEquals(1, filtrarAtividades(listOf(item()), "REVISAO", Filtro.TODAS, hoje).size) }
    @Test fun buscaEncontraDisciplina() { assertEquals(1, filtrarAtividades(listOf(item()), "programacao", Filtro.TODAS, hoje).size) }
    @Test fun pendentesIncluiAtrasadas() {
        val lista = listOf(item(1, hoje - 1), item(2, concluida = true), item(3, hoje + 1))
        assertEquals(listOf(1L, 3L), filtrarAtividades(lista, "", Filtro.PENDENTES, hoje).map { it.id })
    }
    @Test fun filtroAtrasadasExcluiConcluidas() {
        assertEquals(1, filtrarAtividades(listOf(item(1, hoje - 1), item(2, hoje - 2, true)), "", Filtro.ATRASADAS, hoje).size)
    }
    @Test fun ordenaPrazoEPrioridade() {
        val lista = listOf(item(1, hoje + 1), item(2, prioridade = Prioridade.BAIXA), item(3, prioridade = Prioridade.ALTA))
        assertEquals(listOf(3L, 2L, 1L), filtrarAtividades(lista, "", Filtro.TODAS, hoje).map { it.id })
    }
    @Test fun resumoContaDadosReais() {
        assertEquals(Resumo(3, 2, 1, 1), resumir(listOf(item(1), item(2, hoje - 1), item(3, concluida = true)), hoje))
    }
    @Test fun progressoVazioNaoDividePorZero() { assertEquals(0f, resumir(emptyList(), hoje).progresso, 0f) }
    @Test fun rejeitaCamposObrigatoriosVazios() { assertFalse(validarFormulario("  ", "", "", null).valido) }
    @Test fun validaLimitesEDescricaoOpcional() {
        assertTrue(validarFormulario("a".repeat(100), "b".repeat(60), "", hoje).valido)
        assertFalse(validarFormulario("a".repeat(101), "b", "", hoje).valido)
        assertFalse(validarFormulario("a", "b".repeat(61), "", hoje).valido)
        assertFalse(validarFormulario("a", "b", "x".repeat(2001), hoje).valido)
    }
    @Test fun dataFormatadaEmPortugues() { assertEquals("29/10/2026", formatarPrazo(hoje)) }
}
