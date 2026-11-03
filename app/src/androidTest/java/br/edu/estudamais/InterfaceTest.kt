package br.edu.estudamais

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.edu.estudamais.ui.screens.*
import br.edu.estudamais.ui.theme.EstudaMaisTheme
import br.edu.estudamais.ui.viewmodel.*
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class InterfaceTest {
    @get:Rule val compose = createComposeRule()
    @Test fun listaVaziaOrientaPrimeiroCadastro() {
        compose.setContent { EstudaMaisTheme { AtividadesScreen(AtividadesUiState(carregando = false), {}, {}, {}, {}, {}) } }
        compose.onNodeWithText("Comece seu planejamento").assertIsDisplayed()
    }
    @Test fun botaoEncaminhaEventoSalvar() {
        var salvou = false
        compose.setContent { EstudaMaisTheme { FormularioScreen(FormularioUiState(), {}, {}, {}, {}, {}, { salvou = true }, {}, {}) } }
        compose.onNodeWithText("Salvar atividade").performClick()
        compose.runOnIdle { assertTrue(salvou) }
    }
}
