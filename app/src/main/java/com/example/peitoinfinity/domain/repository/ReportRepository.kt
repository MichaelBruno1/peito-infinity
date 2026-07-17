package com.example.peitoinfinity.domain.repository

interface ReportRepository {
    suspend fun getTotalWeightForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getTotalDistanceForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getAverageSpeedForWeek(startMillis: Long, endMillis: Long): Float
    suspend fun getTotalCardioTimeForWeek(startMillis: Long, endMillis: Long): Int
}
