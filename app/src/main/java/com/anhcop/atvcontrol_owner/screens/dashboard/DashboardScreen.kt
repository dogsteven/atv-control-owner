package com.anhcop.atvcontrol_owner.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anhcop.atvcontrol_owner.AnalyticRoute
import com.anhcop.atvcontrol_owner.EmployeeEditorRoute
import com.anhcop.atvcontrol_owner.R
import com.anhcop.atvcontrol_owner.VehicleEditorRoute
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.configuration.ConfigurationTab
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.configuration.ConfigurationViewModel
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.employee_list.EmployeeListTab
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.employee_list.EmployeeListViewModel
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.vehicle_list.VehicleListTab
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.vehicle_list.VehicleListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    vehicleListViewModel: VehicleListViewModel,
    employeeListViewModel: EmployeeListViewModel,
    configurationViewModel: ConfigurationViewModel,
    navController: NavHostController
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Bảng điều khiển")
                },
                actions = {
                    IconButton(
                        onClick = {
                            vehicleListViewModel.loadVehicles()
                            employeeListViewModel.loadEmployees()
                            configurationViewModel.loadConfiguration()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_refresh_24),
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
        },
        floatingActionButton = {
            when (selectedTabIndex) {
                0 -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(VehicleEditorRoute(id = null))
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_add_24),
                            contentDescription = null
                        )
                    }
                }
                1 -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(EmployeeEditorRoute(id = null))
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_add_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { innerPaddings ->

        Column(
            modifier = Modifier.fillMaxWidth().padding(innerPaddings)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    text = { Text("Xe") },
                    onClick = { selectedTabIndex = 0 }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    text = { Text("Nhân viên") },
                    onClick = { selectedTabIndex = 1 }
                )

                Tab(
                    selected = selectedTabIndex == 2,
                    text = { Text("Thiết lập") },
                    onClick = { selectedTabIndex = 2 }
                )
            }

            when (selectedTabIndex) {
                0 -> VehicleListTab(vehicleListViewModel, navController)
                1 -> EmployeeListTab(employeeListViewModel, navController)
                2 -> ConfigurationTab(configurationViewModel, navController)
            }
        }
    }
}