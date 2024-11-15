package com.anhcop.atvcontrol_owner.screens.analytic.tabs.general

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.analytic.AnalyticDocument
import com.anhcop.analytic.AnalyticService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralAnalyticViewModel @Inject constructor(
    private val analyticService: AnalyticService
): ViewModel() {
    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _isFetching = mutableStateOf(false)
    val isFetching by _isFetching

    private val _todayAnalyticDocument = mutableStateOf(AnalyticDocument())
    val todayAnalyticDocument by _todayAnalyticDocument

    private val _thisMonthAnalyticDocument = mutableStateOf(AnalyticDocument())
    val thisMonthAnalyticDocument by _thisMonthAnalyticDocument

    init {
        fetchAnalytic()
    }

    fun fetchAnalytic() {
        if (_isFetching.value) {
            return
        }

        _isFetching.value = true

        viewModelScope.launch {
            try {
                listOf(
                    launch {
                        _todayAnalyticDocument.value = analyticService.makeGeneralAnalyticDocumentForToday()
                    },
                    launch {
                        _thisMonthAnalyticDocument.value = analyticService.makeGeneralAnalyticDocumentForTheCurrentMonth()
                    }
                ).joinAll()

                _status.value = Status.Successful
            } catch (_: Throwable) {
                _status.value = Status.Failed
            } finally {
                _isFetching.value = false
            }
        }
    }
}