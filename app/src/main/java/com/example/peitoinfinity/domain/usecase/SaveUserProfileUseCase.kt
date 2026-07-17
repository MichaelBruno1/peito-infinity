package com.example.peitoinfinity.domain.usecase

import com.example.peitoinfinity.domain.model.UserProfile
import com.example.peitoinfinity.domain.repository.UserProfileRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile): Result<Unit> {
        return try {
            // Validações
            require(profile.heightCm in 100f..250f) { "Altura deve ser entre 100cm e 250cm" }
            require(profile.weightKg in 30f..300f) { "Peso deve ser entre 30kg e 300kg" }
            require(profile.trainingDaysPerWeek in 1..7) { "Frequência de treinos deve ser entre 1 e 7 dias" }
            require(profile.availableTimeMinutes in 20..180) { "Tempo disponível deve ser entre 20 e 180 minutos" }

            repository.saveProfile(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
