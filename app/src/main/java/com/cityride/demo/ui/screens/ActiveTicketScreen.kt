package com.cityride.demo.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.domain.model.TicketStatus
import com.cityride.demo.ui.components.CityRideLogo
import com.cityride.demo.ui.components.DemoBanner
import com.cityride.demo.ui.components.DemoWatermark
import com.cityride.demo.ui.components.rememberNow
import com.cityride.demo.ui.theme.CityColors
import com.cityride.demo.util.Format
import kotlin.math.PI
import kotlin.math.sin
import com.cityride.demo.util.QrCodeGenerator

/**
 * Active ticket screen: green while the countdown runs, red with "TICKET EXPIRED" at zero.
 * [inspectorMode] adds the VALID TICKET / EXPIRED verdict shown after a scan.
 */
@Composable
fun ActiveTicketScreen(ticket: Ticket?, inspectorMode: Boolean, playIntro: Boolean = false, onBack: () -> Unit) {
    if (ticket == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Caricamento…") }
        return
    }
    val now = rememberNow()
    val status = ticket.statusAt(now)
    val valid = status == TicketStatus.ATTIVO
    val background by animateColorAsState(if (valid) CityColors.ActiveGreen else CityColors.ExpiredRed, label = "bg")
    val infoColor by animateColorAsState(if (valid) CityColors.ActiveGreenDark else CityColors.ExpiredRedDark, label = "info")
    val qr = remember(ticket.qrPayload) { ticket.qrPayload?.let { QrCodeGenerator.generate(it) } }
    var expanded by rememberSaveable { mutableStateOf(true) }
    var zoom by remember { mutableStateOf(false) }
    var showIntro by rememberSaveable { mutableStateOf(playIntro) }

    Column(Modifier.fillMaxSize().background(background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Indietro", tint = Color.White)
            }
            Text(
                if (valid) "Ticket attivo" else "Ticket scaduto",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(48.dp))
        }
        DemoBanner()
        Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp)) {
            if (inspectorMode || !valid) {
                Verdict(valid, inspectorMode)
                Spacer(Modifier.height(12.dp))
            }
            DemoWatermark {
                Surface(shape = RoundedCornerShape(10.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        CityRideLogo(height = 26.dp, showTagline = false)
                        Spacer(Modifier.height(10.dp))
                        Text(ticket.name, color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(ticket.validityLabel, color = CityColors.TextSecondary, fontSize = 15.sp)
                        Spacer(Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E8EB)),
                            color = Color.White,
                        ) {
                            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                                Row(
                                    Modifier.fillMaxWidth().clickable { expanded = !expanded },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Filled.ConfirmationNumber, contentDescription = null)
                                    Spacer(Modifier.width(10.dp))
                                    Text("Controllo e validazione", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Icon(if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
                                }
                                if (expanded) {
                                    Spacer(Modifier.height(14.dp))
                                    qr?.let {
                                        Image(
                                            bitmap = it,
                                            contentDescription = "QR code del ticket",
                                            filterQuality = FilterQuality.None,
                                            modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f).align(Alignment.CenterHorizontally),
                                        )
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "Ingrandisci QRCode",
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.align(Alignment.CenterHorizontally).clickable { zoom = true },
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Schedule, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            Format.countdown(ticket.remainingMillisAt(now)),
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (valid) CityColors.TextPrimary else CityColors.ExpiredRed,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Icon(Icons.Filled.DirectionsBus, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text(ticket.busNumber ?: "—", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Wave(background)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Surface(shape = RoundedCornerShape(10.dp), color = infoColor, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ticket.route?.let { InfoLine("Tratta", it) }
                    InfoLine("Stato", if (valid) "VALIDO" else "SCADUTO")
                    ticket.activatedAtMillis?.let { InfoLine("Attivato il", Format.dateTime(it)) }
                    ticket.expiresAtMillis?.let { InfoLine("Scade il", Format.dateTime(it)) }
                    InfoLine("Mezzo", ticket.busNumber ?: "—")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        if (showIntro && valid) ActivationIntro(ticket) { showIntro = false }
        }
    }

    if (zoom && qr != null) {
        Dialog(onDismissRequest = { zoom = false }) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, onClick = { zoom = false }) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(qr, contentDescription = "QR code ingrandito", filterQuality = FilterQuality.None, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
                    Spacer(Modifier.height(8.dp))
                    Text("DEMO – NOT A VALID TRAVEL TICKET", fontWeight = FontWeight.Black, color = CityColors.ExpiredRed)
                }
            }
        }
    }
}

@Composable
private fun Verdict(valid: Boolean, inspectorMode: Boolean) {
    Surface(shape = RoundedCornerShape(10.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Text(
            when {
                valid -> "VALID TICKET"
                else -> "TICKET EXPIRED"
            },
            modifier = Modifier.padding(vertical = 18.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = if (valid) CityColors.ActiveGreenDark else CityColors.ExpiredRed,
            fontWeight = FontWeight.Black,
            fontSize = if (inspectorMode) 30.sp else 28.sp,
        )
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Text(
        androidx.compose.ui.text.buildAnnotatedString {
            append("$label: ")
            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
            append(value)
            pop()
        },
        color = Color.White,
        fontSize = 16.sp,
    )
}

/** Wave strip that scrolls continuously; the phase is read only in the draw phase, so it never recomposes. */
@Composable
private fun Wave(color: Color) {
    val transition = rememberInfiniteTransition(label = "wave")
    val phase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 2400, easing = LinearEasing)),
        label = "wavePhase",
    )
    Canvas(Modifier.fillMaxWidth().height(20.dp)) {
        val wavelength = 28.dp.toPx()
        val shift = phase.value * wavelength
        waveLine(wavelength, shift, size.height * 0.38f, color.copy(alpha = 0.22f), 5.dp.toPx())
        waveLine(wavelength, -shift, size.height * 0.62f, color.copy(alpha = 0.5f), 3.dp.toPx())
    }
}

private fun DrawScope.waveLine(wavelength: Float, offset: Float, centerY: Float, color: Color, strokeWidth: Float) {
    val amplitude = size.height * 0.2f
    val step = 3f
    val path = Path()
    var x = 0f
    path.moveTo(0f, centerY + amplitude * sin(offset / wavelength * 2 * PI).toFloat())
    while (x < size.width) {
        x += step
        path.lineTo(x, centerY + amplitude * sin((x + offset) / wavelength * 2 * PI).toFloat())
    }
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
}
