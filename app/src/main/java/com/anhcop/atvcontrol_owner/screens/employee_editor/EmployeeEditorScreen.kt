package com.anhcop.atvcontrol_owner.screens.employee_editor

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anhcop.atvcontrol_owner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditorScreen(
    id: String?,
    navController: NavHostController
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: EmployeeEditorViewModelFactory ->
            factory.create(id)
        }
    )

    val status = viewModel.status
    val editorMode = viewModel.editorMode

    val firstname = viewModel.firstname
    val lastname = viewModel.lastname
    val deviceIdentifier = viewModel.deviceIdentifier

    val isFirstnameValid = viewModel.isFirstnameValid
    val isLastnameValid = viewModel.isLastnameValid
    val isDeviceIdentifierValid = viewModel.isDeviceIdentifierValid

    val isSubmitting = viewModel.isSubmitting
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (status == Status.Successful) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (editorMode) {
                                EditorMode.Add -> "Thêm nhân viên mới"
                                EditorMode.Update -> "Cập nhật nhân viên"
                            }
                        )
                    },
                    actions = {
                        if (editorMode == EditorMode.Update) {
                            IconButton(
                                onClick = viewModel::reverseChanges
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_autorenew_24),
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_navigate_before_24),
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                )
            }
        }
    ) { innerPaddings ->
        AnimatedVisibility(
            visible = status == Status.Uninitialized,
            modifier = Modifier.fillMaxSize().padding(innerPaddings)
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
            visible = status == Status.Failed,
            modifier = Modifier.fillMaxSize().padding(innerPaddings)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Đã có lỗi không xác định xảy ra",
                )
            }
        }

        AnimatedVisibility(
            visible = status == Status.Successful,
            modifier = Modifier.fillMaxSize().padding(innerPaddings)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    TextField(
                        value = lastname,
                        label = { Text("Họ và tên đệm") },
                        onValueChange = { viewModel.lastname = it },
                        singleLine = true,
                        minLines = 1,
                        maxLines = 1,
                        isError = !isLastnameValid,
                        supportingText = if (!isLastnameValid) {
                            {
                                Text(
                                    text = "Họ và tên đệm không được bỏ trống",
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
                        value = firstname,
                        label = { Text("Tên") },
                        onValueChange = { viewModel.firstname = it },
                        singleLine = true,
                        minLines = 1,
                        maxLines = 1,
                        isError = !isFirstnameValid,
                        supportingText = if (!isFirstnameValid) {
                            {
                                Text(
                                    text = "Tên không được bỏ trống",
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
                        value = deviceIdentifier,
                        label = { Text("Mã thiết bị") },
                        onValueChange = { viewModel.deviceIdentifier = it },
                        singleLine = true,
                        minLines = 1,
                        maxLines = 1,
                        isError = !isDeviceIdentifierValid,
                        supportingText = if (!isDeviceIdentifierValid) {
                            {
                                Text(
                                    text = "Mã thiết bị phải là một dãy hex",
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
                    Button(
                        onClick = {
                            viewModel.submit(
                                onAdded = {
                                    Toast.makeText(context, "Đã thêm nhân viên thành công", Toast.LENGTH_SHORT).show()
                                },
                                onUpdated = {
                                    Toast.makeText(context, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    Toast.makeText(context, "Đã xảy ra lỗi nào đó", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        enabled = !isSubmitting
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (editorMode) {
                                    EditorMode.Add -> "Thêm nhân viên mới"
                                    EditorMode.Update -> "Cập nhật nhân viên"
                                }
                            )

                            if (isSubmitting) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}