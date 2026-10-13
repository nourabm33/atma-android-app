package com.cityride.demo.domain.model

/** Lifecycle state of a ticket, derived from the clock rather than stored. */
enum class TicketStatus { NON_ATTIVO, ATTIVO, SCADUTO }

data class Ticket(
    val id: String,
    val productId: String,
    val name: String,
    val validityLabel: String,
    val durationMinutes: Int,
    val priceCents: Int,
    val purchasedAtMillis: Long,
    val activatedAtMillis: Long? = null,
    val expiresAtMillis: Long? = null,
    val busNumber: String? = null,
    val route: String? = null,
    val qrPayload: String? = null,
) {
    fun statusAt(nowMillis: Long): TicketStatus = when {
        activatedAtMillis == null || expiresAtMillis == null -> TicketStatus.NON_ATTIVO
        nowMillis < expiresAtMillis -> TicketStatus.ATTIVO
        else -> TicketStatus.SCADUTO
    }

    fun remainingMillisAt(nowMillis: Long): Long =
        expiresAtMillis?.let { (it - nowMillis).coerceAtLeast(0L) } ?: 0L
}
