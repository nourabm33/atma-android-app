package com.cityride.demo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cityride.demo.ui.theme.CityColors
import kotlinx.coroutines.delay

/** Current wall-clock time, refreshed every second. */
@Composable
fun rememberNow(): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000L - now % 1000L)
        }
    }
    return now
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
        textAlign = TextAlign.Center,
        color = CityColors.Navy,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier.padding(horizontal = 20.dp, vertical = 14.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp,
        color = CityColors.TextPrimary,
    )
}

@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CityColors.Border),
        color = Color.White,
        onClick = onClick ?: {},
        enabled = onClick != null,
    ) { content() }
}

@Composable
fun IconBubble(icon: ImageVector, tint: Color = CityColors.Navy, background: Color = CityColors.SectionGrey) {
    Box(
        Modifier.size(48.dp).clip(CircleShape).background(background),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp)) }
}

/** One row of a grouped settings-style list with a leading icon and a chevron. */
@Composable
fun ListRow(icon: ImageVector, label: String, showDivider: Boolean, onClick: () -> Unit) {
    Column {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = CityColors.Navy, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Text(label, fontSize = 17.sp, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
        if (showDivider) HorizontalDivider(color = CityColors.Border)
    }
}

@Composable
fun StatusChip(text: String, background: Color, foreground: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(6.dp)).background(background).padding(horizontal = 10.dp, vertical = 6.dp),
    ) { Text(text, color = foreground, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
}

@Composable
fun Dots(count: Int, selected: Int, activeColor: Color = Color.White, inactiveColor: Color = Color.White.copy(alpha = 0.5f)) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { i ->
            Box(
                Modifier.size(if (i == selected) 14.dp else 9.dp).clip(CircleShape)
                    .background(if (i == selected) activeColor else inactiveColor),
            )
        }
    }
}
