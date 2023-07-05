package org.k.barcode.ui.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.k.barcode.R
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.Settings
import org.k.barcode.ui.DecoderViewModel
import org.k.barcode.ui.SettingsViewModel
import org.k.barcode.utils.BarcodeInfoUtils.transformData

@Composable
fun ScanTestScreen(
    paddingValues: PaddingValues,
    snackBarHostState: SnackbarHostState,
    settingsViewModel: SettingsViewModel,
    decoderViewModel: DecoderViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = LocalClipboardManager.current
    val barcodeList = remember { mutableStateListOf<String>() }
    val decoderEvent by decoderViewModel.decoderEvent.observeAsState(initial = DecoderEvent.Closed)
    val settings by settingsViewModel.settings.observeAsState(initial = Settings())
    var barcodeInfo by remember { mutableStateOf(BarcodeInfo()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Clear {
            barcodeList.clear()
        }
        BarcodeList(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f),
            barcodeList = barcodeList
        ) {
            clipboardManager.setText(AnnotatedString(it))
            scope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.copy_to_clipboard))
            }
        }
        DecodeResult(barcodeInfo)
        Scan(decoderEvent)
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                decoderViewModel.barcode.removeObservers(lifecycleOwner)
            } else if (event == Lifecycle.Event.ON_RESUME) {
                decoderViewModel.barcode.observe(lifecycleOwner) {
                    val formatData = it.transformData(settings)
                    if (formatData != null) {
                        barcodeList.add(formatData)
                        barcodeInfo = it
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun Clear(onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(
            text = stringResource(id = R.string.clear),
            fontSize = 18.sp
        )
    }
}

@Composable
fun BarcodeList(
    modifier: Modifier,
    barcodeList: SnapshotStateList<String>,
    onClick: (String) -> Unit
) {
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

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        if (barcodeList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = scrollerLazyStata,
                contentPadding = PaddingValues(8.dp)
            ) {
                items(barcodeList) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onClick(it)
                            }, text = it
                    )
                }
            }
        }
    }
}

@Composable
fun DecodeResult(barcodeInfo: BarcodeInfo) {
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
                text = stringResource(id = R.string.barcode_aim_value, barcodeInfo.aim ?: "N/A"),
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.decode_time_value, barcodeInfo.decodeTime),
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier,
                text = stringResource(
                    R.string.barcode_length_value,
                    barcodeInfo.sourceData?.size ?: 0
                ),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun Scan(decoderEvent: DecoderEvent) {
    Button(
        onClick = {
            EventBus.getDefault().post(MessageEvent(Message.StartDecode))
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        enabled = decoderEvent != DecoderEvent.Error && decoderEvent != DecoderEvent.Closed
    ) {
        Text(
            text = stringResource(id = R.string.scan),
            fontSize = 18.sp
        )
    }
}
