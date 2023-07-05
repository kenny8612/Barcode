package org.k.barcode.data

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.decoder.DecoderEventListener
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.model.BarcodeInfo
import javax.inject.Inject

class DecoderRepository @Inject constructor(private val decoderManager: DecoderManager) {
    fun getEvent(): Flow<DecoderEvent> = callbackFlow {
        val callback = object : DecoderEventListener {
            override fun onEvent(decoderEvent: DecoderEvent) {
                trySend(decoderEvent)
            }
        }
        decoderManager.addEventListener(callback)
        awaitClose {
            decoderManager.removeEventListener(callback)
        }
    }

    fun getBarcode(): Flow<BarcodeInfo> = decoderManager.getBarcodeFlow()
}