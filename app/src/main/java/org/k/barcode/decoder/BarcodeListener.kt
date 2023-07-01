package org.k.barcode.decoder

import org.k.barcode.model.BarcodeInfo

interface BarcodeListener {
    fun onBarcode(barcodeInfo: BarcodeInfo)
}