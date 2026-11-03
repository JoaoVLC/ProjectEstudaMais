# Estuda+

Organizador de atividades acadêmicas para Android. O estudante cadastra trabalhos, provas e revisões, acompanha os prazos e marca o que já concluiu.

Projeto de estudo para a avaliação N2: Jetpack Compose, Material 3, Navigation Compose, Room, MVVM e Hilt. Pacote: `br.edu.estudamais`. Não depende de servidor, login, Firebase ou serviço pago.

## 1. Abrir no Android Studio

1. Extraia **todo o ZIP** para uma pasta simples, por exemplo `C:\Projetos\EstudaMais`.
2. Na tela inicial do Android Studio, clique em **Open**. Se já houver outro projeto aberto, use **File → Open**.
3. Escolha a pasta que contém **settings.gradle.kts**, **build.gradle.kts**, **gradlew.bat** e **app**. Não escolha somente `app`.
4. Aguarde a sincronização. A primeira abertura baixa Gradle e bibliotecas; precisa de internet.
5. Instale **Android SDK Platform 36** e **Android SDK Build-Tools 35.0.0** se o Studio solicitar. O SDK Manager também permite instalar esses componentes.
6. Use **JDK 17** como Gradle JDK. Se houver erro relacionado a Java: Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK. Use a opção de baixar/selecionar um JDK 17. Não altere o código para contornar erro de JDK.
7. No Device Manager, inicie um emulador Android API 24 ou superior. Pode usar o Medium Phone que você já configurou. O SDK de compilação não precisa ser igual à versão do emulador.
8. Na barra superior selecione a configuração **app**, escolha o dispositivo e clique em **Run ▶**.
9. O aplicativo abre no painel **Início**, com contadores zerados. Cadastre sua primeira atividade.

Não substitua arquivos do seu projeto inicial por partes deste ZIP. Este é um projeto independente, com versões combinadas entre si. Não aceite migração automática para outra versão de AGP antes de testar esta versão.

## 2. O que o aplicativo faz

| Tela | Funcionalidades |
| --- | --- |
| Início | Resumo real de pendentes, atrasadas e concluídas; progresso; próximas entregas; atalhos para filtros e cadastro. |
| Atividades | Lista persistida; busca por título, disciplina e descrição; filtros Todas, Pendentes, Atrasadas e Concluídas. |
| Nova atividade / Editar | Formulário com título, disciplina, descrição, prazo e prioridade; validação; confirmação para descartar alterações. |
| Detalhes | Dados completos; marcar como concluída ou pendente; editar; excluir com confirmação. |

Cadastro e edição compartilham a mesma tela. Mesmo contando-os como uma só, existem **quatro telas** de um fluxo integrado.

### Regras

- Título obrigatório, até 100 caracteres; disciplina obrigatória, até 60.
- Descrição opcional, até 2.000 caracteres. Espaços no início/fim são removidos ao salvar.
- Prazo obrigatório; prioridade baixa, média ou alta (média selecionada inicialmente).
- Uma atividade é atrasada quando **não está concluída** e o prazo é anterior à data local atual. Prazo de hoje não é atraso.
- **Pendentes inclui atrasadas.** Portanto, não some esses dois contadores para obter o total.
- Próximas entregas mostra até quatro atividades pendentes de hoje em diante. Atrasadas continuam disponíveis no filtro específico.
- Ordenação: pendentes antes de concluídas, prazo crescente, prioridade maior primeiro e ID decrescente como desempate.
- Busca ignora maiúsculas/minúsculas e acentos; busca e filtro podem ser combinados.
- O dia é atualizado ao retornar ao app e periodicamente enquanto ele permanece aberto.
- ID é gerado pelo Room. Não é um campo para digitar e não é o número 10.
- Ao concluir, o registro permanece no banco; excluir remove definitivamente após confirmação.

## 3. Arquitetura e arquivos

| Camada/pacote | Responsabilidade |
| --- | --- |
| `model` | Modelo puro, prioridade, filtros, validação, busca, ordenação e cálculos. |
| `data/local` | Entity, DAO e configuração do banco Room. |
| `data/repository` | Contrato de acesso aos dados e implementação que usa o DAO. |
| `di/AppModule.kt` | Hilt fornece banco singleton, DAO e Repository. |
| `ui/viewmodel` | ViewModels coordenam CRUD, estados e coroutines, sem construir o banco. |
| `ui/screens` | Quatro telas recebem estado e callbacks; não acessam DAO nem Room. |
| `ui/components` | Cartões, etiquetas, carregamento, erro e estado vazio reutilizáveis. |
| `ui/navigation` | Rotas e obtenção dos ViewModels pelo Hilt. |
| `ui/theme` | Cores e tipografia Material 3, com tema claro e escuro. |

O caminho de uma gravação é: **tela → ViewModel → Repository → DAO → Room**. O DAO expõe `Flow`; quando o banco muda, os ViewModels recebem novos dados e publicam `StateFlow`, observado pela interface com `collectAsStateWithLifecycle()`.

O formulário guarda rascunho em `SavedStateHandle`. O ViewModel sobrevive à rotação; o Room mantém os registros entre execuções. São responsabilidades diferentes. A edição usa o mesmo ID e preserva o estado de conclusão. O botão de salvar é bloqueado durante a operação para impedir duplo cadastro.

### Hilt em uso

- `EstudaMaisApplication`: `@HiltAndroidApp`, registrada no Manifest.
- `MainActivity`: `@AndroidEntryPoint`.
- Três ViewModels: `@HiltViewModel` e construtor `@Inject`.
- `RoomAtividadeRepository`: construtor `@Inject`.
- `AppModule`: `@Module`, `@InstallIn`, `@Provides` e `@Singleton`.
- Navegação: `hiltViewModel()` obtém as dependências.

