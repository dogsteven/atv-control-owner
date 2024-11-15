package com.anhcop.atvcontrol_owner.screens.analytic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.analytic.AnalyticService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class AnalyticViewModel @Inject constructor(
    private val analyticService: AnalyticService
): ViewModel() {
    private val _isExportingReport = mutableStateOf(false)
    val isExportingReport by _isExportingReport

    fun writeAllHistories(stream: OutputStream, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (_isExportingReport.value) {
            return
        }

        _isExportingReport.value = true

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    analyticService.writeAllHistories(stream)
                }

                onSuccess()
            } catch (_: Throwable) {
                onFailure()
            } finally {
                _isExportingReport.value = false
            }
        }
    }
}