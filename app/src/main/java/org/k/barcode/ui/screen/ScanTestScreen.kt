package org.k.barcode.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.k.barcode.R
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.ui.ShareViewModel
import org.k.barcode.utils.BarcodeInfoUtils.transformData
import kotlin.random.Random

@Composable
fun ScanTestScreen(
    paddingValues: PaddingValues,
    shareViewModel: ShareViewModel,
    onNavigateToCodeSettings: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = LocalClipboardManager.current
    val decoderEvent by shareViewModel.decoderEvent.collectAsState()
    val settings by shareViewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        Card(
            modifier = Modifier.padding(4.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (DecoderManager.instance.supportCode() &&
                    decoderEvent != DecoderEvent.Error
                ) {
                    IconButton(
                        onClick = {
                            onNavigateToCodeSettings.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_code_settings),
                            contentDescription = null
                        )
                    }
                }
                IconButton(
                    onClick = {
                        if (shareViewModel.barcodeList.isNotEmpty()) {
                            shareViewModel.barcodeList.clear()
                            shareViewModel.barcodeInfo.value = BarcodeInfo()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
                IconButton(
                    onClick = {
                        if (shareViewModel.barcodeList.isNotEmpty()) {
                            val content = shareViewModel.barcodeList.joinToString("\n")
                            clipboardManager.setText(AnnotatedString(content))
                        }
                    }
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_content_copy), null)
                }
            }
        }

        BarcodeList(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f),
            shareViewModel.barcodeList
        )
        DecodeResult(shareViewModel.barcodeInfo.value)
        Scan(decoderEvent) {
            EventBus.getDefault().post(MessageEvent(Message.StartDecode))
        }
    }

    DisposableEffect(Unit) {
        var barcodeJob: Job? = null
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                barcodeJob?.cancel()
            } else if (event == Lifecycle.Event.ON_RESUME) {
                if (barcodeJob?.isActive == true) return@LifecycleEventObserver

                barcodeJob = lifecycleOwner.lifecycleScope.launch {
                    shareViewModel.barcodeFlow.collect {
                        it.transformData(settings)?.let { data ->
                            shareViewModel.barcodeList.add(data)
                            if (shareViewModel.barcodeList.size > 1000)
                                shareViewModel.barcodeList.clear()
                            shareViewModel.barcodeInfo.value = it
                        }
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

@OptIn(ExperimentalTextApi::class)
@Composable
fun BarcodeList(
    modifier: Modifier,
    barcodeList: SnapshotStateList<String>
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
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(barcodeList) { index, item ->
                    Text(
                        text = item,
                        style = TextStyle(fontSize = 16.sp, lineBreak = LineBreak.Paragraph),
                        color = if (index == barcodeList.size - 1) Color(
                            red = Random.nextInt(256),
                            green = Random.nextInt(256),
                            blue = Random.nextInt(256)
                        ) else Color.Black
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
fun Scan(decoderEvent: DecoderEvent, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
        enabled = decoderEvent == DecoderEvent.Opened
    ) {
        Text(
            text = stringResource(id = R.string.scan),
            fontSize = 18.sp
        )
    }
}
