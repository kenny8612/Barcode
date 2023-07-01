package org.k.barcode.decoder

interface DecoderEventListener {
    fun onEvent(decoderEvent: DecoderEvent)
}