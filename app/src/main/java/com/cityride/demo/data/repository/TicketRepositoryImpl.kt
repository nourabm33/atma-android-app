package com.cityride.demo.data.repository

import com.cityride.demo.data.local.TicketDao
import com.cityride.demo.data.local.TicketEntity
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TicketRepositoryImpl(private val dao: TicketDao) : TicketRepository {

    override fun observeTickets(): Flow<List<Ticket>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeTicket(id: String): Flow<Ticket?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun getTicket(id: String): Ticket? = dao.getById(id)?.toDomain()

    override suspend fun getByPayload(payload: String): Ticket? =
        dao.getByPayload(payload)?.toDomain()

    override suspend fun insert(ticket: Ticket) = dao.insert(ticket.toEntity())

    override suspend fun update(ticket: Ticket) = dao.update(ticket.toEntity())
}

private fun TicketEntity.toDomain() = Ticket(
    id = id,
    productId = productId,
    name = name,
    validityLabel = validityLabel,
    durationMinutes = durationMinutes,
    priceCents = priceCents,
    purchasedAtMillis = purchasedAtMillis,
    activatedAtMillis = activatedAtMillis,
    expiresAtMillis = expiresAtMillis,
    busNumber = busNumber,
    route = route,
    qrPayload = qrPayload,
)

private fun Ticket.toEntity() = TicketEntity(
    id = id,
    productId = productId,
    name = name,
    validityLabel = validityLabel,
    durationMinutes = durationMinutes,
    priceCents = priceCents,
    purchasedAtMillis = purchasedAtMillis,
    activatedAtMillis = activatedAtMillis,
    expiresAtMillis = expiresAtMillis,
    busNumber = busNumber,
    route = route,
    qrPayload = qrPayload,
)
