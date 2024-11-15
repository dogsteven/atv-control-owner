package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.configuration_management.ConfigurationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val configurationService: ConfigurationService
): ViewModel() {
    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _isLoading = mutableStateOf(false)
    val isLoading by _isLoading

    private val _sessionDuration = mutableLongStateOf(20L)
    val sessionDuration by _sessionDuration

    private val _sessionIsAboutToEndAlertDuration = mutableLongStateOf(5L)
    val sessionIsAboutToEndAlertDuration by _sessionIsAboutToEndAlertDuration


    private val _isEditingSessionDuration = mutableStateOf(false)
    val isEditingSessionDuration by _isEditingSessionDuration

    private val _sessionDurationInput = mutableStateOf("")
    var sessionDurationInput by _sessionDurationInput

    private val _isSessionDurationInputValid = mutableStateOf(true)
    val isSessionDurationInputValid by _isSessionDurationInputValid

    private val _isSubmittingUpdateSessionDuration = mutableStateOf(false)
    val isSubmittingUpdateSessionDuration by _isSubmittingUpdateSessionDuration


    private val _isEditingSessionIsAboutToEndAlertDuration = mutableStateOf(false)
    val isEditingSessionIsAboutToEndAlertDuration by _isEditingSessionIsAboutToEndAlertDuration

    private val _sessionIsAboutToEndAlertDurationInput = mutableStateOf("")
    var sessionIsAboutToEndAlertDurationInput by _sessionIsAboutToEndAlertDurationInput

    private val _isSessionIsAboutToEndAlertDurationInputValid = mutableStateOf(true)
    val isSessionIsAboutToEndAlertDurationInputValid by _isSessionIsAboutToEndAlertDurationInputValid

    private val _isSubmittingUpdateSessionIsAboutToEndAlertDuration = mutableStateOf(false)
    val isSubmittingUpdateSessionIsAboutToEndAlertDuration by _isSubmittingUpdateSessionIsAboutToEndAlertDuration

    init {
        loadConfiguration()
    }

    fun loadConfiguration() {
        if (_isLoading.value) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                _sessionDuration.longValue = configurationService.getSessionDuration()
                _sessionIsAboutToEndAlertDuration.longValue = configurationService.getSessionIsAboutToEndAlertDuration()

                _sessionDurationInput.value = _sessionDuration.longValue.toString()
                _sessionIsAboutToEndAlertDurationInput.value = _sessionIsAboutToEndAlertDuration.longValue.toString()

                _status.value = Status.Successful
            } catch (_: Throwable) {
                _status.value = Status.Failed
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editSessionDuration() {
        if (_isEditingSessionDuration.value) {
            return
        }

        _isEditingSessionDuration.value = true
    }

    private fun validateSessionDurationInput() {
        _isSessionDurationInputValid.value = _sessionDurationInput.value.toLongOrNull()?.run { this > 0 } ?: false
    }

    fun submitUpdateSessionDuration() {
        if (!_isEditingSessionDuration.value) {
            return
        }

        if (_isSubmittingUpdateSessionDuration.value) {
            return
        }

        _isSubmittingUpdateSessionDuration.value = true

        validateSessionDurationInput()

        if (!_isSessionDurationInputValid.value) {
            _isSubmittingUpdateSessionDuration.value = false
            return
        }

        val sessionDuration = _sessionDurationInput.value.toLong()

        if (sessionDuration == _sessionDuration.longValue) {
            _isSubmittingUpdateSessionDuration.value = false
            _isEditingSessionDuration.value = false
            return
        }

        try {
            configurationService.updateSessionDuration(sessionDuration)

            _sessionDuration.longValue = sessionDuration

            _isSubmittingUpdateSessionDuration.value = false
            _isEditingSessionDuration.value = false
        } catch (_: Throwable) {
            _isSubmittingUpdateSessionDuration.value = false
        }
    }

    fun editSessionIsAboutToEndAlertDuration() {
        if (_isEditingSessionIsAboutToEndAlertDuration.value) {
            return
        }

        _isEditingSessionIsAboutToEndAlertDuration.value = true
    }

    private fun validateSessionIsAboutToEndAlertDurationInput() {
        _isSessionIsAboutToEndAlertDurationInputValid.value = _sessionIsAboutToEndAlertDurationInput.value.toLongOrNull()?.run { this > 0 } ?: false
    }

    fun submitUpdateSessionIsAboutToEndAlertDuration() {
        if (!_isEditingSessionIsAboutToEndAlertDuration.value) {
            return
        }

        if (_isSubmittingUpdateSessionIsAboutToEndAlertDuration.value) {
            return
        }

        _isSubmittingUpdateSessionIsAboutToEndAlertDuration.value = true

        validateSessionIsAboutToEndAlertDurationInput()

        if (!_isSessionIsAboutToEndAlertDurationInputValid.value) {
            _isSubmittingUpdateSessionIsAboutToEndAlertDuration.value = false
            return
        }

        val sessionIsAboutToEndAlertDuration = _sessionIsAboutToEndAlertDurationInput.value.toLong()

        if (sessionIsAboutToEndAlertDuration == _sessionIsAboutToEndAlertDuration.longValue) {
            _isSubmittingUpdateSessionIsAboutToEndAlertDuration.value = false
            _isEditingSessionIsAboutToEndAlertDuration.value = false
            return
        }

        try {
            configurationService.updateSessionIsAboutToEndAlertDuration(sessionIsAboutToEndAlertDuration)

            _sessionIsAboutToEndAlertDuration.longValue = sessionIsAboutToEndAlertDuration

            _isSubmittingUpdateSessionIsAboutToEndAlertDuration.value = false
            _isEditingSessionIsAboutToEndAlertDuration.value = false
        } catch (_: Throwable) {
            _isSubmittingUpdateSessionIsAboutToEndAlertDuration.value = false
        }
    }
}