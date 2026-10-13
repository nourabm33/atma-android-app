package com.cityride.demo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets ORDER BY purchasedAtMillis DESC")
    fun observeAll(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :id")
    fun observeById(id: String): Flow<TicketEntity?>

    @Query("SELECT * FROM tickets WHERE id = :id")
    suspend fun getById(id: String): TicketEntity?

    @Query("SELECT * FROM tickets WHERE qrPayload = :payload ORDER BY purchasedAtMillis DESC LIMIT 1")
    suspend fun getByPayload(payload: String): TicketEntity?

    @Insert
    suspend fun insert(ticket: TicketEntity)

    @Update
    suspend fun update(ticket: TicketEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun observeFirst(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirst(): UserEntity?

    @Insert
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)
}
