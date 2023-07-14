package org.k.barcode.decoder

import android.content.Context
import com.dawn.decoderapijni.SoftEngine
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_SUCC
import com.dawn.decoderapijni.SoftEngine.SCN_EVENT_DEC_TIMEOUT
import org.k.barcode.AppContent.Companion.app
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_COUNTRY_DATA
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_DATA
import org.k.barcode.decoder.Code.D1.CodaBar
import org.k.barcode.decoder.Code.D1.Code11
import org.k.barcode.decoder.Code.D1.Code128
import org.k.barcode.decoder.Code.D1.Code39
import org.k.barcode.decoder.Code.D1.Code49
import org.k.barcode.decoder.Code.D1.Code93
import org.k.barcode.decoder.Code.D1.Composite
import org.k.barcode.decoder.Code.D1.EAN13
import org.k.barcode.decoder.Code.D1.EAN8
import org.k.barcode.decoder.Code.D1.INT25
import org.k.barcode.decoder.Code.D1.ISBN
import org.k.barcode.decoder.Code.D1.MSI
import org.k.barcode.decoder.Code.D1.Matrix25
import org.k.barcode.decoder.Code.D1.RSS
import org.k.barcode.decoder.Code.D1.UCC_EAN128
import org.k.barcode.decoder.Code.D1.UPC_A
import org.k.barcode.decoder.Code.D1.UPC_E
import org.k.barcode.decoder.Code.D2.Aztec
import org.k.barcode.decoder.Code.D2.DataMatrix
import org.k.barcode.decoder.Code.D2.DotCode
import org.k.barcode.decoder.Code.D2.GridMatrix
import org.k.barcode.decoder.Code.D2.HanXin
import org.k.barcode.decoder.Code.D2.MaxiCode
import org.k.barcode.decoder.Code.D2.MicroPDF
import org.k.barcode.decoder.Code.D2.PDF417
import org.k.barcode.decoder.Code.D2.QR
import org.k.barcode.decoder.Code.Post.AustraliaPost
import org.k.barcode.decoder.Code.Post.ChinaPost
import org.k.barcode.decoder.Code.Post.JapanPostal
import org.k.barcode.model.CodeDetails

class NlsDecoder private constructor() : BaseDecoder() {
    private var softEngine = SoftEngine.getInstance()

    init {
        softEngine.setNdkSystemLanguage(0)
        softEngine.setCameraId(numberOfCameras - 1)
        softEngine.setScanningCallback { eventCode, _, param2, length ->
            when (eventCode) {
                SCN_EVENT_DEC_SUCC -> {
                    val buffer = ByteArray(length - 128)
                    System.arraycopy(param2, 128, buffer, 0, length - 128)
                    for ((index, value) in param2.withIndex()) {
                        if (value.toInt() == 0) {
                            sendBarcodeInfo(sourceData = buffer, aim = String(param2, 0, index))
                            break
                        }
                    }
                }

                SCN_EVENT_DEC_TIMEOUT -> {
                    notifyTimeout()
                }

                else -> {
                    notifyCancel()
                }
            }
            0
        }
    }

    override fun init(): Boolean {
        if (!softEngine.initSoftEngine(app.getDir("nls_data", Context.MODE_PRIVATE).absolutePath))
            return false

        return softEngine.Open().apply {
            if (this) Thread.sleep(100)
        }
    }


    override fun deInit() {
        softEngine.Close()
        softEngine.Deinit()
    }

    override fun startDecode() {
        super.startDecode()
        softEngine.StartDecode()
    }

    override fun cancelDecode() {
        softEngine.StopDecode()
    }

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true

