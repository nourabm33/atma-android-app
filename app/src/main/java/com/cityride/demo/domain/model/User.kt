package com.cityride.demo.domain.model

data class User(
    val id: Long,
    val fullName: String,
    val email: String,
    val walletCents: Int,
)
