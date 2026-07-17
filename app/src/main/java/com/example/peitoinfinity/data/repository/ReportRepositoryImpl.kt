package com.example.peitoinfinity.data.repository

import com.example.peitoinfinity.data.local.database.dao.ExerciseLogDao
import com.example.peitoinfinity.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val exerciseLogDao: ExerciseLogDao
) : ReportRepository {

    override suspend fun getTotalWeightForWeek(startMillis: Long, endMillis: Long): Float {
        return exerciseLogDao.getTotalWeightForWeek(startMillis, endMillis)
    }

    override suspend fun getTotalDistanceForWeek(startMillis: Long, endMillis: Long): Float {
        return exerciseLogDao.getTotalDistanceForWeek(startMillis, endMillis)
    }

    override suspend fun getAverageSpeedForWeek(startMillis: Long, endMillis: Long): Float {
        return exerciseLogDao.getAverageSpeedForWeek(startMillis, endMillis)
    }

    override suspend fun getTotalCardioTimeForWeek(startMillis: Long, endMillis: Long): Int {
        return exerciseLogDao.getTotalCardioTimeForWeek(startMillis, endMillis)
    }
}
