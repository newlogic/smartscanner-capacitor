package com.newlogic.mlkitlibrary

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.googlecode.tesseract.android.TessBaseAPI
import com.newlogic.mlkitlib.innovatrics.mrz.MrzParser
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import kotlin.math.roundToInt

class TesseractAnalyzer(
    private var onResult: ((String) -> Unit)?,
    private var onGetState: (() -> Boolean)?,
    private var onStat: ((Long, Long) -> Unit)?,
    private var tessBaseAPI: TessBaseAPI?,
    private var debugPath: String?
) : ImageAnalysis.Analyzer {

    fun Image.toBitmap(rotation: Int): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()

        var rect: Rect =  Rect()
        if (rotation == 90 || rotation == 270) {
            rect.left = (this.width / 2.5).roundToInt()
            rect.top = 0
            rect.right = this.width - rect.left
            rect.bottom = this.height
        } else {
            rect.left = 0
            rect.top = (this.height / 2.5).roundToInt()
            rect.right = this.width
            rect.bottom = this.height - rect.top
        }

        Log.d(TAG, "Image ${this.width}x${this.height}, crop to: ${rect.left},${rect.top},${rect.right},${rect.bottom}")

        yuvImage.compressToJpeg(rect, 100, out) // Ugly but it works
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (!onGetState!!()) {
            imageProxy.close()
            return
        }

        var startTime = System.currentTimeMillis()

        val mediaImage = imageProxy.image
        if (mediaImage != null) {

            val b = mediaImage.toBitmap(imageProxy.imageInfo.rotationDegrees)
            Log.d(TAG, "Vision Image: (${mediaImage.width}, ${mediaImage.height}) Cropped: (${b.width}, ${b.height}), Rotation: ${imageProxy.imageInfo.rotationDegrees}")

//                // DEBUG: Write images to storage
//                val fname = "MRZ-TESS-$startTime.jpg"
//                val file = File(debugPath, fname)
//                if (file.exists()) file.delete()
//                try {
//                    val out = FileOutputStream(file)
//                    b.compress(Bitmap.CompressFormat.JPEG, 90, out)
//                    out.flush()
//                    out.close()
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//                Log.d(TAG, "Saved image: $debugPath/$fname")

                // Tesseract
                val matrix = Matrix()
                matrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                val rotatedBitmap = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)
                tessBaseAPI!!.setImage(rotatedBitmap)
                var tessResult = tessBaseAPI!!.utF8Text
                Log.d(TAG, "tessResult          : ${URLEncoder.encode(tessResult, "UTF-8")}")

                tessResult = tessResult.replace(" ", "")
                    .replace("«", "<")
                    .replace("<c<", "<<<")
                    .replace("<E<", "<<<") // Good idea? Maybe not.
                    .replace("<K<", "<<<") // Good idea? Maybe not.
                    .replace(" ", "")
                    .replace("\n\n", "\n")


                // Delete everything until there is a P, I, A or C
                tessResult = tessResult.replace(Regex("^[^PIAC]*"), "")


                if (tessResult.contains("<") && (
                            tessResult.startsWith("P<") ||
                                    tessResult.startsWith("I") ||
                                    tessResult.startsWith("A") ||
                                    tessResult.startsWith("C"))
                ) {
                    tessResult = tessResult.toUpperCase()

                    if (tessResult.startsWith("P<") && tessResult.length > 89) {
                        tessResult = tessResult.slice(IntRange(0, 89))
                    } else if ((tessResult.startsWith("I") ||
                        tessResult.startsWith("A") ||
                        tessResult.startsWith("C")) && tessResult.length > 92) {
                        tessResult = tessResult.slice(IntRange(0, 92))
                    }

                    Log.d(TAG, "tessResult (cleanup): ${URLEncoder.encode(tessResult, "UTF-8")}")



                    try {
                        var record = MrzParser.parse(tessResult)

                        record.givenNames = record.givenNames
                            .replace('0', 'O')
                            .replace("1", "I")
                            .replace("8", "B")
                            .replace("5", "S")
                            .replace("2", "Z")

                        record.surname = record.surname
                            .replace('0', 'O')
                            .replace("1", "I")
                            .replace("8", "B")
                            .replace("5", "S")
                            .replace("2", "Z")

                        record.issuingCountry = record.issuingCountry
                            .replace('0', 'O')
                            .replace("1", "I")
                            .replace("8", "B")
                            .replace("5", "S")
                            .replace("2", "Z")

                        record.nationality = record.nationality
                            .replace('0', 'O')
                            .replace("1", "I")
                            .replace("8", "B")
                            .replace("5", "S")
                            .replace("2", "Z")


                        var mrzRecord = record.toMrz()
                        onResult?.invoke(mrzRecord)
                    } catch (e: Exception) { // MrzParseException, IllegalArgumentException
                        Log.d(TAG, "MRZ error: " + e.toString())
//                                onResult?.invoke("")
                    }
                } else {
                    Log.d(TAG, "Not starting with P, I, A or C.")
//                            onResult?.invoke("")
                }
        }
        onStat?.invoke(startTime, System.currentTimeMillis())
        imageProxy.close()
    }

    companion object {
        private const val TAG = "CameraXBasic"
    }
}