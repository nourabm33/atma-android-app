package com.cityride.demo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object CityColors {
    val Navy = Color(0xFF0B4F8A)
    val NavyDark = Color(0xFF083A66)
    val Accent = Color(0xFFF5A623)
    val Surface = Color(0xFFFFFFFF)
    val SectionGrey = Color(0xFFF0F3F7)
    val Border = Color(0xFFC9CED6)
    val TextPrimary = Color(0xFF1B1F24)
    val TextSecondary = Color(0xFF6B717A)
    val ActiveGreen = Color(0xFF1FA34A)
    val ActiveGreenDark = Color(0xFF15803A)
    val ExpiredRed = Color(0xFFD32F2F)
    val ExpiredRedDark = Color(0xFFA91F1F)
    val PendingBg = Color(0xFFFFE8CC)
    val PendingText = Color(0xFF9A5200)
}

private val colors = lightColorScheme(
    primary = CityColors.Navy,
    onPrimary = Color.White,
    secondary = CityColors.Accent,
    background = CityColors.Surface,
    surface = CityColors.Surface,
    onSurface = CityColors.TextPrimary,
    outline = CityColors.Border,
)

private val typography = Typography(
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
)

@Composable
fun CityRideTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, content = content)
}
