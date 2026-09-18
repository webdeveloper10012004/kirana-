package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CartItem
import com.example.data.DailySalesStats
import com.example.data.KiranaRepository
import com.example.data.ProductEntity
import com.example.data.SaleItemEntity
import com.example.data.SaleTransaction
import com.example.data.SaleWithItems
import com.example.data.StockStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KiranaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KiranaRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = KiranaRepository(db, db.productDao(), db.saleDao())
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureInitialData()
        }
    }

    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todaySales: StateFlow<List<SaleTransaction>> = repository.getTodaySales()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSales: StateFlow<List<SaleTransaction>> = repository.allSales
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filter and search states
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>("All")
    val filterOnlyEssentials = MutableStateFlow(false)
    val filterOnlyLowStock = MutableStateFlow(false)

    // Cart state
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Last completed transaction for receipt popup
    private val _lastSaleReceipt = MutableStateFlow<SaleWithItems?>(null)
    val lastSaleReceipt: StateFlow<SaleWithItems?> = _lastSaleReceipt.asStateFlow()

    // Filtered products flow
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        searchQuery,
        selectedCategory,
        filterOnlyEssentials,
        filterOnlyLowStock
    ) { products, query, category, essentialsOnly, lowStockOnly ->
        products.filter { product ->
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)

            val matchesCategory = category == null || category == "All" || product.category.equals(category, ignoreCase = true)

            val matchesEssentials = !essentialsOnly || product.isEssential

            val matchesLowStock = !lowStockOnly || (product.stockQuantity <= product.lowStockThreshold)

            matchesQuery && matchesCategory && matchesEssentials && matchesLowStock
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Essential supplies specifically for the Real-Time availability dashboard
    val essentialSupplies: StateFlow<List<ProductEntity>> = allProducts.combine(searchQuery) { products, query ->
        products.filter { it.isEssential && (query.isBlank() || it.name.contains(query, ignoreCase = true)) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Metrics for real-time stock status
    val stockMetrics: StateFlow<StockMetrics> = allProducts.combine(essentialSupplies) { all, essentials ->
        val totalProducts = all.size
        val inStockCount = all.count { it.stockQuantity > it.lowStockThreshold }
        val lowStockCount = all.count { it.stockQuantity > 0 && it.stockQuantity <= it.lowStockThreshold }
        val outOfStockCount = all.count { it.stockQuantity <= 0.0 }
        val criticalEssentialsCount = essentials.count { it.stockQuantity <= it.lowStockThreshold }

        StockMetrics(
            totalProducts = totalProducts,
            inStockCount = inStockCount,
            lowStockCount = lowStockCount,
            outOfStockCount = outOfStockCount,
            criticalEssentialsCount = criticalEssentialsCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StockMetrics()
    )

    // Today's Sales Stats
    val todayStats: StateFlow<DailySalesStats> = todaySales.combine(allSales) { todayList, _ ->
        val revenue = todayList.sumOf { it.totalAmount }
        val profit = todayList.sumOf { it.totalProfit }
        val bills = todayList.size
        val itemsCount = todayList.sumOf { it.totalItemsCount.toDouble() }
        val cash = todayList.filter { it.paymentMethod.equals("CASH", ignoreCase = true) }.sumOf { it.totalAmount }
        val upi = todayList.filter { it.paymentMethod.equals("UPI", ignoreCase = true) }.sumOf { it.totalAmount }
        val khata = todayList.filter { it.paymentMethod.equals("KHATA", ignoreCase = true) }.sumOf { it.totalAmount }

        DailySalesStats(
            totalRevenue = revenue,
            totalProfit = profit,
            totalBills = bills,
            totalItemsSold = itemsCount,
            cashAmount = cash,
            upiAmount = upi,
            khataAmount = khata
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailySalesStats()
    )

    // Cart Operations
    fun addToCart(product: ProductEntity, quantity: Double = 1.0): Boolean {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.product.id == product.id }

        if (existingIndex != -1) {
            val existing = currentList[existingIndex]
            val newQty = existing.quantity + quantity
            if (newQty > product.stockQuantity && product.stockQuantity > 0) {
                // Cannot add more than current stock
                return false
            }
            currentList[existingIndex] = existing.copy(quantity = newQty)
        } else {
            if (product.stockQuantity <= 0.0) {
                return false
            }
            val initialQty = minOf(quantity, product.stockQuantity)
            currentList.add(CartItem(product = product, quantity = initialQty))
        }
        _cartItems.value = currentList
        return true
    }

    fun updateCartItemQuantity(productId: Long, newQuantity: Double) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            if (newQuantity <= 0.0) {
                currentList.removeAt(index)
            } else {
                val item = currentList[index]
                val maxAllowed = item.product.stockQuantity
                val clampedQty = if (maxAllowed > 0) minOf(newQuantity, maxAllowed) else newQuantity
                currentList[index] = item.copy(quantity = clampedQty)
            }
            _cartItems.value = currentList
        }
    }

    fun removeFromCart(productId: Long) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Quick restock function directly from inventory cards
    fun quickRestock(productId: Long, quantityToAdd: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restockProduct(productId, quantityToAdd)
        }
    }

    fun updateProductStockDirect(productId: Long, newStock: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProductStockDirect(productId, newStock)
        }
    }

    fun saveProduct(product: ProductEntity, isNew: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isNew) {
                repository.insertProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
        }
    }

    /**
     * Completes checkout:
     * Atomically records the sale transaction and updates stock availability in real time.
     */
    fun checkout(
        paymentMethod: String,
        customerName: String,
        customerPhone: String,
        note: String,
        onSuccess: (SaleWithItems) -> Unit
    ) {
        val itemsToCheckout = _cartItems.value
        if (itemsToCheckout.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val saleId = repository.completeTransaction(
                paymentMethod = paymentMethod,
                customerName = customerName,
                customerPhone = customerPhone,
                note = note,
                cartItems = itemsToCheckout
            )

            if (saleId > 0) {
                val recordedItems = repository.getSaleItemsSync(saleId)
                val saleTransaction = SaleTransaction(
                    id = saleId,
                    timestamp = System.currentTimeMillis(),
                    totalAmount = itemsToCheckout.sumOf { it.subtotal },
                    totalProfit = itemsToCheckout.sumOf { it.profit },
                    totalItemsCount = itemsToCheckout.sumOf { it.quantity.toInt().coerceAtLeast(1) },
                    paymentMethod = paymentMethod,
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    note = note.trim()
                )
                val receipt = SaleWithItems(saleTransaction, recordedItems)
                _lastSaleReceipt.value = receipt
                _cartItems.value = emptyList()

                launch(Dispatchers.Main) {
                    onSuccess(receipt)
                }
            }
        }
    }

    fun dismissReceiptDialog() {
        _lastSaleReceipt.value = null
    }

    fun getSaleDetails(sale: SaleTransaction, onResult: (SaleWithItems) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.getSaleItemsSync(sale.id)
            launch(Dispatchers.Main) {
                onResult(SaleWithItems(sale, items))
            }
        }
    }
}

data class StockMetrics(
    val totalProducts: Int = 0,
    val inStockCount: Int = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val criticalEssentialsCount: Int = 0
)
