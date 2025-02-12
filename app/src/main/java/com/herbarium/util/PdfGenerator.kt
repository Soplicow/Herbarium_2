package com.herbarium.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import com.herbarium.data.model.Plant
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {
    fun generatePlantPdf(context: Context, plant: Plant): File? {
        return try {
            val document = PdfDocument()

            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)

            val canvas = page.canvas
            var yPos = 50f

            val titlePaint = android.graphics.Paint().apply {
                textSize = 24f
                color = android.graphics.Color.BLACK
            }
            canvas.drawText("Plant Export: ${plant.name}", 50f, yPos, titlePaint)
            yPos += 50

            // Image
            plant.photo?.let { byteArray ->
                try {
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)

                    // Calculate scaling
                    val maxWidth = 500f // 500 points ~ 17.6cm
                    val scaleFactor = maxWidth / options.outWidth
                    val scaledWidth = maxWidth
                    val scaledHeight = options.outHeight * scaleFactor

                    // Decode properly scaled bitmap
                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = calculateInSampleSize(options, maxWidth.toInt())
                    }
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, decodeOptions)

                    // Fix orientation if needed
                    val orientedBitmap = rotateBitmapIfRequired(byteArray, bitmap)

                    // Draw image centered
                    val xPos = (595 - scaledWidth) / 2
                    canvas.drawBitmap(
                        Bitmap.createScaledBitmap(orientedBitmap, scaledWidth.toInt(), scaledHeight.toInt(), true),
                        xPos,
                        yPos,
                        null
                    )
                    yPos += scaledHeight + 30
                } catch (e: Exception) {
                    Log.e("PDFGenerator", "Error processing image", e)
                }
            }

            val textPaint = android.graphics.Paint().apply {
                textSize = 14f
                color = android.graphics.Color.DKGRAY
            }

            listOf(
                "Description: ${plant.description}",
                "Latitude: ${plant.location?.get("latitude")}, Longitude: ${plant.location?.get("longitude")}",

            ).forEach { line ->
                canvas.drawText(line, 50f, yPos, textPaint)
                yPos += 30
            }

            document.finishPage(page)

            val fileName = "plant_${plant.name}_${System.currentTimeMillis()}.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            document.writeTo(FileOutputStream(file))
            document.close()

            file
        } catch (e: Exception) {
            Log.e("PdfGenerator", "Error generating PDF", e)
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int): Int {
        val width = options.outWidth
        var inSampleSize = 1

        if (width > reqWidth) {
            val halfWidth = width / 2
            while (halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun rotateBitmapIfRequired(byteArray: ByteArray, bitmap: android.graphics.Bitmap): android.graphics.Bitmap {
        return try {
            val inputStream = ByteArrayInputStream(byteArray)
            val exif = ExifInterface(inputStream)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            if (!matrix.isIdentity) {
                return android.graphics.Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
            }
            bitmap
        } catch (e: Exception) {
            bitmap
        }
    }
}