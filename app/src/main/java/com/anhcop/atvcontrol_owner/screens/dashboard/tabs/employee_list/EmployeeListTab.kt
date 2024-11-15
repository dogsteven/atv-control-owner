package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.employee_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anhcop.atvcontrol_owner.EmployeeDetailRoute
import com.anhcop.atvcontrol_owner.R

@Composable
fun EmployeeListTab(
    viewModel: EmployeeListViewModel,
    navController: NavHostController
) {
    val employeeEntries = viewModel.employeeEntries
    val status = viewModel.status
    val isLoading = viewModel.isLoading

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
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(employeeEntries, key = { employeeEntry -> employeeEntry.id }) { employeeEntry ->
                EmployeeEntryCard(
                    employeeEntry = employeeEntry,
                    openEmployeeDetailScreen = {
                        navController.navigate(EmployeeDetailRoute(id = employeeEntry.id))
                    }
                )
            }
        }
    }
}

@Composable
private fun EmployeeEntryCard(
    employeeEntry: EmployeeEntry,
    openEmployeeDetailScreen: () -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 12.dp),
        onClick = openEmployeeDetailScreen
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp, 64.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = employeeEntry.firstname.first().uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${employeeEntry.lastname} ${employeeEntry.firstname}",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Text(
                        text = "Mã thiết bị: ${employeeEntry.deviceIdentifier}",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.rounded_chevron_forward_24),
                contentDescription = null
            )
        }
    }
}