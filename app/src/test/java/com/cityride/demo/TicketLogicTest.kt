package com.cityride.demo

import com.cityride.demo.domain.model.Catalog
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.domain.model.TicketStatus
import com.cityride.demo.domain.repository.TicketRepository
import com.cityride.demo.domain.repository.UserRepository
import com.cityride.demo.domain.model.User
import com.cityride.demo.domain.usecase.ActivateTicketUseCase
import com.cityride.demo.domain.usecase.PurchaseResult
import com.cityride.demo.domain.usecase.PurchaseTicketUseCase
import com.cityride.demo.domain.usecase.ResolveScannedCodeUseCase
import com.cityride.demo.util.QrPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketLogicTest {

    private class FakeTickets : TicketRepository {
        val state = MutableStateFlow<List<Ticket>>(emptyList())
        override fun observeTickets(): Flow<List<Ticket>> = state
        override fun observeTicket(id: String) = state.map { l -> l.firstOrNull { it.id == id } }
        override suspend fun getTicket(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun getByPayload(payload: String) = state.value.firstOrNull { it.qrPayload == payload }
        override suspend fun insert(ticket: Ticket) { state.value = state.value + ticket }
        override suspend fun update(ticket: Ticket) { state.value = state.value.map { if (it.id == ticket.id) ticket else it } }
    }

    private class FakeUsers(var cents: Int) : UserRepository {
        override fun observeCurrentUser(): Flow<User> = MutableStateFlow(User(1, "Test", "t@t", cents))
        override suspend fun addBalanceCents(amount: Int) { cents += amount }
        override suspend fun chargeCents(amount: Int): Boolean = (cents >= amount).also { if (it) cents -= amount }
    }

    private var now = 1_000_000L
    private val clock = { now }

    @Test
    fun purchaseActivateExpireLifecycle() = runBlocking {
        val tickets = FakeTickets()
        val users = FakeUsers(200)
        val product = Catalog.products.first()

        val result = PurchaseTicketUseCase(tickets, users, clock)(product)
        val ticket = (result as PurchaseResult.Success).ticket
        assertEquals(40, users.cents)
        assertEquals(TicketStatus.NON_ATTIVO, ticket.statusAt(now))

        val active = ActivateTicketUseCase(tickets, clock)(ticket.id, " 163 ")
        assertEquals("163", active.busNumber)
        assertEquals(TicketStatus.ATTIVO, active.statusAt(now))
        assertEquals(60 * 60_000L, active.remainingMillisAt(now))
        assertEquals(ticket.id, QrPayload.parseTicketId(active.qrPayload!!))

        now += 60 * 60_000L
        assertEquals(TicketStatus.SCADUTO, active.statusAt(now))
        assertEquals(0L, active.remainingMillisAt(now))
    }

    @Test
    fun purchaseFailsWithoutBalance() = runBlocking {
        val result = PurchaseTicketUseCase(FakeTickets(), FakeUsers(0), clock)(Catalog.products.first())
        assertEquals(PurchaseResult.InsufficientBalance, result)
    }

    @Test
    fun scanningAnyCodeStartsSixtyMinuteSessionOnce() = runBlocking {
        val tickets = FakeTickets()
        val resolve = ResolveScannedCodeUseCase(tickets, clock)
        val first = resolve("https://example.com/anything", "42")
        assertEquals(TicketStatus.ATTIVO, first.statusAt(now))
        assertEquals("42", first.busNumber)
        assertEquals(60 * 60_000L, first.remainingMillisAt(now))
        assertEquals(first.id, resolve("https://example.com/anything", "42").id)
        assertEquals(1, tickets.state.value.size)
    }

    @Test
    fun scanningIssuedCodeResolvesToItsTicket() = runBlocking {
        val tickets = FakeTickets()
        val bought = (PurchaseTicketUseCase(tickets, FakeUsers(500), clock)(Catalog.products.first()) as PurchaseResult.Success).ticket
        val active = ActivateTicketUseCase(tickets, clock)(bought.id, "7")
        assertEquals(active.id, ResolveScannedCodeUseCase(tickets, clock)(active.qrPayload!!, "").id)
    }

    @Test
    fun tamperedPayloadIsRejected() {
        val payload = QrPayload.create("abc", 123L)
        assertEquals("abc", QrPayload.parseTicketId(payload))
        assertNull(QrPayload.parseTicketId(payload.replace("abc", "xyz")))
        assertNotEquals(QrPayload.create("abc", 123L), QrPayload.create("abc", 124L))
        assertTrue(QrPayload.parseTicketId("random") == null)
    }
}
