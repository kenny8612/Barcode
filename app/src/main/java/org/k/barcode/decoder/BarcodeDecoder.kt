package org.k.barcode.decoder

interface BarcodeDecoder {
    fun init(): Boolean
    fun deInit()
    fun setBarcodeListener(barcodeResultCallback: BarcodeResultCallback)
    fun startDecode()
    fun cancelDecode()
}