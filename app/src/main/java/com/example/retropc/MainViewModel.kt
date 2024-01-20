package com.example.retropc

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val model = Model()

    fun loadCode(file: Uri?, contentResolver: ContentResolver) {
        model.uploadCode(file, contentResolver)
    }

    fun getTextRepresentationText(): String = model.getTextScreenContent()

    fun getGraphicRepresentationArray(): Array<Array<Color>> = model.getPixelArray()

    fun resetAndStart() {
        model.cpu.Reset()
    }
}
