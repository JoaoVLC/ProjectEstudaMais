# Checklist de aceitação — Estuda+

Execute no emulador antes da apresentação. Marque somente o que você realmente verificou. Não use dados pessoais reais.

## Preparação

- [ ] Abrir a raiz `EstudaMais` e concluir a sincronização.
- [ ] Executar a configuração `app` e ver o painel Estuda+.
- [ ] Abrir um `@Preview` de cada uma das quatro telas.
- [ ] Se o banco estiver vazio, conferir contadores zerados e mensagem orientando o primeiro cadastro.

## Cadastro e validação

1. Toque em **Nova atividade**, depois em **Salvar atividade**, sem preencher.
2. Confira mensagens nos campos obrigatórios; nenhum registro deve ser criado.
3. Cadastre **Revisar Compose**, disciplina **Mobile**, prazo **hoje**, prioridade **Alta**, descrição **Revisar estado e recomposição**.
4. Confira retorno à tela anterior, mensagem de sucesso e novo item na lista.
5. Cadastre **Preparar apresentação**, disciplina **Projeto**, prazo **amanhã**, prioridade **Média**.
6. Cadastre **Resolver exercícios**, disciplina **Matemática**, prazo **ontem**, prioridade **Baixa**.

- [ ] Título e disciplina com somente espaços são recusados.
- [ ] Limites de 100/60/2.000 caracteres são validados ao salvar.
- [ ] Data escolhida aparece no formato dia/mês/ano e não muda um dia por causa do fuso.
- [ ] Botão não permite criar duas vezes com clique duplo durante a gravação.

## Lista, busca e indicadores

- [ ] Com os três itens pendentes, Início mostra total 3, pendentes 3, atrasadas 1 e concluídas 0.
- [ ] A atividade de hoje não está atrasada.
- [ ] Próximas entregas contém os itens de hoje e amanhã, não o de ontem.
- [ ] Tocar no contador de Atrasadas abre o filtro correto.
- [ ] Buscar `MATEMATICA` encontra a disciplina `Matemática`.
- [ ] Buscar parte da descrição também encontra a atividade.
- [ ] Busca e filtro funcionam juntos; limpar filtros restaura a lista.
- [ ] Tocar no cartão abre os detalhes do registro correto.

## Alteração, conclusão e exclusão

- [ ] Editar título, disciplina, descrição, data e prioridade; salvar e conferir os novos dados nos detalhes, lista e resumo.
- [ ] A edição preserva o ID, sem criar outra atividade.
- [ ] Concluir uma atividade altera os contadores e o filtro Concluídas.
- [ ] Editar uma atividade concluída não a torna pendente automaticamente.
- [ ] Usar **Marcar como pendente** desfaz a conclusão.
- [ ] Tentar excluir, cancelar e confirmar que o registro continua.
- [ ] Excluir novamente e confirmar: o item desaparece da lista e o resumo atualiza.

## Navegação, estado e persistência

- [ ] Alternar Início/Atividades repetidamente; o Voltar não percorre uma pilha enorme de abas duplicadas.
- [ ] Abrir formulário, digitar e girar o emulador: campos permanecem.
- [ ] Tentar voltar com alterações: aparece confirmação para descartar.
- [ ] Cancelar descarte mantém os campos; confirmar descarte não cria registro.
- [ ] Conferir formulário com teclado aberto e em tela pequena: rolagem permite acessar os campos.
- [ ] Conferir tema claro e escuro e legibilidade com fonte aumentada.
- [ ] Fechar o aplicativo pela lista de recentes e abri-lo pelo ícone: registros continuam.
- [ ] Forçar parada pelas configurações do Android e abrir de novo: registros continuam.
- [ ] Reiniciar o emulador e abrir de novo: registros continuam.

Não desinstale nem limpe os dados para testar persistência: essas ações removem propositalmente o armazenamento.

## Testes automatizados incluídos

- 14 testes de regras: atrasos, data, busca, filtros, ordenação, contadores e validação.
- 5 testes do formulário: validação, clique duplo, edição, falha/retry e rascunho.
- 1 teste instrumentado de Room: inserir/listar/editar/concluir/excluir/reabrir o banco.
- 2 testes instrumentados Compose: estado vazio e evento de salvar.

Os testes instrumentados usam emulador/aparelho. O banco do teste tem nome próprio e não apaga `estudamais.db`. Eles não substituem a demonstração manual completa do fluxo.

Resultado efetivo da preparação: consulte `VERIFICACAO.md`.
