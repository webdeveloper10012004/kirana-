package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val stockQuantity: Double,
    val unit: String, // "kg", "packet", "litre", "piece", "gram"
    val price: Double, // Selling price in ₹
    val costPrice: Double, // Purchase/cost price in ₹
    val lowStockThreshold: Double = 5.0,
    val isEssential: Boolean = false,
    val barcode: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val stockStatus: StockStatus
        get() = when {
            stockQuantity <= 0.0 -> StockStatus.OUT_OF_STOCK
            stockQuantity <= lowStockThreshold -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
}

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}

@Entity(tableName = "sale_transactions")
data class SaleTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val totalProfit: Double,
    val totalItemsCount: Int,
    val paymentMethod: String, // "CASH", "UPI", "KHATA"
    val customerName: String = "",
    val customerPhone: String = "",
    val note: String = ""
)

@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleTransaction::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("saleId"), Index("productId")]
)
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Double,
    val unit: String,
    val unitPrice: Double,
    val totalPrice: Double,
    val profit: Double
)

data class CartItem(
    val product: ProductEntity,
    val quantity: Double = 1.0
) {
    val subtotal: Double
        get() = product.price * quantity

    val profit: Double
        get() = (product.price - product.costPrice) * quantity
}

data class DailySalesStats(
    val totalRevenue: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalBills: Int = 0,
    val totalItemsSold: Double = 0.0,
    val cashAmount: Double = 0.0,
    val upiAmount: Double = 0.0,
    val khataAmount: Double = 0.0
)

data class SaleWithItems(
    val sale: SaleTransaction,
    val items: List<SaleItemEntity>
)
