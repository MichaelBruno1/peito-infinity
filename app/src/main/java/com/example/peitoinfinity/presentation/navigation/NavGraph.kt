package com.example.peitoinfinity.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.peitoinfinity.presentation.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = Screen.Onboarding.route,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Plan.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Plan.route,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            TrainingPlanScreen(
                onDayClick = { dayId ->
                    navController.navigate(Screen.PlanDayDetail.createRoute(dayId))
                },
                onResumeWorkout = { sessionId ->
                    navController.navigate(Screen.Workout.createRoute(sessionId))
                }
            )
        }

        composable(
            route = Screen.PlanDayDetail.route,
            arguments = listOf(navArgument("dayId") { type = NavType.LongType }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it }) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it }) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it }) + fadeOut(tween(300))
            }
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getLong("dayId") ?: 1L
            PlanDayDetailScreen(
                dayId = dayId,
                onBackClick = { navController.popBackStack() },
                onStartWorkout = { sessionId ->
                    navController.navigate(Screen.Workout.createRoute(sessionId))
                }
            )
        }

        composable(
            route = Screen.Workout.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
            enterTransition = {
                slideInVertically(animationSpec = tween(300), initialOffsetY = { it }) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutVertically(animationSpec = tween(300), targetOffsetY = { it }) + fadeOut(tween(300))
            },
            popExitTransition = {
                slideOutVertically(animationSpec = tween(300), targetOffsetY = { it }) + fadeOut(tween(300))
            }
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 1L
            WorkoutScreen(
                sessionId = sessionId,
                onFinishWorkout = {
                    navController.popBackStack(Screen.Plan.route, false)
                }
            )
        }

        composable(
            route = Screen.Exercises.route,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            ExerciseListScreen()
        }

        composable(
            route = Screen.Report.route,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            ReportScreen()
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            SettingsScreen(
                onEditProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(
            route = Screen.Profile.route,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it }) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it }) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it }) + fadeOut(tween(300))
            }
        ) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
