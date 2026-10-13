package com.cityride.demo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cityride.demo.domain.model.Catalog
import com.cityride.demo.domain.model.TicketProduct
import com.cityride.demo.ui.components.CityRideLogo
import com.cityride.demo.ui.components.DemoBanner
import com.cityride.demo.ui.components.ScreenTitle
import com.cityride.demo.ui.components.SectionLabel
import com.cityride.demo.ui.theme.CityColors
import com.cityride.demo.util.Format

@Composable
fun AcquistaScreen(onPurchase: (TicketProduct) -> Unit) {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DemoBanner()
        ScreenTitle("Acquista")
        Row(
            Modifier.fillMaxWidth().background(CityColors.SectionGrey).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Città Demo", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Text(
                "Cambia città",
                color = CityColors.Navy,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textDecoration = TextDecoration.Underline,
            )
        }
        SectionLabel("Suggeriti per te")
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(Catalog.products.size) { index ->
                ProductCard(Catalog.products[index], onPurchase)
            }
        }
        Spacer(Modifier.height(24.dp))
        Column(Modifier.fillMaxWidth().background(CityColors.SectionGrey).padding(bottom = 20.dp)) {
            SectionLabel("Servizi in città")
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CityColors.Border),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp).width(160.dp),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Icon(Icons.Filled.DirectionsBus, contentDescription = null, tint = CityColors.Navy)
                    Spacer(Modifier.height(12.dp))
                    Text("Trasporto pubblico", fontSize = 16.sp)
                }
            }
        }
        SectionLabel("Organizza un viaggio")
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CityColors.Border),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Train, contentDescription = null, tint = CityColors.Navy)
                Spacer(Modifier.width(16.dp))
                Text("Treni", fontSize = 17.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CityColors.Border),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Confronta le soluzioni di viaggio", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = CityColors.Navy)
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun ProductCard(product: TicketProduct, onPurchase: (TicketProduct) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CityColors.Border),
        color = Color.White,
        modifier = Modifier.width(300.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            CityRideLogo(height = 30.dp, showTagline = false)
            Spacer(Modifier.height(14.dp))
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CityColors.TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(product.validityLabel, color = CityColors.TextSecondary, fontSize = 15.sp)
            product.route?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, color = CityColors.TextSecondary, fontSize = 14.sp)
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = CityColors.Border)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { onPurchase(product) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CityColors.Navy),
            ) {
                Text("Acquista  ·  ${Format.euro(product.priceCents)}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
    }
}
