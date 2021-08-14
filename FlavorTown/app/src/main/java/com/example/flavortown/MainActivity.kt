package com.example.flavortown

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.createBitmap
import com.example.flavortown.ui.theme.FlavorTownTheme
import io.nayuki.qrcodegen.QrCode
import java.util.*


data class QrImage(val qrCode:ImageBitmap?)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlavorTownTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                    //QrCode(generateQrCode("blah blah", 480))
                    QrCode(generateNayukiQrCode("blah blah"))
                }
            }
        }
    }
}

fun generateQrCode(input: String, dimension: Int): QrImage {
    val qrgEncoder = QRGEncoder(input, null, QRGContents.Type.TEXT, dimension)
    qrgEncoder.colorBlack = Color.RED
    qrgEncoder.colorWhite = Color.BLUE

    return QrImage(qrgEncoder.bitmap.asImageBitmap())
}


fun generateNayukiQrCode(input:String): QrImage {
    // Simple operation
    // Simple operation
    val qr0 = QrCode.encodeText(input, QrCode.Ecc.MEDIUM)
    return QrImage(toImage(qr0, 40, 10))// See QrCodeGeneratorDemo
}

/*---- Utilities ----*/
private fun toImage(qr: QrCode, scale: Int, border: Int): ImageBitmap? {
    return toImage(qr, scale, border, 0xFFFFFF, 0x000000)
}


/**
 * Returns a raster image depicting the specified QR Code, with
 * the specified module scale, border modules, and module colors.
 *
 * For example, scale=10 and border=4 means to pad the QR Code with 4 light border
 * modules on all four sides, and use 10&#xD7;10 pixels to represent each module.
 * @param qr the QR Code to render (not `null`)
 * @param scale the side length (measured in pixels, must be positive) of each module
 * @param border the number of border modules to add, which must be non-negative
 * @param lightColor the color to use for light modules, in 0xRRGGBB format
 * @param darkColor the color to use for dark modules, in 0xRRGGBB format
 * @return a new image representing the QR Code, with padding and scaling
 * @throws NullPointerException if the QR Code is `null`
 * @throws IllegalArgumentException if the scale or border is out of range, or if
 * {scale, border, size} cause the image dimensions to exceed Integer.MAX_VALUE
 */
private fun toImage(
    qr: QrCode,
    scale: Int,
    border: Int,
    lightColor: Int,
    darkColor: Int
): ImageBitmap? {
    Objects.requireNonNull(qr)
    require(!(scale <= 0 || border < 0)) { "Value out of range" }
    require(!(border > Int.MAX_VALUE / 2 || qr.size + border * 2L > Int.MAX_VALUE / scale)) { "Scale or border too large" }
    val bitmapSize = (qr.size + border * 2) * scale
    val result = createBitmap(
        bitmapSize,
        bitmapSize,
        Bitmap.Config.RGB_565
    )

    for (y in 0 until result.getHeight()) {
        for (x in 0 until result.getWidth()) {
            val color = qr.getModule(x / scale - border, y / scale - border)
            result.setPixel(x, y, if (color) darkColor else lightColor)
        }
    }
    return result.asImageBitmap()
}

@Composable
fun QrCode(image: QrImage) {
    image.qrCode?.let {
        Image(it, "", alignment = Alignment.Center, contentScale = ContentScale.Crop)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlavorTownTheme {
        Greeting("Android")
    }
}