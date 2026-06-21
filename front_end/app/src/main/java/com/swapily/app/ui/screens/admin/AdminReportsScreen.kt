package com.swapily.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swapily.app.data.model.Report
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.AdminReportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    viewModel: AdminReportsViewModel
) {
    val reports by viewModel.reports.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()

    var showActionDialog by remember { mutableStateOf<Report?>(null) }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) viewModel.resetActionSuccess()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reports Management",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            val pendingCount = reports.count { it.status == "PENDING" }
            Text(
                text = "${reports.size} reports ($pendingCount pending)",
                fontSize = 13.sp,
                color = GrayText
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (reports.isEmpty() && !loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReportProblem, null, tint = GrayText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No reports found", color = GrayText)
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(reports, key = { it.id }) { report ->
                    ReportCard(
                        report = report,
                        onAction = { showActionDialog = report }
                    )
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }

    // Action Dialog
    showActionDialog?.let { report ->
        AlertDialog(
            onDismissRequest = { showActionDialog = null },
            containerColor = White,
            title = { Text("Report Actions", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Reporter: ${report.reporterName}", fontSize = 13.sp, color = TextDark)
                    Text("Target: ${report.targetType} (${report.targetId})", fontSize = 13.sp, color = TextDark)
                    Text("Reason: ${report.reason}", fontSize = 13.sp, color = TextDark)
                    Text("Status: ${report.status}", fontSize = 13.sp, color = TextDark)
                }
            },
            confirmButton = {
                Column {
                    if (report.status == "PENDING") {
                        Button(
                            onClick = {
                                viewModel.resolveReport(report.id)
                                showActionDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Resolve")
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        if (report.targetType == "PRODUCT") {
                            Button(
                                onClick = {
                                    viewModel.deleteReportedProduct(report.targetId)
                                    viewModel.resolveReport(report.id)
                                    showActionDialog = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Delete Product & Resolve")
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (report.targetType == "USER") {
                            Button(
                                onClick = {
                                    viewModel.blockReportedUser(report.targetId)
                                    viewModel.resolveReport(report.id)
                                    showActionDialog = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Block User & Resolve")
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.dismissReport(report.id)
                                showActionDialog = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Dismiss")
                        }
                    } else {
                        TextButton(onClick = { showActionDialog = null }) {
                            Text("Close", color = GreenPrimary)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ReportCard(
    report: Report,
    onAction: () -> Unit
) {
    val statusColor = when (report.status) {
        "PENDING" -> Color(0xFFFF9800)
        "RESOLVED" -> Color(0xFF4CAF50)
        "DISMISSED" -> Color(0xFF9E9E9E)
        else -> GrayText
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(
                    text = report.status.lowercase().replaceFirstChar { it.uppercase() },
                    isActive = report.status == "RESOLVED",
                    color = statusColor
                )
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(Date(report.createdAt)),
                    fontSize = 11.sp,
                    color = GrayText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Reporter: ${report.reporterName}",
                fontSize = 13.sp,
                color = TextDark,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Reason: ${report.reason}",
                fontSize = 12.sp,
                color = GrayText
            )
            Text(
                text = "Target: ${report.targetType}",
                fontSize = 12.sp,
                color = GrayText
            )

            if (report.status == "PENDING") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take Action")
                }
            }
        }
    }
}
