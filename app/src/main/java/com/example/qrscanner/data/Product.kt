package com.example.qrscanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val numberQR: String
)
