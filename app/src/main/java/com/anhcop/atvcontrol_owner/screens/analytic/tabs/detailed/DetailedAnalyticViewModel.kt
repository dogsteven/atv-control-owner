package com.anhcop.atvcontrol_owner.screens.analytic.tabs.detailed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.analytic.AnalyticService
import com.anhcop.vehicle_management.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private data class ComparablePair<U: Comparable<U>, V: Comparable<V>>(
    private val first: U,
    private val second: V
): Comparable<ComparablePair<U, V>> {
    override fun compareTo(other: ComparablePair<U, V>): Int {
        val firstCoordinate = first compareTo other.first

        if (firstCoordinate != 0) {
            return firstCoordinate
        }

        return second compareTo other.second
    }
}

@HiltViewModel
class DetailedAnalyticViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val analyticService: AnalyticService
): ViewModel() {
    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _isFetching = mutableStateOf(false)
    val isFetching by _isFetching

    private val _todayAnalyticDocumentEntries = mutableStateListOf<AnalyticDocumentEntry>()
    val todayAnalyticDocumentEntries: List<AnalyticDocumentEntry> = _todayAnalyticDocumentEntries

    private val _thisMonthAnalyticDocumentEntries = mutableStateListOf<AnalyticDocumentEntry>()
    val thisMonthAnalyticDocumentEntries: List<AnalyticDocumentEntry> = _thisMonthAnalyticDocumentEntries

    init {
        fetchAnalytic()
    }

    fun fetchAnalytic() {
        if (_isFetching.value) {
            return
        }

        _isFetching.value = true

        viewModelScope.launch {
            try {
                val vehicles = vehicleRepository.getAllVehicles()

                listOf(
                    launch {
                        _todayAnalyticDocumentEntries.clear()

                        val analyticDocumentEntries = withContext(Dispatchers.Default) {
                            vehicles.map { vehicle ->
                                async {
                                    val analyticDocument = analyticService.makeDetailedAnalyticDocumentForToday(
                                        vehicleId = vehicle.id
                                    )

                                    AnalyticDocumentEntry(
                                        vehicleId = vehicle.id,
                                        vehicleName = vehicle.name,
                                        document = analyticDocument
                                    )
                                }
                            }.awaitAll().sortedBy { ComparablePair(-it.document.revenue, it.vehicleName) }
                        }

                        _todayAnalyticDocumentEntries.addAll(analyticDocumentEntries)
                    },
                    launch {
                        _thisMonthAnalyticDocumentEntries.clear()

                        val analyticDocumentEntries = withContext(Dispatchers.Default) {
                            vehicles.map { vehicle ->
                                async {
                                    val analyticDocument = analyticService.makeDetailedAnalyticDocumentForTheCurrentMonth(
                                        vehicleId = vehicle.id
                                    )

                                    AnalyticDocumentEntry(
                                        vehicleId = vehicle.id,
                                        vehicleName = vehicle.name,
                                        document = analyticDocument
                                    )
                                }
                            }.awaitAll().sortedBy { ComparablePair(-it.document.revenue, it.vehicleName) }
                        }

                        _thisMonthAnalyticDocumentEntries.addAll(analyticDocumentEntries)
                    }
                ).joinAll()

                _status.value = Status.Successful
            } catch (_: Throwable) {
                _status.value = Status.Failed
            } finally {
                _isFetching.value = false
            }
        }
    }
}