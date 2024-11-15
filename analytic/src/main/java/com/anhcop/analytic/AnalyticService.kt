package com.anhcop.analytic

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar

class AnalyticService(
    firestoreFactory: () -> FirebaseFirestore
) {
    private val sessionHistoryRepository = SessionHistoryRepository(firestoreFactory)

    private fun countSessions(sessionHistories: List<SessionHistory>): Pair<Int, Int> {
        val numberOfOneTicketSessions = sessionHistories.count { it.numberOfTickets == 1L }
        val numberOfTwoTicketSessions = sessionHistories.size - numberOfOneTicketSessions

        return numberOfOneTicketSessions to numberOfTwoTicketSessions
    }

    private fun calculateRevenueAndUptime(sessionHistories: List<SessionHistory>): Pair<Long, Long> {
        return sessionHistories.fold(0L to 0L) { (revenue, uptime), sessionHistory ->
            val newRevenue = revenue + sessionHistory.price * sessionHistory.numberOfTickets
            val newUptime = uptime + sessionHistory.sessionDuration * sessionHistory.numberOfTickets

            newRevenue to newUptime
        }
    }

    private fun checkAnomaly(sessionHistories: List<SessionHistory>): Boolean {
        for (i in 1..<sessionHistories.size) {
            val prev = sessionHistories[i - 1]
            val curr = sessionHistories[i]

            val prevInstant = prev.timestamp.toInstant()
            val minimumCurrInstant =
                prevInstant.plusSeconds(prev.numberOfTickets * prev.sessionDuration)
            val currInstant = curr.timestamp.toInstant()

            if (currInstant < minimumCurrInstant) {
                return true
            }
        }

        return false
    }

    private suspend fun buildMonthlyAnalyticDocument(sessionHistories: List<SessionHistory>, numberOfDays: Int): AnalyticDocument = coroutineScope {
        val sessionCountingTask = async {
            countSessions(sessionHistories)
        }

        val revenueAndUptimeCalculationTask = async {
            withContext(Dispatchers.IO) {
                calculateRevenueAndUptime(sessionHistories)
            }
        }

        val anomalyDetectionTask = async {
            withContext(Dispatchers.Default) {
                checkAnomaly(sessionHistories)
            }
        }

        val activitiesCalculationTask = async {
            withContext(Dispatchers.Default) {
                mutableListOf<Double>().apply {
                    for (i in 0..<numberOfDays) {
                        add(0.0)
                    }

                    for (sessionHistory in sessionHistories) {
                        val instant = sessionHistory.timestamp.toInstant()
                        val dayOfMonth = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).dayOfMonth

                        this[dayOfMonth - 1] += sessionHistory.numberOfTickets.toDouble()
                    }
                }
            }
        }

        val (numberOfOneTicketSessions, numberOfTwoTicketSessions) = sessionCountingTask.await()
        val (revenue, uptime) = revenueAndUptimeCalculationTask.await()
        val hasAnomaly = anomalyDetectionTask.await()
        val activities = activitiesCalculationTask.await()

        AnalyticDocument(numberOfOneTicketSessions, numberOfTwoTicketSessions, revenue, hasAnomaly, uptime, activities)
    }

    private suspend fun buildDailyAnalyticDocument(sessionHistories: List<SessionHistory>): AnalyticDocument = coroutineScope {
        val sessionCountingTask = async {
            countSessions(sessionHistories)
        }

        val revenueAndUptimeCalculationTask = async {
            withContext(Dispatchers.Default) {
                calculateRevenueAndUptime(sessionHistories)
            }
        }

        val anomalyDetectionTask = async {
            withContext(Dispatchers.Default) {
                checkAnomaly(sessionHistories)
            }
        }

        val activitiesCalculationTask = async {
            withContext(Dispatchers.Default) {
                mutableListOf<Double>().apply {
                    for (i in 6..24) {
                        add(0.0)
                    }

                    for (sessionHistory in sessionHistories) {
                        val instant = sessionHistory.timestamp.toInstant()
                        val hour = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                            .toLocalTime().hour

                        if (hour >= 6) {
                            this[hour - 6] += sessionHistory.numberOfTickets.toDouble()
                        } else {
                            this[0] += sessionHistory.numberOfTickets.toDouble()
                        }
                    }
                }
            }
        }

        val (numberOfOneTicketSessions, numberOfTwoTicketSessions) = sessionCountingTask.await()
        val (revenue, uptime) = revenueAndUptimeCalculationTask.await()
        val hasAnomaly = anomalyDetectionTask.await()
        val activities = activitiesCalculationTask.await()

        AnalyticDocument(numberOfOneTicketSessions, numberOfTwoTicketSessions, revenue, hasAnomaly, uptime, activities)
    }

    suspend fun makeGeneralAnalyticDocumentForTheCurrentMonth(): AnalyticDocument {
        val numberOfDays = YearMonth.now().lengthOfMonth()

        val theFirstDateOfMonth = Timestamp(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.toInstant())

        val theLastDateOfMonth = Timestamp(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, numberOfDays)
            set(Calendar.HOUR, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.toInstant())

        val sessionHistories = sessionHistoryRepository.getGeneralSessionHistoriesBetween(
            startTimestamp = theFirstDateOfMonth,
            endTimestamp = theLastDateOfMonth
        )

        return buildMonthlyAnalyticDocument(sessionHistories, numberOfDays)
    }

    suspend fun makeDetailedAnalyticDocumentForTheCurrentMonth(vehicleId: String): AnalyticDocument {
        val numberOfDays = YearMonth.now().lengthOfMonth()

        val theFirstDateOfMonth = Timestamp(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.toInstant())

        val theLastDateOfMonth = Timestamp(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, numberOfDays)
            set(Calendar.HOUR, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.toInstant())

        val sessionHistories = sessionHistoryRepository.getDetailedSessionHistoriesBetween(
            vehicleId = vehicleId,
            startTimestamp = theFirstDateOfMonth,
            endTimestamp = theLastDateOfMonth
        )

        return buildMonthlyAnalyticDocument(sessionHistories, numberOfDays)
    }

    suspend fun makeGeneralAnalyticDocumentForToday(): AnalyticDocument {
        val startTimestamp = Timestamp(LocalDate.now().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant())
        val endTimestamp = Timestamp(LocalDate.now().atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault()).toInstant())

        val sessionHistories = sessionHistoryRepository.getGeneralSessionHistoriesBetween(startTimestamp, endTimestamp)

        return buildDailyAnalyticDocument(sessionHistories)
    }

    suspend fun makeDetailedAnalyticDocumentForToday(vehicleId: String): AnalyticDocument {
        val startTimestamp = Timestamp(
            LocalDate.now().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant())
        val endTimestamp = Timestamp(
            LocalDate.now().atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault()).toInstant())

        val sessionHistories = sessionHistoryRepository.getDetailedSessionHistoriesBetween(vehicleId, startTimestamp, endTimestamp)

        return buildDailyAnalyticDocument(sessionHistories)
    }

    suspend fun writeAllHistories(stream: OutputStream) {
        val sessionHistories = sessionHistoryRepository.getAllGeneralSessionHistories()

        val workbook = XSSFWorkbook()

        val timestampCellStyle = workbook.createCellStyle()

        val creationHelper = workbook.creationHelper
        val timestampFormat = creationHelper.createDataFormat().getFormat("d/m/yy h:mm:ss")
        timestampCellStyle.dataFormat = timestampFormat

        val worksheet = workbook.createSheet("Report")

        worksheet.createRow(0).apply {
            createCell(0).apply {
                setCellValue("Nhân viên")
            }

            createCell(1).apply {
                setCellValue("Ngày và giờ")
            }

            createCell(2).apply {
                setCellValue("Giá vé")
            }

            createCell(3).apply {
                setCellValue("Số lượng vé")
            }

            createCell(4).apply {
                setCellValue("Thời lượng chơi mỗi vé")
            }
        }

        for ((index, sessionHistory) in sessionHistories.withIndex()) {
            worksheet.createRow(index + 1).apply {
                createCell(0).apply {
                    setCellValue(sessionHistory.employeeName)
                }

                createCell(1).apply {
                    cellStyle = timestampCellStyle
                    setCellValue(LocalDateTime.ofInstant(sessionHistory.timestamp.toInstant(), ZoneId.systemDefault()))
                }

                createCell(2).apply {
                    setCellValue(sessionHistory.price.toDouble())
                }

                createCell(3).apply {
                    setCellValue(sessionHistory.numberOfTickets.toDouble())
                }

                createCell(4).apply {
                    setCellValue(sessionHistory.sessionDuration.toDouble())
                }
            }
        }

        workbook.write(stream)

        Log.d("anhcop", "Here")
    }
}