package com.cityride.demo.domain.repository

import com.cityride.demo.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    fun observeTickets(): Flow<List<Ticket>>
    fun observeTicket(id: String): Flow<Ticket?>
    suspend fun getTicket(id: String): Ticket?
    suspend fun getByPayload(payload: String): Ticket?
    suspend fun insert(ticket: Ticket)
    suspend fun update(ticket: Ticket)
}
