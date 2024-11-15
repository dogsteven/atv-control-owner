package com.anhcop.atvcontrol_owner.screens.analytic

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.anhcop.atvcontrol_owner.DashboardRoute
import com.anhcop.atvcontrol_owner.R
import com.anhcop.atvcontrol_owner.screens.analytic.tabs.detailed.DetailedAnalyticTab
import com.anhcop.atvcontrol_owner.screens.analytic.tabs.detailed.DetailedAnalyticViewModel
import com.anhcop.atvcontrol_owner.screens.analytic.tabs.general.GeneralAnalyticTab
import com.anhcop.atvcontrol_owner.screens.analytic.tabs.general.GeneralAnalyticViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticScreen(
    viewModel: AnalyticViewModel,
    generalAnalyticViewModel: GeneralAnalyticViewModel,
    detailedAnalyticViewModel: DetailedAnalyticViewModel,
    navController: NavHostController
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val isExportingReport = viewModel.isExportingReport

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }

        val stream = context.contentResolver.openOutputStream(uri)
            ?: return@rememberLauncherForActivityResult

        viewModel.writeAllHistories(
            stream = stream,
            onSuccess = {
                stream.close()

                scope.launch {
                    Toast.makeText(context, "Đã lưu báo cáo thành công", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = {
                stream.close()

                scope.launch {
                    Toast.makeText(context, "Đã xảy ra lỗi nào đó", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Thống kê") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(DashboardRoute)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_dashboard_24),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {
                            generalAnalyticViewModel.fetchAnalytic()
                            detailedAnalyticViewModel.fetchAnalytic()
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_refresh_24),
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
            if (selectedTabIndex == 0 && !isExportingReport) {
                FloatingActionButton(
                    onClick = {
                        launcher.launch("report.xlsx")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_download_24),
                        contentDescription = null
                    )
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
                    text = { Text("Tổng quan") },
                    onClick = { selectedTabIndex = 0 }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    text = { Text("Theo xe") },
                    onClick = { selectedTabIndex = 1 }
                )
            }

            when (selectedTabIndex) {
                0 -> GeneralAnalyticTab(generalAnalyticViewModel, navController)
                1 -> DetailedAnalyticTab(detailedAnalyticViewModel, navController)
            }
        }
    }
}