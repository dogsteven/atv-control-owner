package com.anhcop.atvcontrol_owner.screens.analytic.tabs.detailed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun DetailedAnalyticTab(
    viewModel: DetailedAnalyticViewModel,
    navController: NavHostController
) {
    val status = viewModel.status
    val isFetching = viewModel.isFetching

    val numberOfDays = rememberSaveable { YearMonth.now().lengthOfMonth() }

    var selectedTabIndex by rememberSaveable {  mutableIntStateOf(0) }

    val analyticDocumentEntries = remember(selectedTabIndex, isFetching) {
        if (selectedTabIndex == 0) {
            viewModel.todayAnalyticDocumentEntries
        } else {
            viewModel.thisMonthAnalyticDocumentEntries
        }
    }

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
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(
                    analyticDocumentEntries,
                    { analyticEntry -> analyticEntry.vehicleId }) { analyticEntry ->
                    AnalyticEntryCard(analyticEntry, selectedTabIndex, numberOfDays)
                }
            }
        }
    }
}

@Composable
private fun AnalyticEntryCard(
    analyticDocumentEntry: AnalyticDocumentEntry,
    selectedTabIndex: Int,
    numberOfDays: Int,
) {
    val document = analyticDocumentEntry.document

    var isExpanded by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
    ) {
        Column{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp, 64.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = analyticDocumentEntry.vehicleName.first().uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Column(
                            modifier = Modifier.padding(start = 12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = analyticDocumentEntry.vehicleName,
                                style = MaterialTheme.typography.titleLarge,
                            )

                            AnimatedVisibility(
                                visible = !isExpanded
                            ) {
                                Text(
                                    text = "Tổng doanh thu: ${document.revenue} VND",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                Icon(
                    painter = painterResource(if (!isExpanded) {
                        R.drawable.rounded_chevron_forward_24
                    } else {
                        R.drawable.rounded_keyboard_arrow_down_24
                    }),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 8.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)

                    TextField(
                        value = "${document.numberOfOneTicketSessions}",
                        label = { Text("Tổng số phiên chơi 1 vé") },
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = "${document.numberOfTwoTicketSessions}",
                        label = { Text("Tổng số phiên chơi 2 vé") },
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val (hours, minutes, seconds) = rememberSaveable(document.uptime) {
                        var seconds = document.uptime
                        val hours = seconds / 3600L
                        seconds %= 3600L
                        val minutes = seconds / 60L
                        seconds %= 60L

                        Triple(hours, minutes, seconds)
                    }

                    TextField(
                        value = "$hours giờ $minutes phút $seconds giây",
                        label = { Text("Tổng thời gian hoạt động") },
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = "${document.revenue} VND",
                        label = { Text("Tổng doanh thu") },
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    ) {
                        val lineColor = MaterialTheme.colorScheme.onPrimary

                        Text(
                            text = "Dữ liệu hoạt động",
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
                                        values = document.activities,
                                        color = Brush.radialGradient(0.0f to lineColor, 1.0f to lineColor)
                                    )
                                )
                            },
                            minValue = 0.0,
                            maxValue = rememberSaveable(selectedTabIndex) { (document.activities.maxOrNull() ?: 0.0) + 1.0 },
                            labelHelperProperties = LabelHelperProperties(enabled = false),
                            indicatorProperties = HorizontalIndicatorProperties(enabled = false),
                            gridProperties = GridProperties(
                                yAxisProperties = GridProperties.AxisProperties(
                                    lineCount = if (selectedTabIndex == 0) { 19 } else { numberOfDays },
                                ),
                            ),
                            modifier = Modifier.fillMaxWidth().height(128.dp).padding(bottom = 16.dp).padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}