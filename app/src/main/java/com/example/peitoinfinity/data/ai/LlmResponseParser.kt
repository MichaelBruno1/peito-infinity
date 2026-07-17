package com.example.peitoinfinity.data.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class LlmPlanResponse(
    val planName: String,
    val days: List<LlmDayResponse>
)

@Serializable
data class LlmDayResponse(
    val dayNumber: Int? = null,
    val dayName: String,
    val focusMuscleGroups: List<String>,
    val estimatedDurationMinutes: Int,
    val exercises: List<LlmExerciseResponse>
)

@Serializable
data class LlmExerciseResponse(
    val exerciseId: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String? = null
)

class LlmResponseParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Extrai JSON da resposta da LLM, removendo markdown e texto extra.
     */
    fun extractJson(rawResponse: String): String {
        val codeBlockRegex = """```(?:json)?\s*\n?([\s\S]*?)\n?```""".toRegex()
        val codeBlockMatch = codeBlockRegex.find(rawResponse)
        if (codeBlockMatch != null) {
            return codeBlockMatch.groupValues[1].trim()
        }

        val jsonRegex = """\{[\s\S]*\}""".toRegex()
        val jsonMatch = jsonRegex.find(rawResponse)
        if (jsonMatch != null) {
            return jsonMatch.value.trim()
        }

        return rawResponse.trim()
    }

    fun parseFullPlan(rawResponse: String): Result<LlmPlanResponse> {
        return try {
            val jsonStr = extractJson(rawResponse)
            val plan = json.decodeFromString<LlmPlanResponse>(jsonStr)
            Result.success(plan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun parseDayPlan(rawResponse: String): Result<LlmDayResponse> {
        return try {
            val jsonStr = extractJson(rawResponse)
            val day = json.decodeFromString<LlmDayResponse>(jsonStr)
            Result.success(day)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
