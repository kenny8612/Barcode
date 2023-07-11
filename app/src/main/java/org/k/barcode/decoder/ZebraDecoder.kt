package org.k.barcode.decoder

import kotlinx.coroutines.flow.Flow
import org.k.barcode.model.BarcodeInfo

class ZebraDecoder : BarcodeDecoder{
    override fun init(): Boolean {
        TODO("Not yet implemented")
    }

    override fun deInit() {
        TODO("Not yet implemented")
    }

    override fun startDecode() {
        TODO("Not yet implemented")
    }

    override fun cancelDecode() {
        TODO("Not yet implemented")
    }

    override fun getBarcodeFlow(): Flow<BarcodeInfo> {
        TODO("Not yet implemented")
    }

    override fun timeout(timeout: Int) {
        TODO("Not yet implemented")
    }

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true
}