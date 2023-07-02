package org.k.barcode.decoder

import org.k.barcode.model.CodeDetails

abstract class BaseDecoder : BarcodeDecoder {
    protected var barcodeResultCallback: BarcodeResultCallback? = null

    override fun setBarcodeListener(barcodeResultCallback: BarcodeResultCallback) {
        this.barcodeResultCallback = barcodeResultCallback
    }

    override fun supportLight(): Boolean = false

    override fun supportCode(): Boolean  = false

    override fun updateCode(codeDetails: List<CodeDetails>) {
    }

    override fun light(enable: Boolean) {
    }
}