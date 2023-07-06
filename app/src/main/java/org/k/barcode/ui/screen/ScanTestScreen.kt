package org.k.barcode.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
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
import org.k.barcode.ui.BarcodeContentViewModel
import org.k.barcode.ui.DecoderViewModel
import org.k.barcode.ui.SettingsViewModel
import org.k.barcode.utils.BarcodeInfoUtils.transformData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun ScanTestScreen(
    paddingValues: PaddingValues,
    settingsViewModel: SettingsViewModel,
    decoderViewModel: DecoderViewModel,
    barcodeContentViewModel: BarcodeContentViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val decoderEvent by decoderViewModel.decoderEvent.observeAsState(initial = DecoderEvent.Closed)
    val settings by settingsViewModel.settings.observeAsState(initial = Settings())
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Clear {
            barcodeContentViewModel.barcodeContent.value = ""
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(4.dp)
                .verticalScroll(scrollState),
            value = barcodeContentViewModel.barcodeContent.value,
            onValueChange = {},
            readOnly = true,
            textStyle = TextStyle(
                lineBreak = LineBreak.Paragraph
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
        DecodeResult(barcodeContentViewModel.barcodeInfo.value)
        Scan(decoderEvent)
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                decoderViewModel.barcode.removeObservers(lifecycleOwner)
                decoderViewModel.reset()
            } else if (event == Lifecycle.Event.ON_RESUME) {
                decoderViewModel.barcode.observe(lifecycleOwner) { barcodeInfo ->
                    barcodeInfo.transformData(settings)?.let {
                        barcodeContentViewModel.barcodeInfo.value = barcodeInfo
                        barcodeContentViewModel.barcodeContent.value =
                            barcodeContentViewModel.barcodeContent.value.toBarcodeContent(it)
                        scope.launch {
                            scrollState.animateScrollTo(barcodeContentViewModel.barcodeContent.value.length)
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

fun String.toBarcodeContent(barcode: String) = this + barcode + "\n"

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
