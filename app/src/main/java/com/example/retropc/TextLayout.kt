package com.example.retropc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(showBackground = true)
@Composable
fun TextLayoutPreview() {
    var txt = ""
    for (i in 0 until 41) txt += "L8D2F7A1B9C4E5036A0D8F4B2C6E590A71F4BACA\n"
    txt += "L8D2F7A1B9C4E5036A0D8F4B2C6E590A71F4BACA"

    TextLayout(viewModel(), txt)
}


@Composable
fun TextLayout(vmodel: MainViewModel, txt: String = "") {
    val vm = vmodel

    Column {
        Header("UI tekstowe")
        TextScreen(text = txt)
    }
}


@Composable
fun TextScreen(text: String) {
    val fontSize = 15.sp
    val characterWidth = 8.dp // Assuming an average character width
    val maxWidth = 80 * characterWidth

    // Text screen size is 40 rows by 42 columns
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .padding(16.dp)
            .background(Color.Black),
    ) {
        Text(
            text = text,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = fontSize,

            modifier = Modifier
                .width(maxWidth)
                .fillMaxHeight()
                .wrapContentHeight()

        )
    }
}