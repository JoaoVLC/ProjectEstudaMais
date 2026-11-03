package br.edu.estudamais.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import br.edu.estudamais.ui.screens.*
import br.edu.estudamais.ui.viewmodel.*
import kotlinx.coroutines.launch

@Composable
fun EstudaMaisApp() {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rota = entrada?.destination?.route
    val principal = rota == null || rota == "inicio" || rota == "atividades"
    // Compartilhado entre Início e Lista; os formulários usam VMs por destino.
    val listaVm: AtividadesViewModel = hiltViewModel()
    val lista by listaVm.ui.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner, listaVm) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) listaVm.atualizarDia() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    fun abrirAba(destino: String) {
        nav.navigate(destino) { popUpTo("inicio") { saveState = true }; launchSingleTop = true; restoreState = true }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snack) },
        bottomBar = {
            if (principal) NavigationBar {
                NavigationBarItem(selected = rota == "inicio" || rota == null, onClick = { abrirAba("inicio") },
                    icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Início") })
                NavigationBarItem(selected = rota == "atividades", onClick = { abrirAba("atividades") },
                    icon = { Icon(Icons.AutoMirrored.Outlined.Assignment, null) }, label = { Text("Atividades") })
            }
        },
        floatingActionButton = {
            if (rota == "atividades") ExtendedFloatingActionButton(onClick = { nav.navigate("formulario") },
                icon = { Icon(Icons.Default.Add, null) }, text = { Text("Nova atividade") })
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = "inicio", modifier = Modifier.padding(padding).consumeWindowInsets(padding)) {
            composable("inicio") {
                InicioScreen(lista, onNova = { nav.navigate("formulario") },
                    onLista = { listaVm.buscar(""); listaVm.filtrar(it); abrirAba("atividades") },
                    onDetalhe = { nav.navigate("detalhes/$it") }, onTentar = listaVm::tentarNovamente)
            }
            composable("atividades") {
                AtividadesScreen(lista, listaVm::buscar, listaVm::filtrar, { nav.navigate("detalhes/$it") }, listaVm::limparBusca, listaVm::tentarNovamente)
            }
            composable("formulario?id={id}", arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = 0L })) {
                val vm: FormularioViewModel = hiltViewModel()
                val ui by vm.ui.collectAsStateWithLifecycle()
                LaunchedEffect(ui.salvo) {
                    if (ui.salvo) {
                        nav.popBackStack()
                        scope.launch { snack.showSnackbar(if (ui.id == 0L) "Atividade cadastrada." else "Alterações salvas.") }
                    }
                }
                FormularioScreen(ui, vm::titulo, vm::disciplina, vm::descricao, vm::prazo, vm::prioridade,
                    vm::salvar, { nav.popBackStack() }, vm::carregar)
            }
            composable("detalhes/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                val vm: DetalhesViewModel = hiltViewModel()
                val ui by vm.ui.collectAsStateWithLifecycle()
                LaunchedEffect(ui.excluida) {
                    if (ui.excluida) { nav.popBackStack(); scope.launch { snack.showSnackbar("Atividade excluída.") } }
                }
                DetalhesScreen(ui, lista.hoje, { nav.popBackStack() }, { nav.navigate("formulario?id=$it") },
                    vm::alternarConclusao, vm::excluir, vm::carregar)
            }
        }
    }
}
