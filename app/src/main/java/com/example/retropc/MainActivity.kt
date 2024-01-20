package com.example.retropc

import android.content.Intent
import android.os.Bundle
import android.telephony.MbmsDownloadSession.RESULT_CANCELLED
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {
    val vm: MainViewModel by viewModels()

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
            vm.loadCode(selectedFile, contentResolver)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    Pager(MainActivity())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pager(activity: MainActivity) {
    val vm: MainViewModel = activity.vm

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
