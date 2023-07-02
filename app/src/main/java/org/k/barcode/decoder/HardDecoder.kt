package org.k.barcode.decoder

import org.k.barcode.model.CodeDetails

class HardDecoder : BaseDecoder() {
    override fun init(codeDetails: List<CodeDetails>): Boolean {
        return true
    }

    override fun deInit() {

    }

    override fun startDecode() {
        barcodeResultCallback?.onSuccess("https://developer.android.google.cn/jetpack?hl=en".toByteArray(), "QR")
    }

    override fun cancelDecode() {

    }
}