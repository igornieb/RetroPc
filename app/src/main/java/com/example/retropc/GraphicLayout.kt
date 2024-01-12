package com.example.retropc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(showBackground = true)
@Composable
fun GraphicLayoutPreview() {
    GraphicLayout(viewModel())
}


@Composable
fun GraphicLayout(vmodel: MainViewModel) {
    val vm = vmodel
    Column {
        Header("UI graficzne")
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CanvasWithPixels(colors = vm.getGraphicUIArray())
        }
    }
}

@Composable
fun CanvasWithPixels(colors: Array<Array<Color>>) {
    Canvas(
        modifier = Modifier.width(200.dp).height(320.dp)
    ) {
        val size = 1.5;

        for (i in 0 until 320) {
            for (j in 0 until 200) {
                drawRect(
                    color = colors[i][j],
                    size = Size(width=size.dp.toPx(), height=size.dp.toPx()),
                    topLeft = Offset((j * size).dp.toPx(), (i * size).dp.toPx())
                )
            }
        }
    }

}