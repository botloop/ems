package com.ems.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ems.ui.screen.assessment.AssessmentScreen
import com.ems.ui.screen.dashboard.DashboardScreen
import com.ems.ui.screen.welcome.WelcomeScreen
import com.ems.ui.screen.gcs.GcsScreen
import com.ems.ui.screen.mnemonics.MnemonicsScreen
import com.ems.ui.screen.pcr.PcrDetailScreen
import com.ems.ui.screen.pcr.PcrListScreen
import com.ems.ui.screen.vitals.VitalsCalculatorScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Dashboard.route,
        Screen.Assessment.route,
        Screen.PcrList.route,
        Screen.GcsCalculator.route,
        Screen.Mnemonics.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets.ime,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple(Screen.Dashboard, "Dashboard", Icons.Outlined.Home),
                        Triple(Screen.Assessment, "Assess", Icons.Outlined.Assignment),
                        Triple(Screen.PcrList, "PCR", Icons.Outlined.Description),
                        Triple(Screen.GcsCalculator, "Calc", Icons.Outlined.Calculate),
                        Triple(Screen.Mnemonics, "Mnemonics", Icons.Outlined.MenuBook)
                    )
                    items.forEach { (screen, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Welcome.route,
                exitTransition = { fadeOut(tween(600)) }
            ) {
                WelcomeScreen(
                    onFinished = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAssessment = { navController.navigate(Screen.Assessment.route) },
                    onNavigateToPcrList = { navController.navigate(Screen.PcrList.route) },
                    onNavigateToGcs = { navController.navigate(Screen.GcsCalculator.route) },
                    onNavigateToVitals = { navController.navigate(Screen.VitalsCalculator.route) },
                    onNavigateToMnemonics = { navController.navigate(Screen.Mnemonics.route) }
                )
            }
            composable(Screen.Assessment.route) {
                AssessmentScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.PcrList.route) {
                PcrListScreen(
                    onNavigateToPcr = { pcrId ->
                        navController.navigate(Screen.PcrDetail.createRoute(pcrId))
                    },
                    onCreateNewPcr = {
                        navController.navigate(Screen.PcrDetail.createRoute("new"))
                    }
                )
            }
            composable(
                route = Screen.PcrDetail.route,
                arguments = listOf(navArgument(Screen.PcrDetail.ARG_PCR_ID) { type = NavType.StringType })
            ) {
                PcrDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.GcsCalculator.route) {
                GcsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToVitals = { navController.navigate(Screen.VitalsCalculator.route) }
                )
            }
            composable(Screen.VitalsCalculator.route) {
                VitalsCalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Mnemonics.route) {
                MnemonicsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
