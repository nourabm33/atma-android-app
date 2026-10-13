# CityRide Demo

Native Android demo of a city transit-ticket app (Kotlin, Jetpack Compose). **Demo only.** Every ticket screen shows a "DEMO – NOT A VALID TRAVEL TICKET" banner and watermark. It isn't connected to any real transport operator.

## Features
- **Home**: logo, Biglietti / Abbonamenti shortcuts, trip search, stops and timetables, a QR-scan link, notices carousel, news.
- **Acquista**: ticket catalog; buying charges the demo wallet and adds the ticket to *I miei ticket*.
- **I miei ticket**: Disponibili / Scaduti tabs with Non attivo / Attivo / Scaduto status and an **Attiva** button.
- **Activation**: you type the bus number; the app generates a unique signed QR code and starts the validity timer.
- **Active ticket**: ticket name, QR code (with zoom), bus number, activation and expiry time, live countdown. Green while valid; turns red with **TICKET EXPIRED** at zero.
- **Inspector scanner** (Profilo → Modalità controllore): real-time CameraX + ML Kit QR scanning, with manual code entry as a fallback. Codes issued by this app open their ticket. Any other QR starts a 60-minute demo session. The screen shows VALID TICKET (green) or TICKET EXPIRED (red), plus the bus number.
- **Profilo**: user info, demo wallet with top-up, settings-style menu.

## Architecture
```
domain/   models (Ticket, TicketStatus, User), repository interfaces, use cases
data/     Room database (tickets, users), repository implementations
ui/       Compose screens, theme, components, AppViewModel, navigation
util/     QR generation (ZXing), signed QR payload, formatting
```
Status is computed from the clock (`Ticket.statusAt(now)`), so it stays correct across app restarts.

## Build
Requires JDK 17 and the Android SDK (compileSdk 35).
```
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # domain unit tests
```
