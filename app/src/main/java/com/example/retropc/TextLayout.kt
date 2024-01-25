package com.example.retropc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(showBackground = true)
@Composable
fun TextLayoutPreview() {
//    var txt = ""
//    for (i in 0 until 36) txt += "L8D2F7A1B9C4E5036A0D8F4B2C6E590A71F4BAC\n"
//    txt += "L8D2F7A1B9C4E5036A0D8F4B2C6E590A71F4BAC"

    TextLayout(viewModel())
}


@Composable
fun TextLayout(vm: MainViewModel) {

    Column {
        Header("Postać tekstowa")
        TextScreen(text = vm.getTextRepresentationText())
    }
}


@Composable
fun TextScreen(text: String) {
    val fontSize = 13.sp
    val characterWidth = 8.dp
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
            letterSpacing = 2.sp,
            modifier = Modifier
                .width(maxWidth)
                .fillMaxHeight()
                .wrapContentHeight()

        )
    }
}