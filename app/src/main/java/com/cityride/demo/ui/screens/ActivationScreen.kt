package com.cityride.demo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cityride.demo.domain.model.Ticket
import com.cityride.demo.ui.components.CityRideLogo
import com.cityride.demo.ui.components.DemoBanner
import com.cityride.demo.ui.theme.CityColors

private const val MAX_BUS_NUMBER_LENGTH = 8

@Composable
fun ActivationScreen(ticket: Ticket?, onConfirm: (String) -> Unit) {
    var busNumber by rememberSaveable { mutableStateOf("") }
    val valid = busNumber.isNotBlank()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding(),
    ) {
        DemoBanner()
        Column(Modifier.padding(20.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CityColors.Border),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp)) {
                    CityRideLogo(height = 28.dp, showTagline = false)
                    Spacer(Modifier.height(12.dp))
                    Text(ticket?.name.orEmpty(), color = CityColors.Navy, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(ticket?.validityLabel.orEmpty(), color = CityColors.TextSecondary)
                }
            }
            Spacer(Modifier.height(28.dp))
            Text("Su quale mezzo stai viaggiando?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = CityColors.Navy)
            Spacer(Modifier.height(6.dp))
            Text(
                "Inserisci il numero del mezzo indicato a bordo. Il conto alla rovescia parte subito dopo la conferma.",
                color = CityColors.TextSecondary,
            )
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = busNumber,
                onValueChange = { input ->
                    busNumber = input.filter { it.isLetterOrDigit() }.take(MAX_BUS_NUMBER_LENGTH).uppercase()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Numero mezzo") },
                placeholder = { Text("es. 163") },
                leadingIcon = { Icon(Icons.Filled.DirectionsBus, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { if (valid) onConfirm(busNumber) }),
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = { onConfirm(busNumber) },
                enabled = valid && ticket != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CityColors.Navy),
            ) { Text("Conferma e attiva", fontWeight = FontWeight.Bold, fontSize = 17.sp) }
        }
    }
}
