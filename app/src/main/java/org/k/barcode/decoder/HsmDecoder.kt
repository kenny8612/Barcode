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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread


class HsmDecoder : BarcodeDecoder, DecoderListener {
    private val decoder: Decoder = Decoder()
    private val decodeResult = DecodeResult()
    private var decodeStart = false
    private var decodeTimeout = 5000
    private var startTime = 0L
    private val queue = LinkedBlockingQueue<BarcodeInfo>()

    @OptIn(DelicateCoroutinesApi::class)
    private val flow = callbackFlow<BarcodeInfo> {
        val thread = thread {
            while (!Thread.interrupted()) {
                try {
                    trySend(queue.take())
                } catch (e: Exception) {
                    break
                }
            }
        }
        awaitClose {
            thread.interrupt()
        }
    }.shareIn(GlobalScope, started = SharingStarted.WhileSubscribed(), replay = 0)

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
        if (decodeJob?.isActive == true) return

        decodeJob = GlobalScope.launch {
            decodeStart = true
            withContext(Dispatchers.IO) {
                try {
                    startTime = System.currentTimeMillis()
                    decoder.waitForDecodeTwo(decodeTimeout, decodeResult)
                    if (decodeResult.length > 0) {
                        val aim = ByteArray(3)
                        aim[0] = ']'.code.toByte()
                        aim[1] = decoder.barcodeAimID
                        aim[2] = decoder.barcodeAimModifier
                        val barcodeInfo = BarcodeInfo(
                            decodeResult.byteBarcodeData,
                            String(aim),
                            System.currentTimeMillis() - startTime
                        )
                        queue.put(barcodeInfo)
                    }
                } catch (e: DecoderException) {
                    queue.put(
                        if (e.errorCode == RESULT_ERR_NOTRIGGER) BarcodeInfo() else BarcodeInfo(
                            decodeTime = System.currentTimeMillis() - startTime
                        )
                    )
                }
            }
        }
    }

    override fun cancelDecode() {
        decodeJob?.cancel()
        decodeStart = false
    }

    override fun getBarcodeFlow(): Flow<BarcodeInfo> = flow

    override fun timeout(timeout: Int) {
        decodeTimeout = timeout
    }

    override fun updateCode(codeDetails: List<CodeDetails>) {
        codeDetails.forEach {
            var symConfig = SymbologyConfig(-1)
            when (it.name) {
                Code.EAN8.aliasName -> symConfig = formatELAN(SYM_EAN8, it)
                Code.EAN13.aliasName -> symConfig = formatELAN(SYM_EAN13, it)
                Code.UPC_A.aliasName -> symConfig = formatUPC(SYM_UPCA, it)
                Code.UPC_E.aliasName -> symConfig = formatUPC(SYM_UPCE0, it)
                Code.Code11.aliasName -> symConfig = formatLCT(SYM_CODE11, it)
                Code.Code39.aliasName -> {
                    symConfig = formatLCT(SYM_CODE39, it)
                    if (it.fullAscii) symConfig.Flags =
                        symConfig.Flags or SYMBOLOGY_ENABLE_FULLASCII
                }

                Code.Code93.aliasName -> symConfig = formatMinMaxLength(SYM_CODE93, it)
                Code.Code128.aliasName -> symConfig = formatMinMaxLength(SYM_CODE128, it)
                Code.Aztec.aliasName -> symConfig = formatMinMaxLength(SYM_AZTEC, it)
                Code.MaxiCode.aliasName -> symConfig = formatMinMaxLength(SYM_MAXICODE, it)
                Code.MicroPDF.aliasName -> symConfig = formatMinMaxLength(SYM_MICROPDF, it)
                Code.PDF417.aliasName -> symConfig = formatMinMaxLength(SYM_PDF417, it)
                Code.DataMatrix.aliasName -> symConfig = formatMinMaxLength(SYM_DATAMATRIX, it)
                Code.QR.aliasName -> symConfig = formatMinMaxLength(SYM_QR, it)
                Code.HanXin.aliasName -> symConfig = formatMinMaxLength(SYM_HANXIN, it)
                Code.GridMatrix.aliasName -> symConfig = formatMinMaxLength(SYM_GRIDMATRIX, it)
                Code.Matrix25.aliasName -> symConfig = formatMinMaxLength(SYM_MATRIX25, it)
                Code.CodaBar.aliasName -> {
                    symConfig = formatMinMaxLength(SYM_CODABAR, it)
                    if (it.startStopCharacters) symConfig.Flags =
                        symConfig.Flags or SYMBOLOGY_START_STOP_XMIT
                }

                Code.INT25.aliasName -> {
                    symConfig = formatLCT(SYM_INT25, it)
                    setSymbologyConfig(symConfig, it)
                    symConfig = formatMinMaxLength(SYM_IATA25, it)
                    setSymbologyConfig(symConfig, it)
                    symConfig = formatMinMaxLength(SYM_STRT25, it)
                }

                Code.Composite.aliasName -> symConfig = formatMinMaxLength(SYM_COMPOSITE, it)
                Code.CodaBlock.aliasName -> symConfig = formatMinMaxLength(SYM_CODABLOCK, it)
                Code.DotCode.aliasName -> symConfig = formatMinMaxLength(SYM_DOTCODE, it)
                Code.UCC_EAN128.aliasName -> symConfig = formatMinMaxLength(SYM_GS1_128, it)
                Code.RSS.aliasName -> {
                    symConfig = formatMinMaxLength(SYM_RSS, it)
                    symConfig.Flags =
                        symConfig.Flags or SYMBOLOGY_RSS_ENABLE or SYMBOLOGY_RSE_ENABLE or SYMBOLOGY_RSL_ENABLE
                }
                Code.Telepen.aliasName-> symConfig = formatMinMaxLength(SYM_TELEPEN, it)
                Code.MSI.aliasName -> symConfig = formatLCT(SYM_MSI, it)
                Code.ISBN.aliasName -> symConfig.symID = SYM_ISBT
                Code.AustraliaPost.aliasName -> symConfig.symID = SYM_AUSPOST
                Code.ChinaPost.aliasName -> symConfig.symID = SYM_CHINAPOST
                Code.JapanesePost.aliasName -> symConfig.symID = SYM_JAPOST
                Code.KoreanPost.aliasName -> symConfig.symID = SYM_KOREAPOST
                Code.CanadianPost.aliasName -> symConfig.symID = SYM_CANPOST
            }
            setSymbologyConfig(symConfig, it)
        }
    }

    override fun light(enable: Boolean) {
        decoder.lightsMode = if (enable) ILLUM_AIM_ON else ILLUM_AIM_OFF
    }

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true

    override fun onKeepGoingCallback(): Boolean = decodeStart

    override fun onMultiReadCallback(): Boolean = false

    private fun formatELAN(symID: Int, codeDetails: CodeDetails): SymbologyConfig {
        val symConfig = SymbologyConfig(symID)
        if (codeDetails.transmitCheckDigit) symConfig.Flags = SYMBOLOGY_CHECK_TRANSMIT
        if (codeDetails.supplemental2) symConfig.Flags =
            symConfig.Flags or SYMBOLOGY_2_DIGIT_ADDENDA
        if (codeDetails.supplemental5) symConfig.Flags =
            symConfig.Flags or SYMBOLOGY_5_DIGIT_ADDENDA
        return symConfig
    }

    private fun formatUPC(symID: Int, codeDetails: CodeDetails): SymbologyConfig {
        val symConfig = formatELAN(symID, codeDetails)
        if (codeDetails.upcPreamble != 0) symConfig.Flags =
            symConfig.Flags or SYMBOLOGY_NUM_SYS_TRANSMIT
        return symConfig
    }

    private fun formatMinMaxLength(symID: Int, codeDetails: CodeDetails): SymbologyConfig {
        val symConfig = SymbologyConfig(symID)
        symConfig.Mask = SYM_MASK_MIN_LEN or SYM_MASK_MAX_LEN
        symConfig.MinLength = codeDetails.minLength
        symConfig.MaxLength = codeDetails.maxLength
        return symConfig
    }

    private fun formatLCT(symID: Int, codeDetails: CodeDetails): SymbologyConfig {
        val symConfig = formatMinMaxLength(symID, codeDetails)
        if (codeDetails.checkDigit)
            symConfig.Flags = SYMBOLOGY_CHECK_ENABLE
        if (codeDetails.transmitCheckDigit)
            symConfig.Flags = symConfig.Flags or SYMBOLOGY_CHECK_TRANSMIT
        return symConfig
    }

    private fun setSymbologyConfig(symConfig: SymbologyConfig, codeDetails: CodeDetails) {
        if (symConfig.symID == -1) return

        if (codeDetails.enable)
            symConfig.Flags = symConfig.Flags or SYMBOLOGY_ENABLE

        symConfig.Mask = symConfig.Mask or SYM_MASK_FLAGS
        try {
            decoder.setSymbologyConfig(symConfig)
        } catch (_: DecoderException) {
        }
    }
}