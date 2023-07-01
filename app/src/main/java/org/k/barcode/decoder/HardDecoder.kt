package org.k.barcode.decoder

class HardDecoder : BaseDecoder() {
    override fun init(): Boolean {
        return true
    }

    override fun deInit() {

    }

    override fun startDecode() {
        Thread.sleep(10)
        barcodeResultCallback?.onSuccess("https://developer.android.google.cn/jetpack?hl=en".toByteArray(), "QR")
    }

    override fun cancelDecode() {

    }
}