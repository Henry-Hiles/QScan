package com.henryhiles.qscan

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.henryhiles.qscan.components.Camera
import com.henryhiles.qscan.components.alerts.PermissionAlert
import com.henryhiles.qscan.components.alerts.ScannedAlert
import com.henryhiles.qscan.ui.theme.QScanTheme
import com.henryhiles.qscan.utils.Helpers.isURL

const val autoOpenKey = "AUTO_OPEN"

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QScanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var code by remember { mutableStateOf("") }
                    val context = LocalContext.current
                    val activity = context as Activity
                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                    var doNotAsk by remember {
                        mutableStateOf(
                            sharedPref.getBoolean(
                                autoOpenKey,
                                false
                            )
                        )
                    }
                    val uriHandler = LocalUriHandler.current
                    val cameraPermissionState = rememberPermissionState(
                        Manifest.permission.CAMERA
                    )

                    LaunchedEffect(key1 = code) {
                        if (doNotAsk && URLUtil.isValidUrl(code)) {
                            uriHandler.openUri(code)
                            code = ""
                        }
                    }

                    LaunchedEffect(key1 = doNotAsk) {
                        with(sharedPref.edit()) {
                            putBoolean(autoOpenKey, doNotAsk)
                            apply()
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (cameraPermissionState.status.isGranted) {
                            Camera(onScan = { code = it })
                            if (code != "" && !(doNotAsk && isURL(code)))
                                ScannedAlert(
                                    onDismiss = { code = "" },
                                    code = code
                                ) { doNotAsk = it }
                        } else PermissionAlert(
                            textToShow = if (cameraPermissionState.status.shouldShowRationale) "The camera is important for this app. Please grant the permission."
                            else "Camera permission is required for this feature to be available. Please grant the permission.",
                            permissionName = "camera"
                        ) {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }
    }
}