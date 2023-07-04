package org.k.barcode.decoder

import kotlinx.coroutines.flow.Flow
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails

interface BarcodeDecoder {
    fun init(codeDetails: List<CodeDetails>): Boolean
    fun deInit()
    fun startDecode()
    fun cancelDecode()
    fun getBarcodeFlow():Flow<BarcodeInfo>
    fun updateCode(codeDetails: List<CodeDetails>){}
    fun light(enable: Boolean){}
    fun supportLight(): Boolean = false
    fun supportCode(): Boolean = false
}