package com.cityride.demo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.ui.components.CityRideLogo
import com.cityride.demo.ui.theme.CityColors
import com.cityride.demo.util.Format
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** One-time activation sequence (card slide-in, check mark, details) played over the ticket after an Attiva scan. */
@Composable
fun ActivationIntro(ticket: Ticket, onDone: () -> Unit) {
    val done by rememberUpdatedState(onDone)
    val card = remember { Animatable(0f) }
    val badge = remember { Animatable(0f) }
    val tick = remember { Animatable(0f) }
    val details = remember { Animatable(0f) }
    val exit = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        card.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
        launch { badge.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 300f)) }
        delay(150)
        tick.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
        details.animateTo(1f, tween(400))
        delay(900)
        exit.animateTo(1f, tween(400, easing = FastOutLinearInEasing))
        done()
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 1f - exit.value }
            .background(CityColors.ActiveGreen)
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = (1f - card.value) * 600.dp.toPx()
                    alpha = card.value
                    val grow = 1f + 0.06f * exit.value
                    scaleX = grow
                    scaleY = grow
                },
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CityRideLogo(height = 26.dp, showTagline = false)
                Spacer(Modifier.height(20.dp))
                Canvas(Modifier.size(88.dp).graphicsLayer { scaleX = badge.value; scaleY = badge.value }) {
                    drawCircle(CityColors.ActiveGreen)
                    val path = Path().apply {
                        moveTo(size.width * 0.28f, size.height * 0.53f)
                        lineTo(size.width * 0.44f, size.height * 0.68f)
                        lineTo(size.width * 0.73f, size.height * 0.37f)
                    }
                    val measure = PathMeasure().apply { setPath(path, false) }
                    val drawn = Path().also { measure.getSegment(0f, measure.length * tick.value, it, true) }
                    drawPath(drawn, Color.White, style = Stroke(7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    Modifier.fillMaxWidth().graphicsLayer {
                        alpha = details.value
                        translationY = (1f - details.value) * 16.dp.toPx()
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Ticket attivato", color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text(ticket.name, color = CityColors.TextSecondary, fontSize = 15.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(14.dp))
                    ticket.busNumber?.let { Detail("Mezzo", it) }
                    ticket.activatedAtMillis?.let { Detail("Attivato il", Format.dateTime(it)) }
                    ticket.expiresAtMillis?.let { Detail("Scade il", Format.dateTime(it)) }
                }
            }
        }
    }
}

@Composable
private fun Detail(label: String, value: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text("$label: ", color = CityColors.TextSecondary, fontSize = 15.sp)
        Text(value, color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
