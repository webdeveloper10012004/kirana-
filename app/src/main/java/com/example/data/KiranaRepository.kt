package com.example.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class KiranaRepository(
    private val database: AppDatabase,
    private val productDao: ProductDao,
    private val saleDao: SaleDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val essentialProducts: Flow<List<ProductEntity>> = productDao.getEssentialProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts()
    val allSales: Flow<List<SaleTransaction>> = saleDao.getAllSales()

    fun getTodaySales(): Flow<List<SaleTransaction>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return saleDao.getSalesBetween(startOfDay, endOfDay)
    }

    fun getSaleItems(saleId: Long): Flow<List<SaleItemEntity>> =
        saleDao.getSaleItemsForSale(saleId)

    suspend fun getSaleItemsSync(saleId: Long): List<SaleItemEntity> =
        saleDao.getSaleItemsSync(saleId)

    suspend fun ensureInitialData() {
        val count = productDao.countProducts()
        if (count == 0) {
            val initialProducts = InitialData.getInitialProducts()
            productDao.insertAll(initialProducts)
            val currentProducts = productDao.getAllProducts()
            // We can also add initial sample transactions for immediate rich charts & stats
            val seededProducts = InitialData.getInitialProducts()
            // Retrieve inserted IDs
            val initialTxns = InitialData.getInitialTransactions(seededProducts)
            for ((sale, items) in initialTxns) {
                val saleId = saleDao.insertSale(sale)
                val updatedItems = items.map { it.copy(saleId = saleId) }
                saleDao.insertSaleItems(updatedItems)
            }
        }
    }

    /**
     * Executes the sale transaction atomically:
     * 1. Records the sale transaction
     * 2. Automatically updates product availability in real-time by deducting sold quantities
     * 3. Records individual sale line items
     */
    suspend fun completeTransaction(
        paymentMethod: String,
        customerName: String,
        customerPhone: String,
        note: String,
        cartItems: List<CartItem>
    ): Long {
        if (cartItems.isEmpty()) return -1L

        val totalAmount = cartItems.sumOf { it.subtotal }
        val totalProfit = cartItems.sumOf { it.profit }
        val totalItemsCount = cartItems.sumOf { it.quantity.toInt().coerceAtLeast(1) }

        return database.withTransaction {
            val sale = SaleTransaction(
                totalAmount = totalAmount,
                totalProfit = totalProfit,
                totalItemsCount = totalItemsCount,
                paymentMethod = paymentMethod,
                customerName = customerName.trim(),
                customerPhone = customerPhone.trim(),
                note = note.trim()
            )
            val saleId = saleDao.insertSale(sale)

            val saleItems = mutableListOf<SaleItemEntity>()
            for (cartItem in cartItems) {
                // Deduct stock quantity in real time
                val currentProduct = productDao.getProductByIdSync(cartItem.product.id)
                if (currentProduct != null) {
                    val updatedStock = (currentProduct.stockQuantity - cartItem.quantity).coerceAtLeast(0.0)
                    productDao.updateStock(
                        id = currentProduct.id,
                        newStock = updatedStock,
                        updatedAt = System.currentTimeMillis()
                    )
                }

                saleItems.add(
                    SaleItemEntity(
                        saleId = saleId,
                        productId = cartItem.product.id,
                        productName = cartItem.product.name,
                        quantity = cartItem.quantity,
                        unit = cartItem.product.unit,
                        unitPrice = cartItem.product.price,
                        totalPrice = cartItem.subtotal,
                        profit = cartItem.profit
                    )
                )
            }

            saleDao.insertSaleItems(saleItems)
            saleId
        }
    }

    suspend fun restockProduct(productId: Long, quantityToAdd: Double) {
        productDao.addStock(productId, quantityToAdd)
    }

    suspend fun updateProductStockDirect(productId: Long, newStock: Double) {
        productDao.updateStock(productId, newStock)
    }

    suspend fun insertProduct(product: ProductEntity): Long =
        productDao.insert(product)

    suspend fun updateProduct(product: ProductEntity) =
        productDao.update(product)

    suspend fun deleteProduct(product: ProductEntity) =
        productDao.delete(product)
}
