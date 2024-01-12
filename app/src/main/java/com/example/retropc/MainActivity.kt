package com.example.retropc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.MbmsDownloadSession.RESULT_CANCELLED
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pager(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode != RESULT_CANCELLED) {
            val selectedFile = data?.data // The URI with the location of the file
            readFile(selectedFile)
            vm.loadCode(selectedFile, contentResolver)

        }
    }

    // DEBUG PURPOSES
    private fun readFile(fileUri: Uri?) {
        fileUri?.let {
            contentResolver.openInputStream(it)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append('\n')
                }

                val fileContent = stringBuilder.toString()
                Log.d("FILE_CONTENT_DEBUG", fileContent)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun prviewMain() {
    Pager(MainActivity())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pager(activity: MainActivity) {
    val vm: MainViewModel = viewModel()

    val pagerState = rememberPagerState()
    HorizontalPager(
        state = pagerState,
        pageCount = 3,
        pageSize = PageSize.Fill,
    ) { page ->
        when (page) {
            0 -> ConfigLayout(vm, activity)
            1 -> TextLayout(vm)
            2 -> GraphicLayout(vm)
        }
    }
}

@Composable
fun Menu() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .height(50.dp)
                .align(CenterVertically),
            onClick = {}
        ) {
            Text(text = "Start")
        }
    }
}