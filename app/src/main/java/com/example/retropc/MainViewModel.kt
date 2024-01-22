package com.example.retropc

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val model = Model()
    var isCodeLoaded by mutableStateOf(false)
    fun loadCode(file: Uri?, contentResolver: ContentResolver) {
        model.uploadCode(file, contentResolver)
        isCodeLoaded = true;
    }

    fun getTextRepresentationText(): String = model.getTextScreenContent()

    fun getGraphicRepresentationArray(): Array<Array<Color>> = model.getPixelArray()

    fun resetAndStart() {
        if (isCodeLoaded) {
            model.executeCpuCode()
            isCodeLoaded = false
        }
    }
}
