package org.k.barcode.decoder

enum class DecoderEvent {
    Opened,
    Closed,
    Error,
    DecodeCancel,
    DecodeTimeout
}