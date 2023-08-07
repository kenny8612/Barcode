package org.k.barcode.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.greenrobot.eventbus.EventBus
import org.k.barcode.R
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.ui.ShareViewModel

@Composable
fun ScanTestScreen(
    paddingValues: PaddingValues,
    viewModel: ShareViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val decoderEvent by viewModel.decoderEvent.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        BarcodeView(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f),
            barcodeInfo = viewModel.barcode.value
        )
        Scan(decoderEvent) {
            EventBus.getDefault().post(MessageEvent(Message.StartDecode))
        }
    }

    DisposableEffect(Unit) {
        var barcodeJob: Job? = null
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                barcodeJob = viewModel.getBarcode().onEach { barcode ->
                    barcode.sourceData?.also { viewModel.barcode.value = barcode }
                }.launchIn(lifecycleOwner.lifecycleScope)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                barcodeJob?.cancel()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun BarcodeView(modifier: Modifier, barcodeInfo: BarcodeInfo) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.Center),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                barcodeInfo.formatData?.also {
                    SelectionContainer {
                        Text(
                            text = it,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomStart)
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                barcodeInfo.sourceData?.also {
                    Text(
                        modifier = Modifier,
                        text = stringResource(
                            id = R.string.barcode_aim_value,
                            barcodeInfo.aim ?: "N/A"
                        ),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        modifier = Modifier,
                        text = stringResource(
                            id = R.string.decode_time_value,
                            barcodeInfo.decodeTime
                        ),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        modifier = Modifier,
                        text = stringResource(
                            R.string.barcode_length_value,
                            barcodeInfo.sourceData.size
                        ),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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
