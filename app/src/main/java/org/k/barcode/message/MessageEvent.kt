package org.k.barcode.message

data class MessageEvent(
    val message: Message,
    val arg1: Any? = null
)