    override fun updateCode(codeDetails: List<CodeDetails>) {
        codeDetails.forEach {
            it.run {
                when (name) {
                    /***** 1D start *****/
                    EAN8.name -> setEAN("EAN8")
                    EAN13.name -> setEAN("EAN13")
                    UPC_A.name -> setUPC("UPCA")
                    UPC_E.name -> setUPC("UPCE")
                    Code11.name -> {
                        setEnable("CODE11")
                        setLCT("CODE11")
                    }

                    Code128.name -> {
                        setEnable("CODE128")
                        setMinMaxLength("CODE128")
                    }

                    Code39.name -> {
                        setEnable("CODE39")
                        setMinMaxLength("CODE39")
                        setFullAscii("CODE39")
                    }

                    Code49.name -> {
                        setEnable("CODE49")
                        setMinMaxLength("CODE49")
                    }

                    Code93.name -> {
                        setEnable("CODE93")
                        setMinMaxLength("CODE93")
                    }

                    Matrix25.name -> {
                        setEnable("MATRIX25")
                        setLCT("MATRIX25")
                    }

                    UCC_EAN128.name -> {
                        setEnable("UCCEAN128")
                        setMinMaxLength("UCCEAN128")
                    }

                    CodaBar.name -> {
                        setEnable("CODEBAR")
                        setMinMaxLength("CODEBAR")
                        setStartStopCharacters("CODEBAR")
                    }

                    INT25.name -> {
                        setEnable("ITF")
                        setLCT("ITF")
                        setEnable("IND25")
                        setLCT("IND25")
                        setEnable("ITF6")
                        setTransmitCheckDigit("ITF6")
                        setEnable("ITF14")
                        setTransmitCheckDigit("ITF14")
                    }

                    ISBN.name -> setEnable("ISBN")
                    Composite.name -> setEnable("COMPOSITE")
                    RSS.name -> setEnable("RSS")
                    MSI.name -> {
                        setEnable("MSIPLSY")
                        setLCT("MSIPLSY")
                        setCheckDigitAlgorithm("MSIPLSY")
                    }
                    /***** 1D end *****/

                    /***** 2D start *****/
                    MaxiCode.name -> setEnable("MAXIC")
                    MicroPDF.name -> setEnable("MICROPDF")
                    PDF417.name -> setEnable("PDF417")
                    DataMatrix.name -> setEnable("DM")
                    QR.name -> setEnable("QR")
                    HanXin.name -> setEnable("CSC")
                    GridMatrix.name -> setEnable("GM")
                    Aztec.name -> setEnable("AZTEC")
                    DotCode.name -> setEnable("DOTCODE")
                    /***** 2D end *****/

                    /***** POST start *****/
                    AustraliaPost.name -> setEnable("AUSPOST")
                    ChinaPost.name -> setEnable("CHNPOST")
                    JapanPostal.name -> setEnable("JAPANPOST")
                    /***** POST end *****/
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

    private fun CodeDetails.setEnable(codeName: String) {
        softEngine.ScanSet(codeName, "Enable", if (this.enable) "1" else "0")
    }

    private fun CodeDetails.setEAN(codeName: String) {
        setEnable(codeName)
        setDigit2Digit5(codeName)
        setTransmitCheckDigit(codeName)
    }

    private fun CodeDetails.setUPC(codeName: String) {
        setEAN(codeName)
        upcPreamble(codeName)
    }

    private fun CodeDetails.setDigit2Digit5(codeName: String) {
        softEngine.ScanSet(codeName, "Digit2", if (this.supplemental2) "1" else "0")
        softEngine.ScanSet(codeName, "Digit5", if (this.supplemental2) "1" else "0")
    }

    private fun CodeDetails.setTransmitCheckDigit(codeName: String) {
        softEngine.ScanSet(codeName, "TrsmtChkChar", if (this.transmitCheckDigit) "1" else "0")
    }

    private fun CodeDetails.upcPreamble(codeName: String) {
        when (this.upcPreamble) {
            UPC_PREAMBLE_SYSTEM_DATA -> {
                softEngine.ScanSet(codeName, "SysData", "1")
                softEngine.ScanSet(codeName, "UsSysData", "0")
                softEngine.ScanSet(codeName, "OnlyData", "0")
            }

            UPC_PREAMBLE_SYSTEM_COUNTRY_DATA -> {
                softEngine.ScanSet(codeName, "SysData", "0")
                softEngine.ScanSet(codeName, "UsSysData", "1")
                softEngine.ScanSet(codeName, "OnlyData", "0")
            }

            else -> {
                softEngine.ScanSet(codeName, "SysData", "0")
                softEngine.ScanSet(codeName, "UsSysData", "0")
                softEngine.ScanSet(codeName, "OnlyData", "1")
            }
        }
    }

    private fun CodeDetails.setMinMaxLength(codeName: String) {
        softEngine.ScanSet(codeName, "Minlen", this.minLength.toString())
        softEngine.ScanSet(codeName, "Maxlen", this.maxLength.toString())
    }

    private fun CodeDetails.setLCT(codeName: String) {
        setMinMaxLength(codeName)
        setCheckDigit(codeName)
        setTransmitCheckDigit(codeName)
    }

    private fun CodeDetails.setCheckDigit(codeName: String) {
        softEngine.ScanSet(codeName, "Check", if (this.checkDigit) "1" else "0")
    }

    private fun CodeDetails.setFullAscii(codeName: String) {
        softEngine.ScanSet(codeName, "FullAscii", if (this.fullAscii) "1" else "0")
    }

    private fun CodeDetails.setStartStopCharacters(codeName: String) {
        softEngine.ScanSet(codeName, "TrsmtStasrtStop", if (this.startStopCharacters) "1" else "0")
    }

    private fun CodeDetails.setCheckDigitAlgorithm(codeName: String) {
        softEngine.ScanSet(codeName, "ChkMode", this.algorithm.toString())
    }


    companion object {
        val instance: NlsDecoder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NlsDecoder()
        }
    }
}