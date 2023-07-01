package org.k.barcode.decoder

abstract class BaseDecoder : BarcodeDecoder {
    protected var barcodeResultCallback: BarcodeResultCallback? = null

    override fun setBarcodeListener(barcodeResultCallback: BarcodeResultCallback) {
        this.barcodeResultCallback = barcodeResultCallback
    }
}