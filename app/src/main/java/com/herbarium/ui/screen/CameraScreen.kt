package com.herbarium.ui.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.herbarium.util.CameraUtil
import java.io.File

@Composable
fun CameraScreen(navController: NavController) {
    var hasPermission by remember { mutableStateOf(false) }

    CameraUtil.RequestCameraPermission(
        onPermissionGranted = { hasPermission = true },
        onPermissionDenied = { navController.popBackStack() }
    )

    if (hasPermission) {
        CameraPreview(navController)
    }
}

@Composable
fun CameraPreview(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }

    LaunchedEffect(Unit) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageCaptureInstance = ImageCapture.Builder().build()
            imageCapture.value = imageCaptureInstance

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCaptureInstance
                )
            } catch (exc: Exception) {
                Log.e("CameraScreen", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Button(
            onClick = {
                takePicture(navController, imageCapture.value, context)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Take Photo")
        }
    }
}

fun takePicture(navController: NavController, imageCapture: ImageCapture?, context: android.content.Context) {
    if (imageCapture == null) {
        Log.e("CameraScreen", "ImageCapture is not initialized.")
        Toast.makeText(context, "Camera not ready", Toast.LENGTH_SHORT).show()
        return
    }

    val photoFile = File(context.externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                Log.d("CameraScreen", "Photo saved: $savedUri")

                navController.previousBackStackEntry?.savedStateHandle?.set("photoUri", savedUri.toString())

                navController.popBackStack()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed: ${exception.message}", exception)
                Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
