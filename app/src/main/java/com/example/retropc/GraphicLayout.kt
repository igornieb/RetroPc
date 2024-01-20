package com.example.retropc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Random

@Composable
fun GraphicLayout(vmodel: MainViewModel) {
    val vm = vmodel

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Postać graficzna")
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center
        ) {
            CanvasWithPixels(colors = vm.getGraphicRepresentationArray())
        }
    }
}

@Composable
fun CanvasWithPixels(colors: Array<Array<Color>>) {
    Canvas(
        modifier = Modifier
            .width(200.dp)
            .height(320.dp)
    ) {
        val size = 15;

        for (i in 0 until colors.size) {
            for (j in 0 until colors[0].size) {
                drawRect(
                    color = colors[i][j],
                    size = Size(width=size.dp.toPx(), height=size.dp.toPx()),
                    topLeft = Offset((j * size).dp.toPx(), (i * size).dp.toPx())
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GraphicLayoutPreview() {
    //, generateRandomColorArray(42, 13)
    GraphicLayout(viewModel())
}

// FOR PREVIEW
fun generateRandomColorArray(rows: Int, columns: Int): Array<Array<Color>> {
    val random = Random()

    fun generateRandomColor(): Color {
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color(red, green, blue)
    }

    return Array(rows) {
        Array(columns) {
            generateRandomColor()
        }
    }
}