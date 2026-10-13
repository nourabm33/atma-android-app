package com.cityride.demo.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.cityride.demo.ui.components.DemoBanner
import com.cityride.demo.ui.theme.CityColors
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/** Inspector scanner: any QR code read by the camera opens the ticket screen. */
@Composable
fun ScannerScreen(onBack: () -> Unit, onScanned: (payload: String, busNumber: String) -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }
    LaunchedEffect(Unit) { if (!hasPermission) launcher.launch(Manifest.permission.CAMERA) }

    var busNumber by rememberSaveable { mutableStateOf("") }
    var manualEntry by remember { mutableStateOf(false) }
    val currentBus by rememberUpdatedState(busNumber)
    val currentOnScanned by rememberUpdatedState(onScanned)
    val handled = remember { AtomicBoolean(false) }

    Column(Modifier.fillMaxSize().background(CityColors.Navy).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Indietro", tint = Color.White)
            }
            Text(
                "Scanner controllore",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(48.dp))
        }
        DemoBanner()
        Column(
            Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).imePadding().padding(20.dp),
        ) {
            OutlinedTextField(
                value = busNumber,
                onValueChange = { input -> busNumber = input.filter { it.isLetterOrDigit() }.take(8).uppercase() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Numero mezzo") },
                placeholder = { Text("es. 163") },
                leadingIcon = { Icon(Icons.Filled.DirectionsBus, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
            )
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color.Black)
                    .border(BorderStroke(3.dp, CityColors.Accent), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (hasPermission) {
                    CameraPreview { payload ->
                        if (handled.compareAndSet(false, true)) currentOnScanned(payload, currentBus)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("Serve il permesso della fotocamera per scansionare.", color = Color.White, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) { Text("Consenti fotocamera") }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Inquadra un QR code: la schermata del ticket si apre automaticamente.",
                color = CityColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { manualEntry = true }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Inserisci codice manualmente", color = CityColors.Navy, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (manualEntry) {
        var code by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { manualEntry = false },
            title = { Text("Codice del ticket") },
            text = {
                OutlinedTextField(value = code, onValueChange = { code = it }, singleLine = true, label = { Text("Contenuto QR") })
            },
            confirmButton = {
                TextButton(
                    enabled = code.isNotBlank(),
                    onClick = {
                        manualEntry = false
                        if (handled.compareAndSet(false, true)) onScanned(code.trim(), busNumber)
                    },
                ) { Text("Verifica") }
            },
            dismissButton = { TextButton(onClick = { manualEntry = false }) { Text("Annulla") } },
        )
    }
}

@Composable
private fun CameraPreview(onQrCode: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    val currentOnQrCode by rememberUpdatedState(onQrCode)

    DisposableEffect(lifecycleOwner) {
        val executor = Executors.newSingleThreadExecutor()
        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build(),
        )
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(executor) { proxy ->
                val media = proxy.image
                if (media == null) {
                    proxy.close()
                    return@setAnalyzer
                }
                val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { codes ->
                        codes.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotBlank) }?.let(currentOnQrCode)
                    }
                    .addOnCompleteListener { proxy.close() }
            }
            runCatching {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            }.onFailure { Log.e("ScannerScreen", "Camera bind failed", it) }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            runCatching { providerFuture.get().unbindAll() }
            scanner.close()
            executor.shutdown()
        }
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}
