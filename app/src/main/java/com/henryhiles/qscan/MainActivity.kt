@file:OptIn(ExperimentalMaterial3Api::class)

package com.henryhiles.qscan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.henryhiles.qscan.ui.theme.QScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QScanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Screen()
                }
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun Screen() {
    var code by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCamPermission = granted }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (hasCamPermission) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = androidx.camera.core.Preview.Builder().build()
                    val selector =
                        CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(previewView.width, previewView.height))
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QrCodeAnalyzer { result -> code = result })

                    try {
                        cameraProviderFuture.get()
                            .bindToLifecycle(lifeCycleOwner, selector, preview, imageAnalysis)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                }, modifier = Modifier
                    .fillMaxSize()
            )
            if (code != "") {
                val uriHandler = LocalUriHandler.current
                val isURL = URLUtil.isValidUrl(code)
//                val sharedPref =
//                    activity.getPreferences(Context.MODE_PRIVATE)
//                with(sharedPref.edit()) {
//                    putBoolean("", true)
//                    apply()
//                }

                AlertDialog(onDismissRequest = { code = "" }) {
                    Surface(
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "QR Code Scanned",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.align(CenterHorizontally)
                            )
                            SelectionContainer {
                                Text(
                                    text = if (isURL) "This QR code will take you to $code, are you sure you want to go there?" else "The content of that QR Code is \"$code\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { code = "" }) {
                                    Text(
                                        text = "Cancel",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                if (isURL)
                                    TextButton(onClick = { uriHandler.openUri(code) }) {
                                        Text(
                                            text = "Open URL",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                            }
                        }
                    }
                }
            }
        } else AlertDialog(onDismissRequest = {}) {
            Surface(
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "No camera permission",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(CenterHorizontally)
                    )
                    SelectionContainer {
                        Text(
                            text = "Camera permission is needed for this app to function.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { }) {
                            Text(
                                text = "Grant Permission",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

    }
}