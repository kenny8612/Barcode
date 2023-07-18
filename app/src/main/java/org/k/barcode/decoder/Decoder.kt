package org.k.barcode.decoder

import kotlinx.coroutines.flow.Flow
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails

interface Decoder {
    fun init(): Boolean
    fun deInit()
    fun startDecode()
    fun cancelDecode()
    fun getBarcode(): Flow<BarcodeInfo>
    fun updateCode(codeDetails: List<CodeDetails>) {}
    fun light(enable: Boolean) {}
    fun lightLevel(lightLevel: LightLevel) {}
    fun timeout(timeout: Int)
    fun supportLight(): Boolean = false
    fun supportLightLevel(): Boolean = false
    fun supportCode(): Boolean = false
}