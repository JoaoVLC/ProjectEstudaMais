# Roteiro de estudo e apresentação em trio

## Ideia em uma frase

“O Estuda+ organiza atividades acadêmicas, permite acompanhar prazos e prioridades e salva os registros localmente para o estudante continuar de onde parou.”

## Divisão sugerida para preparar a apresentação

Esta é uma divisão de estudo e responsabilidade, não uma declaração de autoria. Cada integrante deve compreender o conjunto e explicar o que realmente fez. Registrem contribuições concretas: testes, revisão visual, correção de bugs, documentação ou implementação.

| Integrante | Preparar e demonstrar | Arquivos para estudar |
| --- | --- | --- |
| 1 | Problema, quatro telas, Compose, Material 3, navegação e Preview | `ui/screens`, `ui/components`, `ui/navigation` |
| 2 | Entity, DAO, Repository, CRUD e persistência após reabrir | `model`, `data/local`, `data/repository` |
| 3 | MVVM, estados, coroutines, Hilt, validação e testes | `ui/viewmodel`, `di`, `src/test` |

Antes de dividir a fala, todos devem abrir o projeto no próprio computador e fazer ao menos um ciclo completo de CRUD.

## Demonstração sugerida — 7 a 10 minutos

1. Apresentar o problema e mostrar o painel com os contadores.
2. Explicar as quatro telas e navegar entre Início e Atividades.
3. Abrir um Preview no Studio; mostrar que usa dados de exemplo sem abrir o banco.
4. Cadastrar uma atividade com título, disciplina, data e prioridade.
5. Buscar a atividade e mostrar os detalhes.
6. Editar o título e salvar. Mostrar que o mesmo registro foi atualizado.
7. Marcar como concluída e mostrar a mudança no filtro e no painel.
8. Cadastrar uma segunda atividade e excluí-la com confirmação.
9. Fechar completamente e abrir o app novamente. Conferir que a primeira continua salva.
10. Mostrar rapidamente o fluxo das camadas e onde o Hilt fornece as dependências.

Evitem fazer a primeira sincronização durante a apresentação. Baixem dependências e iniciem o emulador antes. Preparem duas ou três atividades plausíveis; os indicadores devem corresponder aos dados reais.

## Perguntas que vocês devem saber responder

**O que é CRUD?** Criar, ler/listar, atualizar e excluir. Cadastro, lista, edição/conclusão e exclusão realizam essas operações no app.

**Por que Room?** Para persistir os dados localmente usando entidades e DAOs, sem depender de servidor ou login neste escopo.

**Onde ficam os dados?** No banco `estudamais.db`, na área privada do aplicativo daquele aparelho. Não há sincronização entre colegas.

**O que é ID?** Identificador único gerado automaticamente pela chave primária do Room; permite editar ou excluir o registro correto, mesmo com títulos iguais.

**O que faz a Entity?** Define a tabela e suas colunas. A `AtividadeEntity` é convertida para o modelo usado na interface.

**O que faz o DAO?** Declara as operações SQL/Room: inserir, consultar, atualizar, alterar conclusão e excluir.

**Por que Repository?** Centraliza o acesso aos dados; o ViewModel depende do contrato `AtividadeRepository`, não diretamente do banco.

**Como MVVM aparece?** A tela recebe estado e envia eventos; o ViewModel valida e coordena as operações; Repository e DAO cuidam da persistência.

**Por que StateFlow e Flow?** O DAO observa alterações do banco com Flow. Os ViewModels publicam o estado atual em StateFlow. A interface coleta esse estado respeitando o ciclo de vida.

**Onde usamos coroutines?** Nos ViewModels, com `viewModelScope`, para consultar e gravar sem bloquear a interface. As operações do Room são `suspend` ou Flow.

**O que o Hilt faz?** Constrói e entrega banco, DAO, Repository e ViewModels. Em `AppModule` mostramos como essas dependências são fornecidas.

**O ViewModel abre o banco?** Não. Recebe o Repository no construtor. O módulo Hilt é responsável pela criação e o banco tem escopo singleton.

**Por que não usar só remember?** Estado de composição não substitui armazenamento permanente. Usamos Room para dados persistentes e SavedStateHandle para o rascunho do formulário.

**O que acontece ao girar?** O ViewModel mantém o estado na recriação da Activity; o rascunho também é registrado no SavedStateHandle. Isso não substitui salvar no Room.

**Como a lista atualiza?** O Room emite uma nova lista quando a tabela muda; o ViewModel atualiza o estado e o Compose recompõe a interface. Não precisa reiniciar o app.

**Como se calcula atraso?** A atividade não pode estar concluída e o prazo precisa ser anterior ao dia local atual. Prazo de hoje não é atraso.

**Por que pendentes inclui atrasadas?** Atraso é uma classificação das atividades ainda não concluídas. Os contadores não são todos conjuntos separados.

**Por que quatro telas e só duas abas?** Início e Atividades são destinos principais. Formulário e Detalhes fazem parte do fluxo e são abertos a partir deles.

**O que foi testado?** Mostrem o relatório real de testes e a lista que vocês executaram. Não afirmem ter validado algo apenas porque existe um teste no código.

**O que pode ser evoluído?** Notificações de prazo, sincronização, autenticação e categorias. Não são apresentados como implementados nesta versão.

## Relação com a rubrica

| Critério | Onde demonstrar |
| --- | --- |
| R1 — Compose e navegação | Quatro telas, Material 3, busca, filtros e rotas Navigation Compose. |
| R2 — Persistência | Entity, DAO, Database, CRUD e dados após reabertura. |
| R3 — MVVM | Telas sem DAO; ViewModels e contrato Repository separados. |
| R4 — Hilt | Application, Activity, módulos, ViewModels e construtores anotados. |
| R5 — Apresentação | Todos executam, explicam decisões e descrevem sua contribuição real. |

Atender aos itens técnicos não garante automaticamente uma nota: a validação, a qualidade da demonstração e o entendimento de vocês também são avaliados.
