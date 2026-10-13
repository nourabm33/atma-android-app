package com.cityride.demo.domain.repository

import com.cityride.demo.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<User>
    suspend fun addBalanceCents(amount: Int)
    suspend fun chargeCents(amount: Int): Boolean
}
