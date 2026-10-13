package com.cityride.demo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Train
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
import com.cityride.demo.domain.model.User
import com.cityride.demo.ui.components.DemoBanner
import com.cityride.demo.ui.components.ListRow
import com.cityride.demo.ui.components.ScreenTitle
import com.cityride.demo.ui.components.SectionLabel
import com.cityride.demo.ui.theme.CityColors
import com.cityride.demo.util.Format

@Composable
fun ProfileScreen(user: User, onTopUp: () -> Unit, onScanner: () -> Unit) {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DemoBanner()
        ScreenTitle("Profilo")
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(96.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Surface(shape = CircleShape, border = BorderStroke(1.dp, CityColors.Border), color = Color.White) {
                    Box(Modifier.size(94.dp), contentAlignment = Alignment.Center) {
                        Text(user.initials(), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = CityColors.TextPrimary)
                    }
                }
            }
            Spacer(Modifier.width(18.dp))
            Column {
                Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "I tuoi dati",
                    color = CityColors.Navy,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = CityColors.SectionGrey,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            onClick = onTopUp,
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = CityColors.Navy, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Borsellino CityRide", fontSize = 16.sp)
                    Text(Format.euro(user.walletCents), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text("Ricarica", color = CityColors.Navy, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = CityColors.Navy)
            }
        }
        SectionLabel("Servizi")
        Group {
            ListRow(Icons.Filled.Train, "Tessere trasporto", showDivider = true) {}
            ListRow(Icons.Filled.QrCodeScanner, "Modalità controllore (scanner)", showDivider = false, onClick = onScanner)
        }
        SectionLabel("Gestione")
        Group {
            ListRow(Icons.Filled.StarBorder, "I miei preferiti", showDivider = true) {}
            ListRow(Icons.Filled.CreditCard, "Metodi di pagamento", showDivider = true) {}
            ListRow(Icons.Filled.Description, "Lista movimenti", showDivider = false) {}
        }
        SectionLabel("Generali")
        Group {
            ListRow(Icons.Filled.Language, "Cambia lingua", showDivider = false) {}
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun Group(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CityColors.Border),
        color = Color.White,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) { Column { content() } }
}

private fun User.initials(): String =
    fullName.split(" ").filter { it.isNotBlank() }.take(2).map { it.first().uppercase() }.joinToString("")
