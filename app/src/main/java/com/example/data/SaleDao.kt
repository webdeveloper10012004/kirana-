package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sale_transactions ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sale_transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getSalesBetween(startTime: Long, endTime: Long): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getSaleItemsForSale(saleId: Long): Flow<List<SaleItemEntity>>

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getSaleItemsSync(saleId: Long): List<SaleItemEntity>

    @Query("SELECT * FROM sale_items ORDER BY id DESC LIMIT 50")
    fun getRecentSaleItems(): Flow<List<SaleItemEntity>>
}
