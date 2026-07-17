package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.model.UserProfile
import com.example.peitoinfinity.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> = repository.getProfile()

    suspend fun hasProfile(): Boolean = repository.hasProfile()
}
