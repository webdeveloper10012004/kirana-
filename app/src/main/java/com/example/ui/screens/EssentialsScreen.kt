package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.ProductEntity
import com.example.data.StockStatus
import com.example.ui.KiranaViewModel
import com.example.ui.components.RestockDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssentialsScreen(
    viewModel: KiranaViewModel,
    onNavigateToBilling: () -> Unit,
    modifier: Modifier = Modifier
) {
    val essentials by viewModel.essentialSupplies.collectAsState()
    val stockMetrics by viewModel.stockMetrics.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var activeFilter by remember { mutableStateOf("ALL") } // "ALL", "CRITICAL", "IN_STOCK"
    var productToRestock by remember { mutableStateOf<ProductEntity?>(null) }

    val displayedEssentials = remember(essentials, activeFilter) {
        when (activeFilter) {
            "CRITICAL" -> essentials.filter { it.stockQuantity <= it.lowStockThreshold }
            "IN_STOCK" -> essentials.filter { it.stockQuantity > it.lowStockThreshold }
            else -> essentials
        }
    }

    val criticalCount = essentials.count { it.stockQuantity <= it.lowStockThreshold }
    val outOfStockCount = essentials.count { it.stockQuantity <= 0.0 }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (criticalCount > 0) StockLowColor else StockInStockColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Essential Supplies (Real-Time)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Live availability • Auto-sync on sales",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
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
            // Live Real-Time Availability Metrics Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("essentials_metrics_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricItem(
                        count = essentials.size.toString(),
                        label = "Tracked",
                        color = MaterialTheme.colorScheme.primary
                    )
                    VerticalDivider(modifier = Modifier.height(36.dp))
                    MetricItem(
                        count = (essentials.size - criticalCount).toString(),
                        label = "In Stock",
                        color = StockInStockColor
                    )
                    VerticalDivider(modifier = Modifier.height(36.dp))
                    MetricItem(
                        count = criticalCount.toString(),
                        label = "Low / Out",
                        color = if (criticalCount > 0) StockLowColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Critical Supplies Banner if any essential items need restock
            if (criticalCount > 0) {
                Surface(
                    color = if (outOfStockCount > 0) StockOutColor.copy(alpha = 0.12f) else StockLowColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = if (outOfStockCount > 0) StockOutColor else StockLowColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (outOfStockCount > 0) "Immediate Restock Required!" else "Essential Supplies Running Low",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (outOfStockCount > 0) StockOutColor else StockLowColor
                                )
                            )
                            Text(
                                text = "$criticalCount essential item(s) are below safety stock.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                            )
                        }
                        TextButton(
                            onClick = { activeFilter = "CRITICAL" }
                        ) {
                            Text("View", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Search and Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeFilter == "ALL",
                    onClick = { activeFilter = "ALL" },
                    label = { Text("All Essentials (${essentials.size})") },
                    shape = RoundedCornerShape(8.dp)
                )
                FilterChip(
                    selected = activeFilter == "CRITICAL",
                    onClick = { activeFilter = "CRITICAL" },
                    label = {
                        Text(
                            "⚠️ Low & Out ($criticalCount)",
                            color = if (criticalCount > 0) StockLowColor else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                FilterChip(
                    selected = activeFilter == "IN_STOCK",
                    onClick = { activeFilter = "IN_STOCK" },
                    label = { Text("Healthy Stock") },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // List of Essential Supplies Cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(displayedEssentials, key = { it.id }) { product ->
                    EssentialProductCard(
                        product = product,
                        onQuickAddStock = { qty ->
                            viewModel.quickRestock(product.id, qty)
                        },
                        onOpenRestockDialog = {
                            productToRestock = product
                        },
                        onAddToCart = {
                            viewModel.addToCart(product, 1.0)
                            onNavigateToBilling()
                        }
                    )
                }
            }
        }
    }

    // Restock Dialog if open
    productToRestock?.let { product ->
        RestockDialog(
            product = product,
            onDismiss = { productToRestock = null },
            onRestock = { addedQty ->
                viewModel.quickRestock(product.id, addedQty)
            }
        )
    }
}

@Composable
fun MetricItem(
    count: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
fun EssentialProductCard(
    product: ProductEntity,
    onQuickAddStock: (Double) -> Unit,
    onOpenRestockDialog: () -> Unit,
    onAddToCart: () -> Unit
) {
    val isOutOfStock = product.stockQuantity <= 0.0
    val isLowStock = !isOutOfStock && product.stockQuantity <= product.lowStockThreshold

    val statusColor = when {
        isOutOfStock -> StockOutColor
        isLowStock -> StockLowColor
        else -> StockInStockColor
    }

    val statusText = when {
        isOutOfStock -> "OUT OF STOCK"
        isLowStock -> "LOW STOCK"
        else -> "IN STOCK"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("essential_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Product name, Category & Real-Time Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2
                    )
                    Text(
                        text = "${product.category} • ₹${"%.2f".format(product.price)}/${product.unit}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Real-time Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock Availability Level Progress Bar
            val safeThreshold = product.lowStockThreshold * 2.0
            val progress = if (safeThreshold > 0) (product.stockQuantity / safeThreshold).toFloat().coerceIn(0f, 1f) else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Stock:",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    text = "${if (product.stockQuantity % 1.0 == 0.0) product.stockQuantity.toInt() else product.stockQuantity} ${product.unit}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Restock Buttons: Essential supplies need lightning-fast stock replenishment!
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onQuickAddStock(5.0) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+5 ${product.unit}", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = { onQuickAddStock(10.0) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+10 ${product.unit}", style = MaterialTheme.typography.labelSmall)
                    }

                    FilledTonalIconButton(
                        onClick = onOpenRestockDialog,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Custom Restock", modifier = Modifier.size(16.dp))
                    }
                }

                // Add to Bill Action
                Button(
                    onClick = onAddToCart,
                    enabled = !isOutOfStock,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KiranaGreenPrimary)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sell", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
