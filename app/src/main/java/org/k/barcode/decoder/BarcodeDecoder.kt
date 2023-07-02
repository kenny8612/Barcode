package org.k.barcode.decoder

import org.k.barcode.model.CodeDetails

interface BarcodeDecoder {
    fun init(codeDetails: List<CodeDetails>): Boolean
    fun deInit()
    fun setBarcodeListener(barcodeResultCallback: BarcodeResultCallback)
    fun startDecode()
    fun cancelDecode()
    fun updateCode(codeDetails: List<CodeDetails>)
    fun light(enable: Boolean)
    fun supportLight(): Boolean
    fun supportCode(): Boolean
}