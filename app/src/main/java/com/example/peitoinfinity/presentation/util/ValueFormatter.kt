package com.example.peitoinfinity.presentation.util

object ValueFormatter {
    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 -> "${hours}h ${mins}min"
            else -> "${mins}min"
        }
    }

    fun formatWeight(kg: Float): String {
        return when {
            kg >= 1000 -> String.format("%.1f ton", kg / 1000f)
            kg >= 1 -> String.format("%.0f kg", kg)
            else -> String.format("%.1f kg", kg)
        }
    }

    fun formatDistance(km: Float): String {
        return when {
            km >= 1 -> String.format("%.1f km", km)
            else -> String.format("%.0f m", km * 1000)
        }
    }

    fun formatSpeed(kmh: Float): String = String.format("%.1f km/h", kmh)

    fun formatRest(seconds: Int): String {
        return when {
            seconds >= 60 -> "${seconds / 60}min ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }

    fun formatTimer(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }
}
