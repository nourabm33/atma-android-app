package com.cityride.demo.data.repository

import com.cityride.demo.data.local.UserDao
import com.cityride.demo.data.local.UserEntity
import com.cityride.demo.domain.model.User
import com.cityride.demo.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {

    override fun observeCurrentUser(): Flow<User> = dao.observeFirst().map { entity ->
        entity?.toDomain() ?: User(0, DEFAULT_NAME, DEFAULT_EMAIL, 0)
    }

    override suspend fun addBalanceCents(amount: Int) {
        val user = ensureUser()
        dao.update(user.copy(walletCents = user.walletCents + amount))
    }

    override suspend fun chargeCents(amount: Int): Boolean {
        val user = ensureUser()
        if (user.walletCents < amount) return false
        dao.update(user.copy(walletCents = user.walletCents - amount))
        return true
    }

    private suspend fun ensureUser(): UserEntity {
        dao.getFirst()?.let { return it }
        val id = dao.insert(
            UserEntity(fullName = DEFAULT_NAME, email = DEFAULT_EMAIL, walletCents = 1000)
        )
        return dao.getFirst() ?: UserEntity(id, DEFAULT_NAME, DEFAULT_EMAIL, 1000)
    }

    private companion object {
        const val DEFAULT_NAME = "Utente Demo"
        const val DEFAULT_EMAIL = "demo@cityride.app"
    }
}

private fun UserEntity.toDomain() = User(id, fullName, email, walletCents)
