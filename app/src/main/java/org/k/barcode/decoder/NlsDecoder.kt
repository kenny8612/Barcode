package org.k.barcode.decoder

import android.content.Context
import com.dawn.decoderapijni.SoftEngine
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_SUCC
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_TIMEOUT
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails

class NlsDecoder constructor(private val context: Context) :
    BarcodeDecoder {
    private var softEngine: SoftEngine = SoftEngine.getInstance()
    private var startTime = 0L

    @OptIn(DelicateCoroutinesApi::class)
    private val flow = callbackFlow {
        val callback =
            SoftEngine.ScanningCallback { eventCode, _, param2, length ->
                when (eventCode) {
                    SCN_EVENT_DEC_SUCC -> {
                        val buffer = ByteArray(length - 128)
                        System.arraycopy(param2, 128, buffer, 0, length - 128)
                        for ((index, value) in param2.withIndex()) {
                            if (value.toInt() == 0) {
                                trySend(
                                    BarcodeInfo(
                                        sourceData = buffer,
                                        aim = String(param2, 0, index),
                                        decodeTime = System.currentTimeMillis() - startTime
                                    )
                                )
                                break
                            }
                        }
                    }

                    SCN_EVENT_DEC_TIMEOUT -> {
                        trySend(BarcodeInfo(decodeTime = System.currentTimeMillis() - startTime))
                    }

                    else -> {
                        trySend(BarcodeInfo())
                    }
                }
                0
            }
        softEngine.setScanningCallback(callback)
        awaitClose {

        }
    }.shareIn(GlobalScope, started = SharingStarted.WhileSubscribed(), replay = 0)

    init {
        softEngine.setNdkSystemLanguage(0)
    }

    override fun init(): Boolean {
        val result = softEngine.initSoftEngine(
            context.getDir(
                "nls_data",
                Context.MODE_PRIVATE
            ).absolutePath
        )
        if (!result) return false

        return softEngine.Open()
    }

    override fun deInit() {
        softEngine.Close()
        softEngine.Deinit()
    }

    override fun startDecode() {
        startTime = System.currentTimeMillis()
        softEngine.StartDecode()
    }

    override fun cancelDecode() {
        softEngine.StopDecode()
    }

    override fun getBarcodeFlow(): Flow<BarcodeInfo> = flow

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true

    override fun updateCode(codeDetails: List<CodeDetails>) {
        codeDetails.forEach {
            when (it.name) {
                Code.DotCode.aliasName -> set("DOTCODE", "Enable", enable(it.enable))
                Code.EAN8.aliasName -> elanCode("EAN8", it)
                Code.EAN13.aliasName -> elanCode("EAN13", it)
                Code.UPC_A.aliasName -> upcCode("UPCA", it)
                Code.UPC_E.aliasName -> upcCode("UPCE", it)
                Code.Code11.aliasName -> lct("CODE11", it)
                Code.Matrix25.aliasName -> lct("MATRIX25", it)
                Code.Code128.aliasName -> minMaxLength("CODE128", it)
                Code.Code49.aliasName -> minMaxLength("CODE49", it)
                Code.Code93.aliasName -> minMaxLength("CODE93", it)
                Code.Aztec.aliasName -> minMaxLength("AZTEC", it)
                Code.MaxiCode.aliasName -> minMaxLength("MAXIC", it)
                Code.MicroPDF.aliasName -> minMaxLength("MICROPDF", it)
                Code.PDF417.aliasName -> minMaxLength("PDF417", it)
                Code.DataMatrix.aliasName -> minMaxLength("DM", it)
                Code.QR.aliasName -> minMaxLength("QR", it)
                Code.HanXin.aliasName -> minMaxLength("CSC", it)
                Code.GridMatrix.aliasName -> minMaxLength("GM", it)
                Code.UCC_EAN128.aliasName -> minMaxLength("UCCEAN128", it)
                Code.CodaBar.aliasName -> {
                    minMaxLength("CODEBAR", it)
                    if (it.enable)
                        set("CODEBAR", "TrsmtStasrtStop", enable(it.startStopCharacters))
                }

                Code.Code39.aliasName -> {
                    lct("CODE39", it)
                    if (it.enable)
                        set("CODE39", "FullAscii", enable(it.fullAscii))
                }

                Code.INT25.aliasName -> {
                    lct("ITF", it)
                    lct("IND25", it)
                    set("ITF6", "Enable", enable(it.enable))
                    transmitCheckDigit("ITF6", it.transmitCheckDigit)
                    set("ITF14", "Enable", enable(it.enable))
                    transmitCheckDigit("ITF14", it.transmitCheckDigit)
                }

                Code.ISBN.aliasName -> {
                    set("ISBN", "Enable", enable(it.enable))
                    if (it.enable) {
                        set("ISBN", "Digit2", enable(it.supplemental2))
                        set("ISBN", "Digit5", enable(it.supplemental5))
                    }
                }

                Code.Composite.aliasName -> {
                    set("COMPOSITE", "Enable", enable(it.enable))
                }

                Code.MSI.aliasName -> {
                    lct("MSIPLSY", it)
                    if (it.enable)
                        set("MSIPLSY", "ChkMode", it.algorithm.toString())
                }
                Code.RSS.aliasName -> set("RSS", "Enable", enable(it.enable))
                Code.AustraliaPost.aliasName -> set("AUSPOST", "Enable", enable(it.enable))
                Code.ChinaPost.aliasName -> lct("CHNPOST", it)
                Code.JapanesePost.aliasName -> {
                    set("JAPANPOST", "Enable", enable(it.enable))
                    transmitCheckDigit("JAPANPOST", it.transmitCheckDigit)
                }
            }
        }
    }

    override fun light(enable: Boolean) {
        softEngine.illuminationEnable = if (enable) 1 else 0
    }

    override fun timeout(timeout: Int) {
        softEngine.setScanTimeout(timeout)
    }

    private fun set(codeName: String, params: String, value: String) {
        softEngine.ScanSet(codeName, params, value)
    }

    private fun enable(value: Boolean) = if (value) "1" else "0"

    private fun transmitCheckDigit(codeName: String, value: Boolean) {
        set(codeName, "TrsmtChkChar", enable(value))
    }

    private fun elanCode(codeName: String, codeDetails: CodeDetails) {
        set(codeName, "Enable", enable(codeDetails.enable))
        if (codeDetails.enable) {
            transmitCheckDigit(codeName, codeDetails.transmitCheckDigit)
            set(codeName, "Digit2", enable(codeDetails.supplemental2))
            set(codeName, "Digit5", enable(codeDetails.supplemental5))
        }
    }

    private fun upcCode(codeName: String, codeDetails: CodeDetails) {
        elanCode(codeName, codeDetails)
        if (codeDetails.enable) {
            when (codeDetails.upcPreamble) {
                0 -> {
                    set(codeName, "SysData", "0")
                    set(codeName, "UsSysData", "0")
                    set(codeName, "OnlyData", "1")
                }

                1 -> {
                    set(codeName, "SysData", "1")
                    set(codeName, "UsSysData", "0")
                    set(codeName, "OnlyData", "0")
                }

                else -> {
                    set(codeName, "SysData", "0")
                    set(codeName, "UsSysData", "1")
                    set(codeName, "OnlyData", "0")
                }
            }
        }
    }

    private fun minMaxLength(codeName: String, codeDetails: CodeDetails) {
        set(codeName, "Enable", enable(codeDetails.enable))
        if (codeDetails.enable) {
            set(codeName, "Minlen", codeDetails.minLength.toString())
            set(codeName, "Maxlen", codeDetails.maxLength.toString())
        }
    }

    private fun lct(codeName: String, codeDetails: CodeDetails) {
        minMaxLength(codeName, codeDetails)
        if (codeDetails.enable) {
            set(codeName, "Check", enable(codeDetails.checkDigit))
            transmitCheckDigit(codeName, codeDetails.transmitCheckDigit)
        }
    }
}