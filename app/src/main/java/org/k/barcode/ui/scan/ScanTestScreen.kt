package org.k.barcode.ui.scan

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.k.barcode.BarcodeService
import org.k.barcode.R
import org.k.barcode.decoder.BarcodeListener
import org.k.barcode.model.BarcodeInfo

@Composable
fun ScanTest(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val barcodeList = remember { mutableStateListOf<String>() }
    var barcode by remember {
        mutableStateOf(
            BarcodeInfo(
                "".toByteArray(),
                aim = "",
                decodeTime = 0L
            )
        )
    }
    var scanTotal by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            BarcodeList(barcodeList)
        }
        DecodeResult(barcode, scanTotal)
        Clear(barcodeList)
    }

    DisposableEffect(Unit) {
        val barcodeInfoListener = object : BarcodeListener {
            override fun onBarcode(barcodeInfo: BarcodeInfo) {
                barcodeInfo.formatData?.let { barcodeList.add(it) }
                barcode = barcodeInfo
                scanTotal++
            }
        }

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val barcodeServiceBind = service as BarcodeService.BarcodeServiceBind
                barcodeServiceBind.addBarcodeInfoListener(barcodeInfoListener)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                context.unbindService(connection)
            } else if (event == Lifecycle.Event.ON_RESUME) {
                context.bindService(
                    Intent(context, BarcodeService::class.java),
                    connection, Context.BIND_AUTO_CREATE
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun BarcodeList(barcodeList: SnapshotStateList<String>) {
    val scrollerLazyStata = rememberLazyListState()
    val insert by remember {
        derivedStateOf {
            scrollerLazyStata.layoutInfo.totalItemsCount == barcodeList.size && barcodeList.isNotEmpty()
        }
    }

    if (insert) {
        LaunchedEffect(Unit) {
            scrollerLazyStata.scrollToItem(scrollerLazyStata.layoutInfo.totalItemsCount)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = scrollerLazyStata,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(barcodeList) {
            Text(
                text = it,
                softWrap = true
            )
        }
    }
}

@Composable
fun Clear(barcodeList: SnapshotStateList<String>) {
    Button(
        onClick = {
            if (barcodeList.isNotEmpty())
                barcodeList.clear()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(text = stringResource(id = R.string.clean), fontSize = 18.sp)
    }
}

@Composable
fun DecodeResult(barcodeInfo: BarcodeInfo, scanTotal: Long) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(35.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.barcode_info_value, barcodeInfo.aim),
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.decode_time_value, barcodeInfo.decodeTime),
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier,
                text = stringResource(R.string.decode_total_value, scanTotal),
                fontWeight = FontWeight.Medium
            )
        }
    }
}