package org.k.barcode.decoder

import org.k.barcode.model.CodeDetails

class NlsDecoder : BaseDecoder() {
    override fun init(codeDetails: List<CodeDetails>): Boolean {
        updateCode(codeDetails)
        return true
    }

    override fun deInit() {

    }

    override fun startDecode() {
        barcodeResultCallback?.onSuccess("https://developer.android.google.cn/jetpack?hl=en".toByteArray(), "QR")
    }

    override fun cancelDecode() {

    }

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true

    override fun updateCode(codeDetails: List<CodeDetails>) {
        codeDetails.forEach {
            when (it.name) {
                Code.Code128.route -> {

                }

                Code.Code11.route -> {

                }

                Code.Code39.route -> {

                }

                Code.DotCode.route -> {

                }

                Code.EAN8.route -> {

                }

                Code.EAN13.route -> {

                }

                Code.UPC_A.route -> {

                }

                Code.UPC_E.route -> {

                }

                Code.Aztec.route -> {

                }

                Code.CodaBar.route -> {

                }

                Code.Codablock.route -> {

                }

                Code.GM.route -> {

                }

                Code.Gs1_128.route -> {

                }

                Code.Int25.route -> {

                }

                Code.HanXin.route -> {

                }

                Code.MSI.route -> {

                }

                Code.Maxicode.route -> {

                }

                Code.MicroPDF.route -> {

                }

                Code.RSS.route -> {

                }

                Code.Matrix25.route -> {

                }

                Code.Telepen.route -> {

                }

                Code.QR.route -> {

                }

                Code.PDF417.route -> {

                }
            }
        }
    }

    override fun light(enable: Boolean) {

    }
}