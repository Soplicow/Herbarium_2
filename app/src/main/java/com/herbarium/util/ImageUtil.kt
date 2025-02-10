package com.herbarium.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import java.io.ByteArrayOutputStream

object ImageUtil {
    fun uriToByteArray(context: Context, uri: Uri, maxSizeKB: Int = 500): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()

                val scale = calculateInSampleSize(options, maxSizeKB)
                val newOptions = BitmapFactory.Options().apply {
                    inSampleSize = scale
                }

                context.contentResolver.openInputStream(uri)?.use { newStream ->
                    val bitmap = BitmapFactory.decodeStream(newStream, null, newOptions)
                    val outputStream = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    outputStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, maxSizeKB: Int): Int {
        val (width, height) = options.run { outWidth to outHeight }
        var inSampleSize = 1

        while ((width * height) / (inSampleSize * inSampleSize) > maxSizeKB * 1024) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    @Composable
    fun ByteArrayImage(
        data: ByteArray?,
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Crop
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .memoryCacheKey(data?.contentHashCode().toString())
                .build(),
            contentDescription = "Plant image",
            modifier = modifier,
            contentScale = contentScale
        )
    }
}