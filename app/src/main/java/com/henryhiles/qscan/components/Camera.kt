package com.henryhiles.qscan.components

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.henryhiles.qscan.QrCodeAnalyzer

@Composable
fun Camera(onScan: (result: String) -> Unit) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(lifeCycleOwner as Context)
    }

    AndroidView(
        factory = { context ->

            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector =
                CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                QrCodeAnalyzer(onScan)
            )

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
}