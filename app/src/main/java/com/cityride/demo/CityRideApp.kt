package com.cityride.demo

import android.app.Application
import com.cityride.demo.data.local.AppDatabase
import com.cityride.demo.data.repository.TicketRepositoryImpl
import com.cityride.demo.data.repository.UserRepositoryImpl
import com.cityride.demo.domain.repository.TicketRepository
import com.cityride.demo.domain.repository.UserRepository
import com.cityride.demo.domain.usecase.ActivateTicketUseCase
import com.cityride.demo.domain.usecase.PurchaseTicketUseCase
import com.cityride.demo.domain.usecase.ResolveScannedCodeUseCase

class CityRideApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(app: Application) {
    private val database = AppDatabase.build(app)
    val ticketRepository: TicketRepository = TicketRepositoryImpl(database.ticketDao())
    val userRepository: UserRepository = UserRepositoryImpl(database.userDao())
    val purchaseTicket = PurchaseTicketUseCase(ticketRepository, userRepository)
    val activateTicket = ActivateTicketUseCase(ticketRepository)
    val resolveScannedCode = ResolveScannedCodeUseCase(ticketRepository)
}
