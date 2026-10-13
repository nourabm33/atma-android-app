package com.cityride.demo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cityride.demo.ui.theme.CityColors

/** CityRide brand mark: a stylised bus badge with the wordmark next to it. */
@Composable
fun CityRideLogo(
    modifier: Modifier = Modifier,
    color: Color = CityColors.Navy,
    accent: Color = CityColors.Accent,
    height: Dp = 56.dp,
    showTagline: Boolean = true,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(height)) {
            val s = size.minDimension
            drawRoundRect(color, Offset(s * 0.12f, s * 0.08f), Size(s * 0.76f, s * 0.74f), CornerRadius(s * 0.16f))
            drawRoundRect(Color.White, Offset(s * 0.22f, s * 0.22f), Size(s * 0.56f, s * 0.28f), CornerRadius(s * 0.05f))
            drawRect(accent, Offset(s * 0.22f, s * 0.13f), Size(s * 0.56f, s * 0.05f))
            drawCircle(Color.White, s * 0.06f, Offset(s * 0.32f, s * 0.64f))
            drawCircle(Color.White, s * 0.06f, Offset(s * 0.68f, s * 0.64f))
            drawRoundRect(color, Offset(s * 0.2f, s * 0.8f), Size(s * 0.14f, s * 0.14f), CornerRadius(s * 0.04f))
            drawRoundRect(color, Offset(s * 0.66f, s * 0.8f), Size(s * 0.14f, s * 0.14f), CornerRadius(s * 0.04f))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("City", color = color, fontSize = (height.value * 0.5f).sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                Text("Ride", color = accent, fontSize = (height.value * 0.5f).sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
            }
            if (showTagline) {
                Text("trasporto urbano · demo", color = color, fontSize = (height.value * 0.2f).sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
