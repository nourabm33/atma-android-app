package com.cityride.demo.domain.model

/** A ticket type offered in the Acquista tab. */
data class TicketProduct(
    val id: String,
    val name: String,
    val validityLabel: String,
    val durationMinutes: Int,
    val priceCents: Int,
    val route: String? = null,
)

object Catalog {
    val products = listOf(
        TicketProduct(
            id = "urban-60",
            name = "Biglietto urbano 60 minuti",
            validityLabel = "Valido 60 minuti",
            durationMinutes = 60,
            priceCents = 160,
        ),
        TicketProduct(
            id = "urban-100",
            name = "Biglietto urbano 100 minuti",
            validityLabel = "Valido 100 minuti",
            durationMinutes = 100,
            priceCents = 250,
        ),
        TicketProduct(
            id = "daily",
            name = "Biglietto giornaliero",
            validityLabel = "Valido 24 ore",
            durationMinutes = 24 * 60,
            priceCents = 450,
        ),
        TicketProduct(
            id = "suburban-30",
            name = "Biglietto extraurbano 30 km",
            validityLabel = "Valido per una sola corsa",
            durationMinutes = 90,
            priceCents = 320,
            route = "Centro - Litorale",
        ),
    )

    fun byId(id: String): TicketProduct? = products.firstOrNull { it.id == id }

    /** Fallback used when the scanner reads a code that this demo did not issue. */
    val scannedFallback = TicketProduct(
        id = "scanned",
        name = "Codice scansionato (demo)",
        validityLabel = "Valido 60 minuti",
        durationMinutes = 60,
        priceCents = 0,
    )
}
