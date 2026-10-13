package com.cityride.demo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val name: String,
    val validityLabel: String,
    val durationMinutes: Int,
    val priceCents: Int,
    val purchasedAtMillis: Long,
    val activatedAtMillis: Long?,
    val expiresAtMillis: Long?,
    val busNumber: String?,
    val route: String?,
    val qrPayload: String?,
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val walletCents: Int,
)
