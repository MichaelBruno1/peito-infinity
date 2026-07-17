package com.example.peitoinfinity.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.domain.model.WeeklyReport
import com.example.peitoinfinity.domain.usecase.GetWeeklyReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

data class ReportUiState(
    val report: WeeklyReport? = null,
    val isLoading: Boolean = false,
    val currentWeekStart: LocalDate = LocalDate.now(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getWeeklyReportUseCase: GetWeeklyReportUseCase
) : ViewModel() {

    private val _currentWeekStart = MutableStateFlow(getStartOfWeek(LocalDate.now()))

    val uiState: StateFlow<ReportUiState> = _currentWeekStart.flatMapLatest { weekStart ->
        flow {
            emit(ReportUiState(isLoading = true, currentWeekStart = weekStart))
            try {
                val report = getWeeklyReportUseCase(weekStart)
                emit(ReportUiState(report = report, isLoading = false, currentWeekStart = weekStart))
            } catch (e: Exception) {
                emit(ReportUiState(error = e.message, isLoading = false, currentWeekStart = weekStart))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportUiState(currentWeekStart = getStartOfWeek(LocalDate.now()))
    )

    fun selectPreviousWeek() {
        _currentWeekStart.value = _currentWeekStart.value.minusWeeks(1)
    }

    fun selectNextWeek() {
        val next = _currentWeekStart.value.plusWeeks(1)
        if (!next.isAfter(getStartOfWeek(LocalDate.now()))) {
            _currentWeekStart.value = next
        }
    }

    private fun getStartOfWeek(date: LocalDate): LocalDate {
        var d = date
        while (d.dayOfWeek != DayOfWeek.MONDAY) {
            d = d.minusDays(1)
        }
        return d
    }
}
