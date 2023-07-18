package org.k.barcode.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.model.BarcodeInfo
import javax.inject.Inject

class DecoderRepository @Inject constructor(private val decoderManager: DecoderManager) {
    fun getEvent(): StateFlow<DecoderEvent> = decoderManager.getEvent()

    fun getBarcode(): Flow<BarcodeInfo> = decoderManager.getBarcode()
}