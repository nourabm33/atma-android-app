package com.cityride.demo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cityride.demo.ui.components.CityRideLogo
import com.cityride.demo.ui.components.DemoBanner
import com.cityride.demo.ui.components.Dots
import com.cityride.demo.ui.components.SectionLabel
import com.cityride.demo.ui.theme.CityColors

private data class Notice(val date: String, val title: String, val body: String)

private val notices = listOf(
    Notice(
        "18 settembre 2026",
        "Sciopero regionale di 24 ore – 23 settembre 2026",
        "Possibili variazioni al servizio di Trasporto Pubblico Locale per l'intera giornata.",
    ),
    Notice(
        "12 settembre 2026",
        "Linea 2: deviazione per lavori in Via Marconi",
        "Dal 15 al 30 settembre le corse deviano su Via Verdi; fermate temporanee attive.",
    ),
    Notice(
        "2 settembre 2026",
        "Nuovo orario scolastico dal 16 settembre",
        "Corse aggiuntive sulle linee urbane negli orari di entrata e uscita.",
    ),
)

@Composable
fun HomeScreen(
    onTickets: () -> Unit,
    onSubscriptions: () -> Unit,
    onScanner: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var noticeIndex by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DemoBanner()
        Column(Modifier.fillMaxWidth().background(CityColors.Navy).padding(20.dp)) {
            CityRideLogo(color = Color.White, height = 52.dp)
            Spacer(Modifier.height(22.dp))
            NavyCardRow(Icons.Filled.ConfirmationNumber, "Biglietti", onTickets)
            Spacer(Modifier.height(12.dp))
            NavyCardRow(Icons.Filled.CalendarMonth, "Abbonamenti", onSubscriptions)
        }

        Column(Modifier.fillMaxWidth().background(Color.White).padding(20.dp)) {
            Text("Viaggia con noi", color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Dove vuoi andare?", color = CityColors.TextSecondary) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                )
                Spacer(Modifier.width(10.dp))
                CircleIconButton(Icons.Filled.Home)
                Spacer(Modifier.width(8.dp))
                CircleIconButton(Icons.Filled.Work)
            }
            Spacer(Modifier.height(22.dp))
            Text("Fermate e orari", color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CityColors.Border),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = CityColors.Navy)
                    Spacer(Modifier.width(14.dp))
                    Text("Consulta orari", fontWeight = FontWeight.Bold, color = CityColors.Navy, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sei in una fermata CityRide?", fontSize = 15.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Scansiona il QR",
                    color = CityColors.Navy,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickableNoRipple(onScanner),
                )
            }
        }

        Column(Modifier.fillMaxWidth().background(CityColors.Navy).padding(vertical = 20.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Avvisi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.weight(1f))
                Text("Vedi tutti", color = Color.White, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.height(14.dp))
            val notice = notices[noticeIndex]
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    .clickableNoRipple { noticeIndex = (noticeIndex + 1) % notices.size },
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(notice.date, color = CityColors.TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(notice.body, color = CityColors.TextPrimary, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Dots(notices.size, noticeIndex)
            }
        }

        Column(Modifier.fillMaxWidth().background(CityColors.SectionGrey)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("News", color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.weight(1f))
                Text("Vedi tutte", color = CityColors.Navy, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = CityColors.Navy)
            }
            SectionLabel("Servizio estivo")
            Text(
                "Dal 1 giugno entrano in servizio le corse per il litorale con frequenza ogni 20 minuti.",
                Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp),
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun NavyCardRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).background(CityColors.SectionGrey, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = CityColors.Navy, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = CityColors.Navy, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = CityColors.Navy)
        }
    }
}

@Composable
private fun CircleIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        Modifier.size(48.dp).background(Color.White, CircleShape)
            .padding(1.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(shape = CircleShape, border = androidx.compose.foundation.BorderStroke(1.5.dp, CityColors.Navy), color = Color.White) {
            Box(Modifier.size(46.dp), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = CityColors.Navy)
            }
        }
    }
}

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick,
    )
