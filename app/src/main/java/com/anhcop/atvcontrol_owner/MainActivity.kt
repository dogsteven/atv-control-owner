package com.anhcop.atvcontrol_owner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.anhcop.atvcontrol_owner.screens.analytic.AnalyticScreen
import com.anhcop.atvcontrol_owner.screens.analytic.AnalyticViewModel
import com.anhcop.atvcontrol_owner.screens.analytic.tabs.detailed.DetailedAnalyticViewModel
import com.anhcop.atvcontrol_owner.screens.analytic.tabs.general.GeneralAnalyticViewModel
import com.anhcop.atvcontrol_owner.screens.dashboard.DashboardScreen
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.configuration.ConfigurationViewModel
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.employee_list.EmployeeListViewModel
import com.anhcop.atvcontrol_owner.screens.dashboard.tabs.vehicle_list.VehicleListViewModel
import com.anhcop.atvcontrol_owner.screens.employee_detail.EmployeeDetailScreen
import com.anhcop.atvcontrol_owner.screens.employee_editor.EmployeeEditorScreen
import com.anhcop.atvcontrol_owner.screens.vehicle_detail.VehicleDetailScreen
import com.anhcop.atvcontrol_owner.screens.vehicle_editor.VehicleEditorScreen
import com.anhcop.atvcontrol_owner.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vehicleListViewModel by viewModels<VehicleListViewModel>()
    private val employeeListViewModel by viewModels<EmployeeListViewModel>()
    private val configurationViewModel by viewModels<ConfigurationViewModel>()

    private val analyticViewModel by viewModels<AnalyticViewModel>()
    private val generalAnalyticViewModel by viewModels<GeneralAnalyticViewModel>()
    private val detailedAnalyticViewModel by viewModels<DetailedAnalyticViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            AppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    NavHost(navController, startDestination = AnalyticRoute) {
                        composable<AnalyticRoute> {
                            AnalyticScreen(
                                viewModel = analyticViewModel,
                                generalAnalyticViewModel = generalAnalyticViewModel,
                                detailedAnalyticViewModel = detailedAnalyticViewModel,
                                navController = navController
                            )
                        }

                        composable<DashboardRoute> {
                            DashboardScreen(
                                vehicleListViewModel = vehicleListViewModel,
                                employeeListViewModel = employeeListViewModel,
                                configurationViewModel = configurationViewModel,
                                navController = navController
                            )
                        }

                        composable<VehicleDetailRoute> { backStackEntry ->
                            val (id) = backStackEntry.toRoute<VehicleDetailRoute>()

                            VehicleDetailScreen(
                                id = id,
                                navController = navController
                            )
                        }

                        composable<VehicleEditorRoute> { backStackEntry ->
                            val (id) = backStackEntry.toRoute<VehicleEditorRoute>()

                            VehicleEditorScreen(
                                id = id,
                                navController = navController
                            )
                        }

                        composable<EmployeeDetailRoute> { backStackEntry ->
                            val (id) = backStackEntry.toRoute<EmployeeDetailRoute>()

                            EmployeeDetailScreen(
                                id = id,
                                navController = navController
                            )
                        }

                        composable<EmployeeEditorRoute> { backStackEntry ->
                            val (id) = backStackEntry.toRoute<EmployeeEditorRoute>()

                            EmployeeEditorScreen(
                                id = id,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}