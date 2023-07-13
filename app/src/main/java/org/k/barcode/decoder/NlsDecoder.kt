package org.k.barcode.decoder

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
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_COUNTRY_DATA
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_DATA
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails
import org.k.barcode.decoder.Code.D1.*
import org.k.barcode.decoder.Code.D2.*
import org.k.barcode.decoder.Code.Post.*

class NlsDecoder(numberOfCameras: Int, private val dataPath: String) : BarcodeDecoder {
    private var softEngine = SoftEngine.getInstance()
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
        softEngine.setCameraId(numberOfCameras - 1)
    }

    override fun init(): Boolean {
        if (!softEngine.initSoftEngine(dataPath))
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
}