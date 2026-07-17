package com.example.peitoinfinity.domain.repository

import com.example.peitoinfinity.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun getProfileSync(): UserProfile?
    suspend fun saveProfile(profile: UserProfile)
    suspend fun hasProfile(): Boolean
}
