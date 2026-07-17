package com.example.peitoinfinity.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.peitoinfinity.presentation.navigation.NavGraph
import com.example.peitoinfinity.presentation.navigation.Screen
import com.example.peitoinfinity.presentation.components.PeitoBottomBar
import com.example.peitoinfinity.presentation.components.BottomBarItem

@Composable
fun PeitoInfinityApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Destinos principais que exibem a BottomBar
    val bottomBarScreens = listOf(
        BottomBarItem("Treino", Screen.Plan.route, Icons.Default.FitnessCenter),
        BottomBarItem("Exercícios", Screen.Exercises.route, Icons.AutoMirrored.Filled.List),
        BottomBarItem("Relatório", Screen.Report.route, Icons.Default.BarChart),
        BottomBarItem("Ajustes", Screen.Settings.route, Icons.Default.Settings)
    )

    // Decidir se exibe a BottomBar com base na rota ativa
    val showBottomBar = bottomBarScreens.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PeitoBottomBar(
                    items = bottomBarScreens,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
