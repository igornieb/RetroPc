package com.example.retropc

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun ConfigLayout(vm: MainViewModel, activity: Activity) {
    Column {
        Header("Konfiguracja")
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            UploadRow(activity)
            StartRow(vm)
        }
    }
}

fun openFile(activity: Activity) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE)
        .setType("*/*")

    startActivityForResult(activity, intent, 111, null)
}

@Composable
fun UploadRow(activity: Activity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("Wybierz plik z kodem:")
        }

        // Right column with button
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Button(
                modifier = Modifier.width(100.dp),
                onClick = { openFile(activity) }
            ) {
                Text("Upload")
            }
        }
    }
}

@Composable
fun StartRow(vm: MainViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("Rozpocznij pracę CPU:")
        }

        // Right column with button
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Button(
                modifier = Modifier
                    .width(100.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (vm.isCodeLoaded) Color.Green else Color.Gray
                ),
                onClick = { vm.resetAndStart() }
            ) {
                Text("Start")
            }
        }
    }
}
