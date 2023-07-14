package org.k.barcode.decoder

import com.hsm.barcode.DecodeResult
import com.hsm.barcode.Decoder
import com.hsm.barcode.DecoderConfigValues.LightsMode.ILLUM_AIM_OFF
import com.hsm.barcode.DecoderConfigValues.LightsMode.ILLUM_AIM_ON
import com.hsm.barcode.DecoderConfigValues.SymbologyFlags.*
import com.hsm.barcode.DecoderConfigValues.SymbologyID.*
import com.hsm.barcode.DecoderException
import com.hsm.barcode.DecoderException.ResultID.RESULT_ERR_NOTRIGGER
import com.hsm.barcode.DecoderListener
import com.hsm.barcode.SymbologyConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_COUNTRY_DATA
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_DATA
import org.k.barcode.decoder.Code.D1.*
import org.k.barcode.decoder.Code.D2.*
import org.k.barcode.decoder.Code.Post.*
import org.k.barcode.model.CodeDetails

class HsmDecoder private constructor() : BaseDecoder(), DecoderListener {
    private val decoder: Decoder = Decoder()
    private val decodeResult = DecodeResult()
    private var decodeStart = false
    private var decodeTimeout = 5000

    init {
        decoder.setDecoderListeners(this)
    }

    override fun init(): Boolean {
        try {
            decoder.connectDecoderLibrary()
        } catch (e: DecoderException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun deInit() {
        try {
            decoder.disconnectDecoderLibrary()
        } catch (e: DecoderException) {
            e.printStackTrace()
        }
    }

    private var decodeJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun startDecode() {
        super.startDecode()

        if (decodeJob?.isActive == true)
            decodeJob?.cancel()

        decodeJob = GlobalScope.launch {
            try {
                decodeStart = true
                decoder.waitForDecodeTwo(decodeTimeout, decodeResult)
                if (decodeResult.length > 0) {
                    val aim = ByteArray(3)
                    aim[0] = ']'.code.toByte()
                    aim[1] = decoder.barcodeAimID
                    aim[2] = decoder.barcodeAimModifier
                    sendBarcodeInfo(decodeResult.byteBarcodeData, String(aim))
                }
            } catch (e: DecoderException) {
                if (e.errorCode == RESULT_ERR_NOTRIGGER) notifyCancel()
                else notifyTimeout()
            }
        }
    }

    override fun cancelDecode() {
        decodeStart = false
        decodeJob?.cancel()
    }

    override fun timeout(timeout: Int) {
        decodeTimeout = timeout
    }

    override fun updateCode(codeDetails: List<CodeDetails>) {
        codeDetails.forEach {
            it.run {
                var symID: Int
                when (name) {
                    /***** 1D start *****/
                    EAN8.name -> setEAN(SYM_EAN8)
                    EAN13.name -> setEAN(SYM_EAN13)
                    UPC_A.name -> setUPC(SYM_UPCA)
                    UPC_E.name -> {
                        setUPC(SYM_UPCE0)
                        setUPC(SYM_UPCE1)
                    }

                    Code11.name -> {
                        symID = SYM_CODE11
                        setEnable(symID)
                        setLCT(symID)
                    }

                    Code39.name -> {
                        symID = SYM_CODE39
                        setEnable(symID)
                        setLCT(symID)
                        setFullAscii(symID)
                    }

                    Code93.name -> {
                        symID = SYM_CODE93
                        setEnable(symID)
                        setMinMaxLength(symID)
                    }

                    Code128.name -> {
                        symID = SYM_CODE128
                        setEnable(symID)
                        setMinMaxLength(symID)
                    }

                    Matrix25.name -> {
                        symID = SYM_MATRIX25
                        setEnable(symID)
                        setLCT(symID)
                    }

                    CodaBar.name -> {
                        symID = SYM_CODABAR
                        setEnable(symID)
                        setMinMaxLength(symID)
                        setStartStopCharacters(symID)
                    }

                    INT25.name -> {
                        symID = SYM_INT25
                        setEnable(symID)
                        setLCT(symID)
                        symID = SYM_IATA25
                        setEnable(symID)
                        setLCT(symID)
                        symID = SYM_STRT25
                        setEnable(symID)
                        setLCT(symID)
                    }

                    Composite.name -> {
                        symID = SYM_COMPOSITE
                        setEnable(symID)
                        setMinMaxLength(symID)
                    }

                    UCC_EAN128.name -> {
                        symID = SYM_GS1_128
                        setEnable(symID)
                        setMinMaxLength(symID)
                    }

                    RSS.name -> setRSS()

                    Telepen.name -> {
                        symID = SYM_TELEPEN
                        setEnable(symID)
                        setMinMaxLength(symID)
                    }

                    MSI.name -> {
                        symID = SYM_MSI
                        setEnable(symID)
                        setLCT(symID)
                    }

                    ISBN.name -> setEnable(SYM_ISBT)
                    /***** 1D end *****/

                    /***** 2D start *****/
                    Aztec.name -> setEnable(SYM_AZTEC)
                    MaxiCode.name -> setEnable(SYM_MAXICODE)
                    MicroPDF.name -> setEnable(SYM_MICROPDF)
                    PDF417.name -> setEnable(SYM_PDF417)
                    DataMatrix.name -> setEnable(SYM_DATAMATRIX)
                    QR.name -> setEnable(SYM_QR)
                    HanXin.name -> setEnable(SYM_HANXIN)
                    GridMatrix.name -> setEnable(SYM_GRIDMATRIX)
                    CodaBlock.name -> setEnable(SYM_CODABLOCK)
                    DotCode.name -> setEnable(SYM_DOTCODE)
                    /***** 2D end *****/


                    /***** POST start *****/
                    AustraliaPost.name -> setEnable(SYM_AUSPOST)
                    ChinaPost.name -> setEnable(SYM_CHINAPOST)
                    JapanPostal.name -> setEnable(SYM_JAPOST)
                    KoreanPost.name -> setEnable(SYM_KOREAPOST)
                    CanadianPost.name -> setEnable(SYM_CANPOST)
                    /***** POST end *****/
                }
            }
        }
    }

    override fun light(enable: Boolean) {
        decoder.lightsMode = if (enable) ILLUM_AIM_ON else ILLUM_AIM_OFF
    }

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true

    override fun onKeepGoingCallback(): Boolean = decodeStart

    override fun onMultiReadCallback(): Boolean = false

    private fun CodeDetails.setEnable(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = symConfig.Mask or SYM_MASK_FLAGS
            symConfig.Flags = if (this.enable) symConfig.Flags or SYMBOLOGY_ENABLE
            else symConfig.Flags and SYMBOLOGY_ENABLE.inv()
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.setLCT(symID: Int) {
        setMinMaxLength(symID)
        setCheckDigit(symID)
        setTransmitCheckDigit(symID)
    }

    private fun CodeDetails.setEAN(symID: Int) {
        setEnable(symID)
        setDigit2Digit5(symID)
        setTransmitCheckDigit(symID)
    }

    private fun CodeDetails.setUPC(symID: Int) {
        setEAN(symID)
        upcPreamble(symID)
    }

    private fun CodeDetails.setTransmitCheckDigit(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS
            symConfig.Flags =
                if (this.transmitCheckDigit) symConfig.Flags or SYMBOLOGY_CHECK_TRANSMIT
                else symConfig.Flags and SYMBOLOGY_CHECK_TRANSMIT.inv()
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.setDigit2Digit5(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS
            symConfig.Flags =
                if (this.supplemental2) symConfig.Flags or SYMBOLOGY_2_DIGIT_ADDENDA
                else symConfig.Flags and SYMBOLOGY_2_DIGIT_ADDENDA.inv()
            symConfig.Flags =
                if (this.supplemental5) symConfig.Flags or SYMBOLOGY_5_DIGIT_ADDENDA
                else symConfig.Flags and SYMBOLOGY_5_DIGIT_ADDENDA.inv()
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.upcPreamble(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS
            symConfig.Flags = when (this.upcPreamble) {
                UPC_PREAMBLE_SYSTEM_DATA -> symConfig.Flags or SYMBOLOGY_NUM_SYS_TRANSMIT
                UPC_PREAMBLE_SYSTEM_COUNTRY_DATA -> symConfig.Flags or SYMBOLOGY_NUM_SYS_TRANSMIT or SYMBOLOGY_ADDENDA_REQUIRED
                else -> symConfig.Flags and (SYMBOLOGY_NUM_SYS_TRANSMIT or SYMBOLOGY_ADDENDA_REQUIRED).inv()
            }
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.setMinMaxLength(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS or SYM_MASK_MIN_LEN or SYM_MASK_MAX_LEN
            symConfig.MinLength = this.minLength
            symConfig.MaxLength = this.maxLength
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.setCheckDigit(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS
            symConfig.Flags = if (this.checkDigit) symConfig.Flags or SYMBOLOGY_CHECK_ENABLE
            else symConfig.Flags and SYMBOLOGY_CHECK_ENABLE.inv()
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.setFullAscii(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS
            symConfig.Flags = if (this.fullAscii) symConfig.Flags or SYMBOLOGY_ENABLE_FULLASCII
            else symConfig.Flags and SYMBOLOGY_ENABLE_FULLASCII.inv()
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.setStartStopCharacters(symID: Int) {
        try {
            val symConfig = SymbologyConfig(symID)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS
            symConfig.Flags =
                if (this.startStopCharacters) symConfig.Flags or SYMBOLOGY_START_STOP_XMIT
                else symConfig.Flags and SYMBOLOGY_START_STOP_XMIT.inv()
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    private fun CodeDetails.setRSS() {
        setEnable(SYM_RSS)

        try {
            val symConfig = SymbologyConfig(SYM_RSS)
            decoder.getSymbologyConfig(symConfig)
            symConfig.Mask = SYM_MASK_FLAGS
            symConfig.Flags = symConfig.Flags or SYMBOLOGY_RSX_ENABLE_MASK
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }

    companion object {
        val instance: HsmDecoder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HsmDecoder()
        }
    }
}