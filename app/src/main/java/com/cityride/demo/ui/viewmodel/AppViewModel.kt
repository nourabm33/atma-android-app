package com.cityride.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cityride.demo.AppContainer
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.domain.model.TicketProduct
import com.cityride.demo.domain.model.User
import com.cityride.demo.domain.usecase.PurchaseResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val container: AppContainer) : ViewModel() {

    val tickets: StateFlow<List<Ticket>> = container.ticketRepository.observeTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val user: StateFlow<User> = container.userRepository.observeCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), User(0, "Utente Demo", "demo@cityride.app", 0))

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages = _messages

    fun ticket(id: String): StateFlow<Ticket?> = container.ticketRepository.observeTicket(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun purchase(product: TicketProduct) = viewModelScope.launch {
        when (container.purchaseTicket(product)) {
            is PurchaseResult.Success ->
                _messages.emit("${product.name} aggiunto a I miei ticket")
            PurchaseResult.InsufficientBalance ->
                _messages.emit("Credito insufficiente: ricarica il borsellino dal Profilo")
        }
    }

    fun activate(ticketId: String, busNumber: String, onActivated: (String) -> Unit) = viewModelScope.launch {
        runCatching { container.activateTicket(ticketId, busNumber) }
            .onSuccess { onActivated(it.id) }
            .onFailure { _messages.emit(it.message ?: "Attivazione non riuscita") }
    }

    fun resolveScan(payload: String, busNumber: String, onResolved: (String) -> Unit) = viewModelScope.launch {
        val ticket = container.resolveScannedCode(payload, busNumber)
        onResolved(ticket.id)
    }

    fun topUp(cents: Int = 1000) = viewModelScope.launch {
        container.userRepository.addBalanceCents(cents)
        _messages.emit("Borsellino ricaricato")
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(container) as T
        }
    }
}
