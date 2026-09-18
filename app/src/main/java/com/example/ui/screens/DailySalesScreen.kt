package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailySalesStats
import com.example.data.SaleTransaction
import com.example.data.SaleWithItems
import com.example.ui.KiranaViewModel
import com.example.ui.components.ReceiptDialog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySalesScreen(
    viewModel: KiranaViewModel,
    modifier: Modifier = Modifier
) {
    val todaySales by viewModel.todaySales.collectAsState()
    val allSales by viewModel.allSales.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()

    var viewMode by remember { mutableStateOf("TODAY") } // "TODAY" or "ALL"
    var selectedSaleDetails by remember { mutableStateOf<SaleWithItems?>(null) }

    val displayedSales = if (viewMode == "TODAY") todaySales else allSales

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Daily Sales & Revenue",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Track sales, payments & profits",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // View Mode Selector (Today vs All-Time History)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = viewMode == "TODAY",
                    onClick = { viewMode = "TODAY" },
                    label = { Text("Today's Sales (${todaySales.size})") },
                    shape = RoundedCornerShape(8.dp)
                )
                FilterChip(
                    selected = viewMode == "ALL",
                    onClick = { viewMode = "ALL" },
                    label = { Text("All Transactions (${allSales.size})") },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Hero Daily Sales KPI Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .testTag("sales_summary_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KiranaGreenLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (viewMode == "TODAY") "Today's Total Revenue" else "Total Revenue",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹${"%.2f".format(todayStats.totalRevenue)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = KiranaGreenPrimary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Est. Profit",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = "₹${"%.2f".format(todayStats.totalProfit)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = KiranaGreenDark
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = KiranaGreenPrimary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Metrics (Bills Count & Items Count)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${todayStats.totalBills}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Total Bills",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(30.dp), color = KiranaGreenPrimary.copy(alpha = 0.2f))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${todayStats.totalItemsSold.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Items Sold",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(30.dp), color = KiranaGreenPrimary.copy(alpha = 0.2f))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val avgBill = if (todayStats.totalBills > 0) todayStats.totalRevenue / todayStats.totalBills else 0.0
                            Text(
                                text = "₹${"%.0f".format(avgBill)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Avg Bill",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }

            // Payment Methods Breakdown (Cash vs UPI vs Khata)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Payment Modes Breakdown",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PaymentPill(
                            label = "💵 Cash",
                            amount = todayStats.cashAmount,
                            bgColor = Color(0xFFE8F5E9),
                            textColor = Color(0xFF2E7D32)
                        )
                        PaymentPill(
                            label = "📱 UPI",
                            amount = todayStats.upiAmount,
                            bgColor = Color(0xFFE3F2FD),
                            textColor = Color(0xFF1565C0)
                        )
                        PaymentPill(
                            label = "📓 Khata",
                            amount = todayStats.khataAmount,
                            bgColor = Color(0xFFF3E5F5),
                            textColor = Color(0xFF6A1B9A)
                        )
                    }
                }
            }

            // Transaction History Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transactions Log",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${displayedSales.size} recorded",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            // Transaction Items List
            if (displayedSales.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No transactions found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Completed sales will appear here in real time.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(displayedSales, key = { it.id }) { sale ->
                        TransactionRow(
                            sale = sale,
                            formattedTime = dateFormat.format(Date(sale.timestamp)),
                            onClick = {
                                viewModel.getSaleDetails(sale) { fullSale ->
                                    selectedSaleDetails = fullSale
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Receipt details dialog when tapping transaction
    selectedSaleDetails?.let { details ->
        ReceiptDialog(
            receipt = details,
            onDismiss = { selectedSaleDetails = null }
        )
    }
}

@Composable
fun RowScope.PaymentPill(
    label: String,
    amount: Double,
    bgColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        modifier = Modifier.weight(1f).padding(horizontal = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "₹${"%.1f".format(amount)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun TransactionRow(
    sale: SaleTransaction,
    formattedTime: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("transaction_row_${sale.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (sale.paymentMethod) {
                                "UPI" -> Color(0xFFE3F2FD)
                                "CASH" -> Color(0xFFE8F5E9)
                                else -> Color(0xFFF3E5F5)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (sale.paymentMethod) {
                            "UPI" -> Icons.Default.QrCode
                            "CASH" -> Icons.Default.Payments
                            else -> Icons.Default.Book
                        },
                        contentDescription = null,
                        tint = when (sale.paymentMethod) {
                            "UPI" -> Color(0xFF1565C0)
                            "CASH" -> Color(0xFF2E7D32)
                            else -> Color(0xFF6A1B9A)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (sale.customerName.isNotBlank()) sale.customerName else "Walk-in Sale #${sale.id}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "$formattedTime • ${sale.totalItemsCount} item(s) • ${sale.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${"%.2f".format(sale.totalAmount)}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = KiranaGreenPrimary
                    )
                )
                Text(
                    text = "+₹${"%.1f".format(sale.totalProfit)} profit",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = StockInStockColor,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
