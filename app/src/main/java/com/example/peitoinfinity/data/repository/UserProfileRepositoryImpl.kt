package com.example.peitoinfinity.data.repository

import com.example.peitoinfinity.data.local.database.dao.UserProfileDao
import com.example.peitoinfinity.data.local.database.entity.UserProfileEntity
import com.example.peitoinfinity.domain.model.Gender
import com.example.peitoinfinity.domain.model.TrainingGoal
import com.example.peitoinfinity.domain.model.TrainingLevel
import com.example.peitoinfinity.domain.model.UserProfile
import com.example.peitoinfinity.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun getProfile(): Flow<UserProfile?> {
        return userProfileDao.getProfile().map { it?.toDomain() }
    }

    override suspend fun getProfileSync(): UserProfile? {
        return userProfileDao.getProfileSync()?.toDomain()
    }

    override suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.saveProfile(profile.toEntity())
    }

    override suspend fun hasProfile(): Boolean {
        return userProfileDao.getProfileSync() != null
    }
}

// Mappers
fun UserProfileEntity.toDomain() = UserProfile(
    gender = Gender.valueOf(gender),
    heightCm = heightCm,
    weightKg = weightKg,
    trainingDaysPerWeek = trainingDaysPerWeek,
    availableTimeMinutes = availableTimeMinutes,
    trainingLevel = TrainingLevel.valueOf(trainingLevel),
    trainingGoal = TrainingGoal.valueOf(trainingGoal),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = 1,
    gender = gender.name,
    heightCm = heightCm,
    weightKg = weightKg,
    trainingDaysPerWeek = trainingDaysPerWeek,
    availableTimeMinutes = availableTimeMinutes,
    trainingLevel = trainingLevel.name,
    trainingGoal = trainingGoal.name,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)
