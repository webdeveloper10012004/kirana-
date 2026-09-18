package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ReceiptDialog
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.DailySalesScreen
import com.example.ui.screens.EssentialsScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.theme.KiranaGreenPrimary
import com.example.ui.theme.StockLowColor

enum class KiranaTab {
    BILLING,
    ESSENTIALS,
    INVENTORY,
    SALES
}

@Composable
fun KiranaApp(
    viewModel: KiranaViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(KiranaTab.BILLING) }

    val cartItems by viewModel.cartItems.collectAsState()
    val stockMetrics by viewModel.stockMetrics.collectAsState()
    val lastSaleReceipt by viewModel.lastSaleReceipt.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // 1. Billing POS
                NavigationBarItem(
                    selected = selectedTab == KiranaTab.BILLING,
                    onClick = { selectedTab = KiranaTab.BILLING },
                    icon = {
                        BadgedBox(badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = KiranaGreenPrimary) {
                                    Text("${cartItems.size}")
                                }
                            }
                        }) {
                            Icon(Icons.Default.PointOfSale, contentDescription = "Billing")
                        }
                    },
                    label = { Text("Billing", fontWeight = if (selectedTab == KiranaTab.BILLING) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_billing")
                )

                // 2. Real-Time Essentials
                NavigationBarItem(
                    selected = selectedTab == KiranaTab.ESSENTIALS,
                    onClick = { selectedTab = KiranaTab.ESSENTIALS },
                    icon = {
                        BadgedBox(badge = {
                            if (stockMetrics.criticalEssentialsCount > 0) {
                                Badge(containerColor = StockLowColor) {
                                    Text("${stockMetrics.criticalEssentialsCount}")
                                }
                            }
                        }) {
                            Icon(Icons.Default.Storefront, contentDescription = "Essentials")
                        }
                    },
                    label = { Text("Essentials", fontWeight = if (selectedTab == KiranaTab.ESSENTIALS) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_essentials")
                )

                // 3. Inventory
                NavigationBarItem(
                    selected = selectedTab == KiranaTab.INVENTORY,
                    onClick = { selectedTab = KiranaTab.INVENTORY },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
                    label = { Text("Inventory", fontWeight = if (selectedTab == KiranaTab.INVENTORY) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_inventory")
                )

                // 4. Daily Sales
                NavigationBarItem(
                    selected = selectedTab == KiranaTab.SALES,
                    onClick = { selectedTab = KiranaTab.SALES },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Sales") },
                    label = { Text("Sales", fontWeight = if (selectedTab == KiranaTab.SALES) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_sales")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                KiranaTab.BILLING -> BillingScreen(viewModel = viewModel)
                KiranaTab.ESSENTIALS -> EssentialsScreen(
                    viewModel = viewModel,
                    onNavigateToBilling = { selectedTab = KiranaTab.BILLING }
                )
                KiranaTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
                KiranaTab.SALES -> DailySalesScreen(viewModel = viewModel)
            }
        }

        // Automatic live receipt whenever a transaction completes
        lastSaleReceipt?.let { receipt ->
            ReceiptDialog(
                receipt = receipt,
                onDismiss = { viewModel.dismissReceiptDialog() }
            )
        }
    }
}
