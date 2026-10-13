package com.cityride.demo.domain.usecase

import com.cityride.demo.domain.model.Catalog
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.domain.model.TicketProduct
import com.cityride.demo.domain.model.TicketStatus
import com.cityride.demo.domain.repository.TicketRepository
import com.cityride.demo.domain.repository.UserRepository
import com.cityride.demo.util.QrPayload
import java.util.UUID

sealed interface PurchaseResult {
    data class Success(val ticket: Ticket) : PurchaseResult
    data object InsufficientBalance : PurchaseResult
}

class PurchaseTicketUseCase(
    private val tickets: TicketRepository,
    private val users: UserRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(product: TicketProduct): PurchaseResult {
        if (!users.chargeCents(product.priceCents)) return PurchaseResult.InsufficientBalance
        val ticket = Ticket(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            name = product.name,
            validityLabel = product.validityLabel,
            durationMinutes = product.durationMinutes,
            priceCents = product.priceCents,
            purchasedAtMillis = clock(),
            route = product.route,
        )
        tickets.insert(ticket)
        return PurchaseResult.Success(ticket)
    }
}

class ActivateTicketUseCase(
    private val tickets: TicketRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(ticketId: String, busNumber: String): Ticket {
        val bus = busNumber.trim()
        require(bus.isNotEmpty()) { "Bus number is required" }
        val ticket = requireNotNull(tickets.getTicket(ticketId)) { "Ticket not found" }
        val now = clock()
        check(ticket.statusAt(now) == TicketStatus.NON_ATTIVO) { "Ticket already activated" }
        val activated = ticket.copy(
            activatedAtMillis = now,
            expiresAtMillis = now + ticket.durationMinutes * 60_000L,
            busNumber = bus,
            qrPayload = QrPayload.create(ticket.id, now),
        )
        tickets.update(activated)
        return activated
    }
}

/**
 * Turns any scanned QR content into a ticket to display. Codes issued by this app
 * resolve to their ticket; any other code starts a 60-minute demo session.
 */
class ResolveScannedCodeUseCase(
    private val tickets: TicketRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(payload: String, busNumber: String): Ticket {
        QrPayload.parseTicketId(payload)?.let { id -> tickets.getTicket(id)?.let { return it } }
        tickets.getByPayload(payload)?.let { return it }

        val product = Catalog.scannedFallback
        val now = clock()
        val ticket = Ticket(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            name = product.name,
            validityLabel = product.validityLabel,
            durationMinutes = product.durationMinutes,
            priceCents = 0,
            purchasedAtMillis = now,
            activatedAtMillis = now,
            expiresAtMillis = now + product.durationMinutes * 60_000L,
            busNumber = busNumber.trim().ifEmpty { null },
            qrPayload = payload,
        )
        tickets.insert(ticket)
        return ticket
    }
}
