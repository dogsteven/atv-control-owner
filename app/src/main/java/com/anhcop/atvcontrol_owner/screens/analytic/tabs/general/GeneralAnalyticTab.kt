package com.anhcop.atvcontrol_owner.screens.analytic.tabs.general

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anhcop.atvcontrol_owner.R
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import java.time.YearMonth

@Composable
fun GeneralAnalyticTab(
    viewModel: GeneralAnalyticViewModel,
    navController: NavHostController
) {
    val status = viewModel.status
    val isFetching = viewModel.isFetching

    val numberOfDays = rememberSaveable { YearMonth.now().lengthOfMonth() }

    var selectedTabIndex by rememberSaveable {  mutableIntStateOf(0) }

    val analyticDocument = remember(selectedTabIndex, isFetching) {
        if (selectedTabIndex == 0) {
            viewModel.todayAnalyticDocument
        } else {
            viewModel.thisMonthAnalyticDocument
        }
    }

    val numberOfOneTicketSessions = analyticDocument.numberOfOneTicketSessions
    val numberOfTwoTicketSessions = analyticDocument.numberOfTwoTicketSessions
    val revenue = analyticDocument.revenue
    val hasAnomaly = analyticDocument.hasAnomaly

    val activities = analyticDocument.activities

    AnimatedVisibility(
        visible = isFetching || status == Status.Uninitialized,
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
        visible = !isFetching && status == Status.Failed,
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
        visible = !isFetching && status == Status.Successful,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            TabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    text = { Text("Hôm nay") },
                    onClick = { selectedTabIndex = 0 }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    text = { Text("Tháng này") },
                    onClick = { selectedTabIndex = 1 }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    TextField(
                        value = "$numberOfOneTicketSessions",
                        label = { Text("Tổng số phiên chơi 1 vé") },
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
                        value = "$numberOfTwoTicketSessions",
                        label = { Text("Tổng số phiên chơi 2 vé") },
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
                        value = "$revenue VND",
                        label = { Text("Tổng doanh thu") },
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
                        value = if (!hasAnomaly) "Không" else "Có",
                        label = { Text("Có bất thường trong dữ liệu hay không?") },
                        isError = hasAnomaly,
                        supportingText = if (!hasAnomaly) {
                            null
                        } else {
                            {
                                Text(
                                    text = "Có thể là lỗi kỹ thuât hoặc nhân viên chủ ý gian lận",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (!hasAnomaly) {
                                        R.drawable.round_done_24
                                    } else {
                                        R.drawable.rounded_close_24
                                    }
                                ),
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                    ) {
                        val lineColor = MaterialTheme.colorScheme.onSecondaryContainer

                        Text(
                            text = "Biểu đồ hoạt động",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LineChart(
                            data = remember(selectedTabIndex) {
                                listOf(
                                    Line(
                                        label = "",
                                        values = activities,
                                        color = Brush.radialGradient(
                                            0.0f to lineColor,
                                            1.0f to lineColor
                                        )
                                    )
                                )
                            },
                            minValue = 0.0,
                            maxValue = remember(selectedTabIndex) { (activities.maxOrNull() ?: 0.0) + 1.0 },
                            labelHelperProperties = LabelHelperProperties(enabled = false),
                            indicatorProperties = HorizontalIndicatorProperties(enabled = false),
                            gridProperties = GridProperties(
                                yAxisProperties = GridProperties.AxisProperties(
                                    lineCount = if (selectedTabIndex == 0) { 19 } else { numberOfDays }
                                )
                            ),
                            modifier = Modifier.fillMaxWidth().height(128.dp)
                                .padding(bottom = 16.dp).padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}