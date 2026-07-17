package com.example.peitoinfinity.data.repository

import com.example.peitoinfinity.data.local.database.dao.ExerciseLogDao
import com.example.peitoinfinity.data.local.database.dao.WorkoutSessionDao
import com.example.peitoinfinity.data.local.database.entity.WorkoutSessionEntity
import com.example.peitoinfinity.domain.model.WorkoutSession
import com.example.peitoinfinity.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutSessionRepositoryImpl @Inject constructor(
    private val workoutSessionDao: WorkoutSessionDao,
    private val exerciseLogDao: ExerciseLogDao
) : WorkoutSessionRepository {

    override fun getActiveSession(): Flow<WorkoutSession?> {
        return workoutSessionDao.getActiveSession().flatMapLatest { sessionEntity ->
            if (sessionEntity == null) flowOf(null)
            else {
                exerciseLogDao.getLogsForSession(sessionEntity.id).map { logs ->
                    sessionEntity.toDomain(logs.map { it.toDomain() })
                }
            }
        }
    }

    override suspend fun getActiveSessionSync(): WorkoutSession? {
        val sessionEntity = workoutSessionDao.getActiveSessionSync() ?: return null
        val logs = exerciseLogDao.getLogsForSession(sessionEntity.id).first()
        return sessionEntity.toDomain(logs.map { it.toDomain() })
    }

    override fun getAllSessions(): Flow<List<WorkoutSession>> {
        return workoutSessionDao.getAllSessions().flatMapLatest { sessionEntities ->
            if (sessionEntities.isEmpty()) flowOf(emptyList())
            else {
                val sessionFlows = sessionEntities.map { sessionEntity ->
                    exerciseLogDao.getLogsForSession(sessionEntity.id).map { logs ->
                        sessionEntity.toDomain(logs.map { it.toDomain() })
                    }
                }
                combine(sessionFlows) { it.toList() }
            }
        }
    }

    override fun getSessionsForWeek(startMillis: Long, endMillis: Long): Flow<List<WorkoutSession>> {
        return workoutSessionDao.getSessionsForWeek(startMillis, endMillis).flatMapLatest { sessionEntities ->
            if (sessionEntities.isEmpty()) flowOf(emptyList())
            else {
                val sessionFlows = sessionEntities.map { sessionEntity ->
                    exerciseLogDao.getLogsForSession(sessionEntity.id).map { logs ->
                        sessionEntity.toDomain(logs.map { it.toDomain() })
                    }
                }
                combine(sessionFlows) { it.toList() }
            }
        }
    }

    override suspend fun startSession(planDayId: Long): Long {
        val entity = WorkoutSessionEntity(
            planDayId = planDayId,
            startedAt = System.currentTimeMillis(),
            finishedAt = null,
            notes = null
        )
        return workoutSessionDao.startSession(entity)
    }

    override suspend fun finishSession(sessionId: Long, notes: String?) {
        val entity = workoutSessionDao.getSessionById(sessionId)
            ?: throw Exception("Sessão não encontrada")
        val updated = entity.copy(
            finishedAt = System.currentTimeMillis(),
            notes = notes
        )
        workoutSessionDao.updateSession(updated)
    }

    override suspend fun getSessionById(sessionId: Long): WorkoutSession? {
        val entity = workoutSessionDao.getSessionById(sessionId) ?: return null
        val logs = exerciseLogDao.getLogsForSession(sessionId).first()
        return entity.toDomain(logs.map { it.toDomain() })
    }

    override suspend fun getSessionsForWeekSync(startMillis: Long, endMillis: Long): List<WorkoutSession> {
        val entities = workoutSessionDao.getSessionsForWeekSync(startMillis, endMillis)
        return entities.map { entity ->
            val logs = exerciseLogDao.getLogsForSession(entity.id).first()
            entity.toDomain(logs.map { it.toDomain() })
        }
    }
}

// Mappers
fun WorkoutSessionEntity.toDomain(logs: List<com.example.peitoinfinity.domain.model.ExerciseLog> = emptyList()) = WorkoutSession(
    id = id,
    planDayId = planDayId,
    startedAt = startedAt,
    finishedAt = finishedAt,
    notes = notes,
    logs = logs
)
