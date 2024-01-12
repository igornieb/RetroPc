package com.example.retropc

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(showBackground = true)
@Composable
fun PreviewConfig() {
    ConfigLayout(viewModel(), activity = MainActivity())
}

@Composable
fun ConfigLayout(vmodel: MainViewModel, activity: Activity) {
    val vm = vmodel

    Column {
        Header("Konfiguracja")
        Spacer(modifier = Modifier.height(40.dp))

        // Body
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            Text(text = "Wybierz plik z kodem: ")
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = { openFile(activity) })
            {
                Text(text = "Upload")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            Text(text = "Wybierz opóźnienie: ")
            Spacer(modifier = Modifier.width(10.dp))
            TempoSlider(vm.tempoSliderPosition)
        }
        Menu()
    }
}

fun openFile(activity: Activity) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE)
        .setType("*/*")

    startActivityForResult(activity, intent, 111, null)
}

@Composable
fun TempoSlider(sliderPosition: MutableState<Float>) {
    Column(
        modifier = Modifier.width(200.dp)
    ) {
        Slider(
            value = sliderPosition.value,
            onValueChange = { sliderPosition.value = it},
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 4,
            valueRange = 0f..1500f
        )
        Text(text = sliderPosition.value.toInt().toString() + "ms")

    }
}