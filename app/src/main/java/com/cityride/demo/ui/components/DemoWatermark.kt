package com.cityride.demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val DEMO_NOTICE = "DEMO – NOT A VALID TRAVEL TICKET"

/** Full-width banner that marks every ticket screen as a demo. */
@Composable
fun DemoBanner(modifier: Modifier = Modifier) {
    Text(
        text = DEMO_NOTICE,
        modifier = modifier.fillMaxWidth().background(Color(0xFFFFD600)).padding(vertical = 6.dp),
        textAlign = TextAlign.Center,
        color = Color.Black,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp,
    )
}

/** One large diagonal "DEMO" mark drawn over its content. */
@Composable
fun DemoWatermark(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier) {
        content()
        Text(
            text = "DEMO",
            modifier = Modifier.matchParentSize().wrapContentSize(Alignment.Center).rotate(-30f),
            color = Color(0x2ED32F2F),
            fontWeight = FontWeight.Black,
            fontSize = 96.sp,
            maxLines = 1,
            softWrap = false,
        )
    }
}
