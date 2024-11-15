package com.anhcop.atvcontrol_owner.screens.employee_detail

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anhcop.atvcontrol_owner.EmployeeEditorRoute
import com.anhcop.atvcontrol_owner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    id: String,
    navController: NavHostController
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: EmployeeDetailViewModelFactory ->
            factory.create(id)
        }
    )

    val status = viewModel.status

    val firstname = viewModel.firstname
    val lastname = viewModel.lastname
    val deviceIdentifier = viewModel.deviceIdentifier

    var isDeletionConfirmationDialogOpen by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    if (isDeletionConfirmationDialogOpen) {
        BasicAlertDialog(
            onDismissRequest = {
                isDeletionConfirmationDialogOpen = false
            }
        ) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(6.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bạn có chắc chắn bạn muốn xóa nhân viên $lastname $firstname",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Row {
                        Button(
                            onClick = {
                                isDeletionConfirmationDialogOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text("Không")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                viewModel.deleteEmployee(
                                    onError = {
                                        Toast.makeText(context, "Đã xảy ra lỗi nào đó", Toast.LENGTH_SHORT).show()
                                    },
                                    onDeleted = {
                                        isDeletionConfirmationDialogOpen = false
                                        Toast.makeText(context, "Nhân viên $lastname $firstname đã được xóa", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Có")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (status == Status.Successful) {
                TopAppBar(
                    title = { Text("Thông tin chi tiết") },
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate(EmployeeEditorRoute(id = viewModel.id))
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_edit_24),
                                contentDescription = null
                            )
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
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$lastname $firstname",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {
                                isDeletionConfirmationDialogOpen = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_close_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    TextField(
                        value = lastname,
                        label = { Text("Họ và tên đệm") },
                        onValueChange = {},
                        readOnly = true,
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
                        onValueChange = {},
                        readOnly = true,
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
                        onValueChange = {},
                        readOnly = true,
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
}