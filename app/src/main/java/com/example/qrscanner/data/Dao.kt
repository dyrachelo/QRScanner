package com.example.qrscanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM Products")
    fun getAllProducts() : Flow<List<Product>>

    @Query("SELECT * FROM products WHERE numberQR = :qr")
    fun getProductByQr(qr: String): Product?


}