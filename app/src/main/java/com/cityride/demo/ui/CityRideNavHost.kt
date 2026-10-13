package com.cityride.demo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cityride.demo.ui.screens.AcquistaScreen
import com.cityride.demo.ui.screens.ActiveTicketScreen
import com.cityride.demo.ui.screens.HomeScreen
import com.cityride.demo.ui.screens.MyTicketsScreen
import com.cityride.demo.ui.screens.ProfileScreen
import com.cityride.demo.ui.screens.ScannerScreen
import com.cityride.demo.ui.theme.CityColors
import com.cityride.demo.ui.viewmodel.AppViewModel

private object Routes {
    const val HOME = "home"
    const val ACQUISTA = "acquista"
    const val TICKETS = "tickets"
    const val PROFILE = "profile"
    const val SCANNER = "scanner"
    const val ACTIVATE = "activate/{id}"
    const val TICKET = "ticket/{id}?inspector={inspector}&intro={intro}"
    fun activate(id: String) = "activate/$id"
    fun ticket(id: String, inspector: Boolean = false, intro: Boolean = false) =
        "ticket/$id?inspector=$inspector&intro=$intro"
}

private val tabs = listOf(Routes.HOME, Routes.ACQUISTA, Routes.TICKETS, Routes.PROFILE)

@Composable
fun CityRideNavHost(viewModel: AppViewModel) {
    val nav = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val user by viewModel.user.collectAsStateWithLifecycle()
    val tickets by viewModel.tickets.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.messages.collect { snackbar.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = { if (route in tabs) BottomBar(nav, route, initials(user.fullName)) },
        containerColor = Color.White,
    ) { padding ->
        NavHost(nav, startDestination = Routes.HOME, modifier = Modifier.padding(padding)) {
            composable(Routes.HOME) {
                HomeScreen(
                    onTickets = { nav.switchTab(Routes.ACQUISTA) },
                    onSubscriptions = { nav.switchTab(Routes.ACQUISTA) },
                    onScanner = { nav.navigate(Routes.SCANNER) },
                )
            }
            composable(Routes.ACQUISTA) { AcquistaScreen(onPurchase = viewModel::purchase) }
            composable(Routes.TICKETS) {
                MyTicketsScreen(
                    tickets = tickets,
                    onActivate = { nav.navigate(Routes.activate(it.id)) },
                    onOpen = { nav.navigate(Routes.ticket(it.id)) },
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(user = user, onTopUp = { viewModel.topUp() }, onScanner = { nav.navigate(Routes.SCANNER) })
            }
            composable(Routes.ACTIVATE, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                ScannerScreen(
                    title = "Attiva ticket",
                    requireBusNumber = true,
                    onBack = { nav.popBackStack() },
                    onScanned = { _, bus ->
                        viewModel.activate(id, bus) { activatedId ->
                            nav.navigate(Routes.ticket(activatedId, intro = true)) { popUpTo(Routes.TICKETS) }
                        }
                    },
                )
            }
            composable(
                Routes.TICKET,
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("inspector") { type = NavType.BoolType; defaultValue = false },
                    navArgument("intro") { type = NavType.BoolType; defaultValue = false },
                ),
            ) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                val inspector = entry.arguments?.getBoolean("inspector") ?: false
                val intro = entry.arguments?.getBoolean("intro") ?: false
                val ticket by remember(id) { viewModel.ticket(id) }.collectAsStateWithLifecycle()
                ActiveTicketScreen(ticket, inspectorMode = inspector, playIntro = intro, onBack = { nav.popBackStack() })
            }
            composable(Routes.SCANNER) {
                ScannerScreen(
                    onBack = { nav.popBackStack() },
                    onScanned = { payload, bus ->
                        viewModel.resolveScan(payload, bus) { id ->
                            nav.navigate(Routes.ticket(id, inspector = true)) { popUpTo(Routes.SCANNER) { inclusive = true } }
                        }
                    },
                )
            }
        }
    }
}

private fun NavHostController.switchTab(route: String) = navigate(route) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@Composable
private fun BottomBar(nav: NavHostController, current: String?, initials: String) {
    androidx.compose.foundation.layout.Column {
        HorizontalDivider(color = CityColors.Border)
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            val colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CityColors.Navy,
                selectedTextColor = CityColors.Navy,
                unselectedIconColor = Color(0xFF3A3F45),
                unselectedTextColor = Color(0xFF555A60),
                indicatorColor = Color.Transparent,
            )
            val items = listOf(
                Triple(Routes.HOME, "Home", Icons.Outlined.Home),
                Triple(Routes.ACQUISTA, "Acquista", Icons.Outlined.Storefront),
                Triple(Routes.TICKETS, "I miei ticket", Icons.Outlined.Description),
            )
            items.forEach { (route, label, icon) ->
                NavigationBarItem(
                    selected = current == route,
                    onClick = { nav.switchTab(route) },
                    icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(30.dp)) },
                    label = { Text(label, fontSize = 13.sp, fontWeight = if (current == route) FontWeight.Bold else FontWeight.Normal) },
                    colors = colors,
                )
            }
            val selected = current == Routes.PROFILE
            NavigationBarItem(
                selected = selected,
                onClick = { nav.switchTab(Routes.PROFILE) },
                icon = {
                    Surface(
                        shape = CircleShape,
                        color = if (selected) Color(0xFFE3ECF5) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) CityColors.Navy else Color(0xFF3A3F45)),
                    ) {
                        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                            Text(initials, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selected) CityColors.Navy else Color(0xFF3A3F45))
                        }
                    }
                },
                label = { Text("Profilo", fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                colors = colors,
            )
        }
    }
}

private fun initials(name: String) =
    name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
