package org.k.barcode.decoder

import android.content.Context
import com.dawn.decoderapijni.SoftEngine
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_SUCC
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_TIMEOUT
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails

class NlsDecoder constructor(private val context: Context) : BarcodeDecoder {
    private var softEngine: SoftEngine? = null

    private val flow = callbackFlow {
        val callback =
            SoftEngine.ScanningCallback { eventCode, _, code, length ->
                when (eventCode) {
                    SCN_EVENT_DEC_SUCC -> {
                        code?.let {
                            val buffer = ByteArray(length - 128)
                            System.arraycopy(it, 128, buffer, 0, length - 128)
                            trySend(BarcodeInfo(buffer, String(it, 0, 3)))
                        }
                    }

                    SCN_EVENT_DEC_TIMEOUT -> {
                        trySend(BarcodeInfo(decodeTime = -1))
                    }

                    else -> {
                        trySend(BarcodeInfo())
                    }
                }
                0
            }
        SoftEngine.getInstance()?.setScanningCallback(callback)
        awaitClose {

        }
    }

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

    override fun getBarcodeFlow(): Flow<BarcodeInfo> = flow

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
}