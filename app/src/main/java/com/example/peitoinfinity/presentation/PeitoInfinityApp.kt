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
                NavigationBar {
                    bottomBarScreens.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(text = item.label)
                            }
                        )
                    }
                }
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

data class BottomBarItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)
