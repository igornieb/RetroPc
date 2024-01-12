package com.example.retropc

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val model = Model()
    var tempoSliderPosition = mutableStateOf(0f)

    fun loadCode(file: Uri?, contentResolver: ContentResolver) {
        model.setCode(file, contentResolver)
    }

    fun getTextUIText(): String = model.getTextScreenContent()

    fun getGraphicUIArray(): Array<Array<Color>> = model.getPixelArray()
}
