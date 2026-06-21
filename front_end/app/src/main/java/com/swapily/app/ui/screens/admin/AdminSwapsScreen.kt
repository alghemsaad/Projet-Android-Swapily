package com.swapily.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swapily.app.data.model.Swap
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.AdminSwapsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSwapsScreen(
    viewModel: AdminSwapsViewModel
) {
    val swaps by viewModel.swaps.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val statuses = listOf("ALL", "PENDING", "ACCEPTED", "REJECTED")

    val filteredSwaps = if (selectedStatus == "ALL") swaps
        else swaps.filter { it.status.equals(selectedStatus, ignoreCase = true) }

    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Swaps Management",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Status filter chips
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { status ->
                    FilterChip(
                        selected = status == selectedStatus,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = {
                            Text(
                                status.lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = White,
                            containerColor = GreenLight.copy(alpha = 0.5f),
                            labelColor = GreenPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${filteredSwaps.size} swaps",
                fontSize = 13.sp,
                color = GrayText
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredSwaps.isEmpty() && !loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SwapHoriz, null, tint = GrayText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No swaps found", color = GrayText)
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredSwaps, key = { it.id }) { swap ->
                    SwapCard(swap)
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun SwapCard(swap: Swap) {
    val statusColor = when (swap.status.uppercase()) {
        "PENDING" -> Color(0xFFFF9800)
        "ACCEPTED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFF44336)
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
                    text = swap.status.lowercase().replaceFirstChar { it.uppercase() },
                    isActive = swap.status == "ACCEPTED",
                    color = statusColor
                )
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(Date(swap.timestamp)),
                    fontSize = 11.sp,
                    color = GrayText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User A
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = swap.senderName.ifEmpty { "User A" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = swap.senderProductTitle.ifEmpty { "Product A" },
                        fontSize = 11.sp,
                        color = GrayText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    Icons.Default.SwapHoriz,
                    null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(24.dp)
                )

                // User B
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = swap.receiverName.ifEmpty { "User B" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = swap.receiverProductTitle.ifEmpty { "Product B" },
                        fontSize = 11.sp,
                        color = GrayText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
