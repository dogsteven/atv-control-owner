package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anhcop.atvcontrol_owner.R

@Composable
fun ConfigurationTab(
    viewModel: ConfigurationViewModel,
    navController: NavHostController
) {
    val status = viewModel.status
    val isLoading = viewModel.isLoading

    val sessionDuration = viewModel.sessionDuration
    val sessionIsAboutToEndAlertDuration = viewModel.sessionIsAboutToEndAlertDuration

    val sessionDurationInput = viewModel.sessionDurationInput
    val isEditingSessionDuration = viewModel.isEditingSessionDuration
    val isSessionDurationInputValid = viewModel.isSessionDurationInputValid
    val isSubmittingUpdateSessionDuration = viewModel.isSubmittingUpdateSessionDuration

    val sessionIsAboutToEndAlertDurationInput = viewModel.sessionIsAboutToEndAlertDurationInput
    val isEditingSessionIsAboutToEndAlertDuration = viewModel.isEditingSessionIsAboutToEndAlertDuration
    val isSessionIsAboutToEndAlertDurationInputValid = viewModel.isSessionIsAboutToEndAlertDurationInputValid
    val isSubmittingUpdateSessionIsAboutToEndAlertDuration = viewModel.isSubmittingUpdateSessionIsAboutToEndAlertDuration

    AnimatedVisibility(
        visible = status == Status.Uninitialized || isLoading,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }

    AnimatedVisibility(
        visible = !isLoading && status == Status.Failed,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Đã xảy ra lỗi nào đó",
                textAlign = TextAlign.Center
            )
        }
    }

    AnimatedVisibility(
        visible = !isLoading && status == Status.Successful,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                TextField(
                    value = if (!isEditingSessionDuration) sessionDuration.toString() else sessionDurationInput,
                    label = { Text("Thời gian chơi một vé") },
                    onValueChange = { viewModel.sessionDurationInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = !isEditingSessionDuration,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (!isEditingSessionDuration) {
                                    viewModel.editSessionDuration()
                                } else {
                                    viewModel.submitUpdateSessionDuration()
                                }
                            },
                            enabled = !isEditingSessionDuration || !isSubmittingUpdateSessionDuration
                        ) {
                            if (!isEditingSessionDuration) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_edit_24),
                                    contentDescription = null
                                )
                            } else {
                                if (!isSubmittingUpdateSessionDuration) {
                                    Icon(
                                        painter = painterResource(R.drawable.round_done_24),
                                        contentDescription = null
                                    )
                                } else {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    },
                    isError = isEditingSessionDuration && !isSessionDurationInputValid,
                    supportingText = if (isEditingSessionDuration && !isSessionDurationInputValid) {
                        {
                            Text(
                                text = "Thời gian chơi một vé phải là một số dương",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        null
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TextField(
                    value = if (!isEditingSessionIsAboutToEndAlertDuration) sessionIsAboutToEndAlertDuration.toString() else sessionIsAboutToEndAlertDurationInput,
                    label = { Text("Thời gian cảnh báo") },
                    onValueChange = { viewModel.sessionIsAboutToEndAlertDurationInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = !isEditingSessionIsAboutToEndAlertDuration,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (!isEditingSessionIsAboutToEndAlertDuration) {
                                    viewModel.editSessionIsAboutToEndAlertDuration()
                                } else {
                                    viewModel.submitUpdateSessionIsAboutToEndAlertDuration()
                                }
                            },
                            enabled = !isEditingSessionIsAboutToEndAlertDuration || !isSubmittingUpdateSessionIsAboutToEndAlertDuration
                        ) {
                            if (!isEditingSessionIsAboutToEndAlertDuration) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_edit_24),
                                    contentDescription = null
                                )
                            } else {
                                if (!isSubmittingUpdateSessionIsAboutToEndAlertDuration) {
                                    Icon(
                                        painter = painterResource(R.drawable.round_done_24),
                                        contentDescription = null
                                    )
                                } else {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    },
                    isError = isEditingSessionIsAboutToEndAlertDuration && !isSessionIsAboutToEndAlertDurationInputValid,
                    supportingText = if (isEditingSessionIsAboutToEndAlertDuration && !isSessionIsAboutToEndAlertDurationInputValid) {
                        {
                            Text(
                                text = "Thời gian cảnh báo phải là một số dương",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        null
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}