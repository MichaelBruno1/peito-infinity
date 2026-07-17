package com.example.peitoinfinity.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Plan : Screen("plan")
    object PlanDayDetail : Screen("plan_day_detail/{dayId}") {
        fun createRoute(dayId: Long) = "plan_day_detail/$dayId"
    }
    object Workout : Screen("workout/{sessionId}") {
        fun createRoute(sessionId: Long) = "workout/$sessionId"
    }
    object Exercises : Screen("exercises")
    object Report : Screen("report")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}
