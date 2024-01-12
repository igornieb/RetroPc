package com.example.retropc

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import java.io.BufferedReader
import java.io.InputStreamReader

class Model {
    val cpu = Cpu6502()
    val bus = Bus()

    init {
        cpu.ConnectBus(bus)
        cpu.Reset()
    }

    fun getTextScreenContent(): String {
        var sb = StringBuilder()

        val start: UInt = 0x2000u;
        // Screen memory (~16kB)
//        val end: UInt = 0x5FFFu;

        // Text screen dimensions are 40x42
        for (i in 0u until 42u) {
            for (j in 0u until 40u) {
                sb.append(bus.Read(start + i * 40u + j))
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    fun setCode(uri: Uri?, contentResolver: ContentResolver) {
        uri?.let {

            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val reader = BufferedReader(InputStreamReader(stream))
                var content: String? = reader.readText()

                content?.let {
                    Log.d("FILE_CONTENT", content)
                    bus.LoadInstructions(0x00FFu, content)
                }
            }
        }
    }

    fun getPixelArray(): Array<Array<Color>> {
        val start = 0x2000u
        val pixels: Array<Array<Color>> = Array(320) { Array(200) { Color.Black } }

        var currentPixel = start
        var color = 0
        var red = 0
        var green = 0
        var blue = 0

        for (i in 0 until 320) {
            for (j in 0 until 200) {

                // Gets 16 bit color value and converts it to RGB
                color = (bus.Read(currentPixel++).toInt() shl 16) or bus.Read(currentPixel++).toInt()
                red = (color and 0xF800) shr 8
                green = (color and 0x07E0) shr 3
                blue = (color and 0x001F) shl 3

                pixels[i][j] = Color(red, green, blue)
            }
        }
        return pixels
    }
}