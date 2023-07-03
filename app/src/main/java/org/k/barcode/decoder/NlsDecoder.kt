package org.k.barcode.decoder

import android.content.Context
import com.dawn.decoderapijni.SoftEngine
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_SUCC
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_TIMEOUT
import org.k.barcode.model.CodeDetails

class NlsDecoder constructor(private val context: Context) : BaseDecoder(),
    SoftEngine.ScanningCallback {
    private var softEngine: SoftEngine? = null

    override fun init(codeDetails: List<CodeDetails>): Boolean {
        softEngine = SoftEngine.getInstance()
        softEngine?.setNdkSystemLanguage(0)
        val result = softEngine?.initSoftEngine(
            context.getDir(
                "nls_data",
                Context.MODE_PRIVATE
            ).absolutePath
        )
        if (result == true) {
            softEngine?.Open()
            updateCode(codeDetails)
            softEngine?.setScanningCallback(this)
            return true
        }
        return false
    }

    override fun deInit() {
        softEngine?.Close()
        softEngine?.Deinit()
    }

    override fun startDecode() {
        softEngine?.StartDecode()
    }

    override fun cancelDecode() {
        softEngine?.StopDecode()
    }

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true

    override fun updateCode(codeDetails: List<CodeDetails>) {
        codeDetails.forEach {
            when (it.name) {
                Code.Code128.aliasName -> {

                }

                Code.Code11.aliasName -> {

                }

                Code.Code39.aliasName -> {

                }

                Code.Code93.aliasName -> {

                }

                Code.DotCode.aliasName -> {

                }

                Code.EAN8.aliasName -> {

                }

                Code.EAN13.aliasName -> {

                }

                Code.UPC_A.aliasName -> {

                }

                Code.UPC_E.aliasName -> {

                }

                Code.Aztec.aliasName -> {

                }

                Code.CodaBar.aliasName -> {

                }

                Code.CodaBlock.aliasName -> {

                }

                Code.GridMatrix.aliasName -> {

                }

                Code.INT25.aliasName -> {

                }

                Code.HanXin.aliasName -> {

                }

                Code.MSI.aliasName -> {

                }

                Code.MaxiCode.aliasName -> {

                }

                Code.MicroPDF.aliasName -> {

                }

                Code.Matrix25.aliasName -> {

                }

                Code.Telepen.aliasName -> {

                }

                Code.QR.aliasName -> {

                }

                Code.PDF417.aliasName -> {

                }

                Code.DataMatrix.aliasName -> {

                }
            }
        }
    }

    override fun light(enable: Boolean) {

    }

    override fun onScanningCallback(
        eventCode: Int,
        type: Int,
        code: ByteArray?,
        length: Int
    ): Int {
        when (eventCode) {
            SCN_EVENT_DEC_SUCC -> {
                code?.let {
                    val buffer = ByteArray(length - 128)
                    System.arraycopy(it, 128, buffer, 0, length - 128)
                    barcodeResultCallback?.onSuccess(buffer, String(it, 0, 3))
                }
            }
            SCN_EVENT_DEC_TIMEOUT -> {
                barcodeResultCallback?.onTimeout()
            }
            else -> {
                barcodeResultCallback?.onCancel()
            }
        }
        return 0
    }
}