Não há código de criação de banco nas telas ou nos ViewModels. KSP processa **Room e Hilt**; o projeto não mistura KAPT e KSP.

## 4. Versões do projeto

| Componente | Versão |
| --- | --- |
| Gradle / Android Gradle Plugin | 8.13 / 8.13.2 |
| Java / Kotlin | 17 / 2.2.21 |
| KSP | 2.2.21-2.0.4 |
| SDK mínimo / compilação / alvo | 24 / 36 / 36 |
| Compose BOM | 2025.11.01 |
| Navigation Compose | 2.9.5 |
| Room | 2.8.4 |
| Hilt / integração Compose | 2.57.2 / 1.2.0 |
| Lifecycle | 2.9.4 |

As versões estão centralizadas em `gradle/libs.versions.toml`. O wrapper oficial está incluído e tem checksum SHA-256 fixado. O projeto usa desugaring para disponibilizar `java.time` também em APIs anteriores à 26.

A compilação incremental do Kotlin está desativada em `gradle.properties`: foi reproduzido um conflito de cache ao recompilar funções após uma edição. A configuração prioriza consistência; quando o Kotlin muda, a recompilação pode levar um pouco mais de tempo. Isso não altera o comportamento do app.

## 5. Preview sem banco

Abra qualquer arquivo em `app/src/main/java/br/edu/estudamais/ui/screens` que termina em `Screen.kt`, escolha **Split** ou **Design** e aguarde a compilação do Preview. Cada tela possui uma função `@Preview`.

Os exemplos de `DadosPreview.kt` são usados **somente** no Preview. O aplicativo real começa vazio; não há dados falsos para inflar os indicadores. O Preview não instancia Hilt, ViewModel nem banco.

## 6. Testes e execução pelo terminal

No terminal do Android Studio, na raiz do projeto, no Windows:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
# Requer um emulador ou aparelho conectado:
.\gradlew.bat :app:connectedDebugAndroidTest
```

No Linux/macOS use `./gradlew` no lugar de `.\gradlew.bat` (se necessário, `chmod +x gradlew`).

- `src/test`: testes de regras de negócio e ViewModel com Repository falso.
- `src/androidTest`: teste CRUD/persistência Room com banco exclusivo de teste e testes básicos da interface Compose.
- Relatórios locais após executar: `app/build/reports/tests/testDebugUnitTest/index.html` e `app/build/reports/lint-results-debug.html`.

Leia **docs/VERIFICACAO.md** para distinguir o que foi executado na preparação do que precisa ser validado no seu emulador. A presença de um teste no código não significa que ele já foi executado.

## 7. Preservação dos dados

O banco chama-se `estudamais.db` e fica na área privada do aplicativo. Os dados permanecem ao fechar o app, encerrar o processo e reiniciar o emulador. Também permanecem ao reinstalar uma compilação de desenvolvimento compatível sobre o mesmo pacote.

**Desinstalar o app, limpar os dados ou apagar o emulador remove o banco.** Cada aparelho/emulador tem seus próprios registros: não há sincronização entre os colegas. O backup Android foi desativado para tornar esse comportamento explícito.

Se alterarem os campos da Entity no futuro, criem uma migração e atualizem a versão do Room. Não há `fallbackToDestructiveMigration`, pois apagar o banco silenciosamente não atende à preservação dos dados.

## 8. Problemas comuns

- **“Hello Android!” aparece:** você executou o projeto antigo; abra a pasta deste ZIP e selecione `app`.
- **Erro no download de dependências:** confira internet/proxy e se Gradle Offline está desligado; sincronize novamente. Não troque versões aleatoriamente.
- **SDK não encontrado:** instale Platform 36 e deixe o Studio gerar `local.properties` com o caminho do seu SDK. Esse arquivo não é compartilhado no ZIP.
- **Java incompatível:** escolha o Gradle JDK 17 nas configurações do Studio.
- **Emulador lento:** aguarde o primeiro boot; se necessário use o emulador que já funcionou no seu computador. Não é obrigatório ser Pixel 6.
- **Não aparece `app` para executar:** aguarde a sincronização e confira se abriu a raiz correta. Envie a primeira mensagem de erro da janela Build.
- **Não vê dados do colega:** é esperado; o banco é local.
- **Lista vazia após cadastrar:** limpe busca/filtros; uma atividade concluída não aparece em Pendentes.

## 9. Trabalho em equipe

### Integrantes do grupo

- João Vitor Lima Cecilio
- David Rodrigues
- Henrique Dias

O ZIP pode ser enviado aos colegas. Cada um abre uma cópia e executa o checklist de `docs/TESTES.md`. Para juntar alterações, usem Git ou combinem qual pessoa editará cada arquivo; evitem três cópias divergentes sem controle.

`docs/APRESENTACAO.md` contém uma divisão de estudo e um roteiro. Todos precisam executar, compreender e contribuir com testes, revisão ou implementação, descrevendo honestamente sua participação. Não basta decorar uma fala.

## Referências para estudo

- [Jetpack Compose](https://developer.android.com/compose)
- [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation)
- [Room](https://developer.android.com/training/data-storage/room)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Camada de dados](https://developer.android.com/topic/architecture/data-layer)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Configuração de backup Android](https://developer.android.com/identity/data/autobackup)
- [Compilação e cache do Kotlin](https://kotlinlang.org/docs/gradle-compilation-and-caches.html)

O wrapper incluído é uma ferramenta oficial do Gradle; sua licença está em `LICENSE-Gradle.txt`. As bibliotecas mantêm suas respectivas licenças. Não foram adicionados logotipos, imagens de terceiros, credenciais ou chaves de publicação.
