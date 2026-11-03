# Verificação da versão 1.0

Este registro distingue compilação e testes locais de uma execução real no Android. Não foi feita uma apresentação nem uma validação visual no computador do estudante.

## Verificações executadas

| Verificação | Resultado |
| --- | --- |
| Compilação `:app:assembleDebug` | Aprovada; APK de desenvolvimento gerado. |
| Recompilação após editar uma mensagem em Kotlin | Aprovada, sem `clean`, com a configuração final; 19 testes passaram novamente. |
| Testes `:app:testDebugUnitTest` | 19 executados, 0 falhas, 0 erros, 0 ignorados. |
| Regras de negócio | 14 testes aprovados. |
| FormularioViewModel | 5 testes aprovados, com Repository falso e dispatcher de teste. |
| Compilação `:app:assembleDebugAndroidTest` | Aprovada; APK dos testes instrumentados gerado. |
| Análise `:app:lintDebug` | Aprovada, sem erros; 2 avisos informando versões mais recentes de bibliotecas/plugins. |
| Metadados do APK | Pacote `br.edu.estudamais`, versão 1.0, mínimo API 24, alvo API 36. |
| Geração do Room | Schema versão 1 exportado; chave primária INTEGER AUTOINCREMENT. |
| Estrutura | 3 ViewModels Hilt, 4 Previews, sem TODOs de funcionalidade pendentes. |

Ambiente de compilação: Linux, Eclipse Temurin JDK 17, Gradle 8.13, AGP 8.13.2, SDK 36 e Build-Tools 35.0.0. As dependências foram resolvidas e os processadores de Room e Hilt executados de fato.

## O que ainda precisa ser executado no emulador

- Testes instrumentados `:app:connectedDebugAndroidTest`: **não executados nesta preparação**, pois não havia emulador/aparelho conectado.
- Demonstração manual do cadastro, edição, conclusão, busca, filtros e exclusão.
- Confirmação visual de telas, teclado, rotação, tema escuro, fontes maiores e Previews no Android Studio.
- Fechar/abrir o aplicativo e reiniciar o emulador para observar os registros reais persistidos pelo Room.

O teste de persistência Room está implementado e compilado, mas isso não equivale a executá-lo. Os testes unitários aprovados não substituem esses testes Android. Use `TESTES.md` como roteiro de aceitação antes de apresentar.

## Ajuste de recompilação

Durante a preparação, recompilações incrementais apresentaram conflitos de cache, embora uma compilação limpa fosse bem-sucedida. A versão entregue desativa a compilação incremental Kotlin em `gradle.properties`. O ajuste não muda as funcionalidades; prioriza a consistência da compilação em vez de compilar apenas trechos alterados.

Após o ajuste e a limpeza dos resíduos antigos, foram repetidos a compilação completa e um ciclo de edição/recompilação sem limpeza, ambos com sucesso. Os testes unitários foram executados novamente nesse último ciclo.

Caso a IDE mantenha resíduos de outra configuração, execute uma compilação limpa antes de voltar a editar:

```powershell
.\gradlew.bat clean :app:assembleDebug :app:testDebugUnitTest
```

Não altere versões, apague o banco do aplicativo nem desinstale o app para corrigir problemas de cache da compilação: são assuntos diferentes. `clean` remove somente os arquivos gerados do projeto, não os dados no emulador.

## Repetir as verificações

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug
# Com o emulador iniciado:
.\gradlew.bat :app:connectedDebugAndroidTest
```

O ZIP contém fontes, Gradle Wrapper, schema Room, testes e documentação. SDKs, caches, caminhos pessoais do SDK, banco de dados, chaves de assinatura e APKs gerados não são compartilhados. Os APKs podem ser gerados pelo Android Studio a partir deste projeto.
