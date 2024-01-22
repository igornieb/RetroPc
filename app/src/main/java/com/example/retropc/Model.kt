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
    val ROWS = 36u
    val COLUMNS = 39u // 24 bit color -> 3 Bytes for each pixel -> columns should be divisible by 3
    init {
        cpu.ConnectBus(bus)
        cpu.Reset()
    }

    fun executeCpuCode() {
        cpu.Reset() // reset cpu
        while (true) {
            cpu.Clock() // execute instruction
            if (cpu.opCode==0)
            {
                break   // stops execution when there is no more instructions to execute
            }
        }
    }

    fun getTextScreenContent(): String {
        var sb = StringBuilder()

        val start: UInt = 0x0200u;

        for (i in 0u until ROWS) {
            for (j in 0u until COLUMNS) {
                sb.append(bus.Read(start + i * COLUMNS + j))
            }
            sb.append("\n")
        }
        for (j in 0u until COLUMNS) {
            sb.append(bus.Read(start + ROWS * COLUMNS + j))
        }

        return sb.toString()
    }

    fun uploadCode(uri: Uri?, contentResolver: ContentResolver) {
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
        val start = 0x0200u
        val intRows = ROWS.toInt()
        val intColumns = COLUMNS.toInt() / 3
        val pixels: Array<Array<Color>> = Array(intRows) { Array(intColumns) { Color.Black } }

        var currentPixel = start

        for (i in 0 until intRows) {
            for (j in 0 until intColumns) {
                pixels[i][j] = Color(
                    bus.Read(currentPixel++).toInt(),
                    bus.Read(currentPixel++).toInt(),
                    bus.Read(currentPixel++).toInt()
                )
            }
        }
        return pixels
    }
}