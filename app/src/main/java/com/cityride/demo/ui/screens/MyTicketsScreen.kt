package com.cityride.demo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.domain.model.TicketStatus
import com.cityride.demo.ui.components.CityRideLogo
import com.cityride.demo.ui.components.DemoBanner
import com.cityride.demo.ui.components.ScreenTitle
import com.cityride.demo.ui.components.SectionLabel
import com.cityride.demo.ui.components.StatusChip
import com.cityride.demo.ui.components.rememberNow
import com.cityride.demo.ui.theme.CityColors
import com.cityride.demo.util.Format

@Composable
fun MyTicketsScreen(
    tickets: List<Ticket>,
    onActivate: (Ticket) -> Unit,
    onOpen: (Ticket) -> Unit,
) {
    val now = rememberNow()
    var tab by remember { mutableIntStateOf(0) }
    val active = tickets.filter { it.statusAt(now) == TicketStatus.ATTIVO }
    val inactive = tickets.filter { it.statusAt(now) == TicketStatus.NON_ATTIVO }
    val expired = tickets.filter { it.statusAt(now) == TicketStatus.SCADUTO }

    Column(Modifier.fillMaxSize()) {
        DemoBanner()
        ScreenTitle("I miei ticket")
        TabRow(
            selectedTabIndex = tab,
            containerColor = Color.White,
            contentColor = CityColors.Navy,
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(positions[tab]),
                    height = 6.dp,
                    color = CityColors.Navy,
                )
            },
            divider = {},
        ) {
            listOf("DISPONIBILI", "SCADUTI").forEachIndexed { i, label ->
                Tab(
                    selected = tab == i,
                    onClick = { tab = i },
                    text = {
                        Text(
                            label,
                            fontSize = 18.sp,
                            fontWeight = if (tab == i) FontWeight.Bold else FontWeight.Normal,
                            color = if (tab == i) CityColors.Navy else CityColors.TextSecondary,
                        )
                    },
                )
            }
        }
        LazyColumn(
            Modifier.fillMaxSize().padding(top = 0.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (tab == 0) {
                if (active.isNotEmpty()) {
                    item { SectionHeader("Ticket attivi") }
                    items(active, key = { it.id }) { TicketCard(it, now, onActivate, onOpen) }
                }
                item { SectionHeader("Ticket non attivi") }
                if (inactive.isEmpty()) item { EmptyText("Nessun ticket da attivare. Acquistane uno dalla sezione Acquista.") }
                items(inactive, key = { it.id }) { TicketCard(it, now, onActivate, onOpen) }
            } else {
                item { SectionHeader("Ticket scaduti") }
                if (expired.isEmpty()) item { EmptyText("Nessun ticket scaduto.") }
                items(expired, key = { it.id }) { TicketCard(it, now, onActivate, onOpen) }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Box(Modifier.fillMaxWidth().padding(top = 0.dp)) {
        Surface(color = CityColors.SectionGrey, modifier = Modifier.fillMaxWidth()) {
            SectionLabel(text, Modifier.padding(vertical = 6.dp))
        }
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text,
        Modifier.fillMaxWidth().padding(24.dp),
        textAlign = TextAlign.Center,
        color = CityColors.TextSecondary,
    )
}

@Composable
private fun TicketCard(ticket: Ticket, now: Long, onActivate: (Ticket) -> Unit, onOpen: (Ticket) -> Unit) {
    val status = ticket.statusAt(now)
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CityColors.Border),
        color = Color.White,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        onClick = { if (status != TicketStatus.NON_ATTIVO) onOpen(ticket) },
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CityRideLogo(height = 28.dp, showTagline = false, modifier = Modifier.weight(1f))
                when (status) {
                    TicketStatus.NON_ATTIVO -> StatusChip("Non attivo", CityColors.PendingBg, CityColors.PendingText)
                    TicketStatus.ATTIVO -> StatusChip("Attivo", Color(0xFFD7F5E0), CityColors.ActiveGreenDark)
                    TicketStatus.SCADUTO -> StatusChip("Scaduto", Color(0xFFFBDADA), CityColors.ExpiredRedDark)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(ticket.name, color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = CityColors.Border)
            Row(Modifier.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    when (status) {
                        TicketStatus.NON_ATTIVO -> buildAnnotatedString {
                            append("Valido ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(durationLabel(ticket.durationMinutes)) }
                        }
                        TicketStatus.ATTIVO -> buildAnnotatedString {
                            append("Scade tra ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Format.countdown(ticket.remainingMillisAt(now))) }
                        }
                        TicketStatus.SCADUTO -> buildAnnotatedString {
                            append("Scaduto il ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Format.dateTime(ticket.expiresAtMillis ?: now)) }
                        }
                    },
                    fontSize = 17.sp,
                )
            }
            HorizontalDivider(color = CityColors.Border)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (status) {
                    TicketStatus.NON_ATTIVO -> Button(
                        onClick = { onActivate(ticket) },
                        modifier = Modifier.width(180.dp).height(54.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CityColors.Navy),
                    ) { Text("Attiva", fontWeight = FontWeight.Bold, fontSize = 17.sp) }
                    else -> Button(
                        onClick = { onOpen(ticket) },
                        modifier = Modifier.width(180.dp).height(54.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == TicketStatus.ATTIVO) CityColors.ActiveGreen else CityColors.ExpiredRed,
                        ),
                    ) { Text("Mostra", fontWeight = FontWeight.Bold, fontSize = 17.sp) }
                }
                Spacer(Modifier.weight(1f))
                Text(Format.euro(ticket.priceCents), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

private fun durationLabel(minutes: Int): String = when {
    minutes % (24 * 60) == 0 -> "${minutes / (24 * 60) * 24} ore"
    else -> "$minutes minuti"
}
