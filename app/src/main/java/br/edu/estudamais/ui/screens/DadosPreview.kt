package br.edu.estudamais.ui.screens

import br.edu.estudamais.model.*
import java.time.LocalDate

// Somente os Previews usam estes dados. O banco real começa vazio.
internal val hojePreview = LocalDate.now().toEpochDay()
internal val atividadesPreview = listOf(
    Atividade(1, "Preparar apresentação do aplicativo", "Mobile Development", "Revisar arquitetura, testes e demonstração do CRUD.", hojePreview + 3, Prioridade.ALTA),
    Atividade(2, "Revisar consultas SQL", "Banco de Dados", "Comparar planos de execução.", hojePreview, Prioridade.MEDIA),
    Atividade(3, "Entregar lista de exercícios", "Matemática", prazoDia = hojePreview - 2, concluida = true)
)
