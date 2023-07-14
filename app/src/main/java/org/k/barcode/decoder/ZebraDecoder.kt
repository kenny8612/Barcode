package org.k.barcode.decoder

import com.zebra.zebrascanner.ZebraScanner
import org.k.barcode.decoder.Code.D1.CodaBar
import org.k.barcode.decoder.Code.D1.Code11
import org.k.barcode.decoder.Code.D1.Code128
import org.k.barcode.decoder.Code.D1.Code39
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
import org.k.barcode.decoder.Code.D2.HanXin
import org.k.barcode.decoder.Code.D2.MaxiCode
import org.k.barcode.decoder.Code.D2.MicroPDF
import org.k.barcode.decoder.Code.D2.PDF417
import org.k.barcode.decoder.Code.D2.QR
import org.k.barcode.decoder.Code.Post.AustraliaPost
import org.k.barcode.decoder.Code.Post.JapanPostal
import org.k.barcode.decoder.Code.Post.UKPostal
import org.k.barcode.decoder.Code.Post.USPostnet
import org.k.barcode.model.CodeDetails

class ZebraDecoder private constructor() : BaseDecoder() {
    private val zebraScanner = ZebraScanner()

    init {
        zebraScanner.setDecodeCallback(object : ZebraScanner.DecodeCallback {
            override fun onDecodeComplete(
                symbology: Int,
                length: Int,
                data: ByteArray,
                reader: ZebraScanner
            ) {
                if (length > 0) {
                    //3 bits AIM
                    sendBarcodeInfo(data.copyOfRange(3, length), String(data, 0, 3))
                } else {
                    if (length == ZebraScanner.DECODE_STATUS_TIMEOUT)
                        notifyTimeout()
                    else if (length == ZebraScanner.DECODE_STATUS_CANCELED || length == ZebraScanner.DECODE_STATUS_ERROR)
                        notifyCancel()
                }
            }

            override fun onEvent(event: Int, info: Int, data: ByteArray?, reader: ZebraScanner?) {

            }
        })
    }

    override fun init(): Boolean {
        val result = zebraScanner.openScanner(numberOfCameras - 1)
        if (result != ZebraScanner.BCR_SUCCESS)
            return false

        // AIM Code ID Character
        zebraScanner.sdlApiSetNumParameter(45, 1)
        return true
    }

    override fun deInit() {
        zebraScanner.sdlApiClose()
    }

    override fun startDecode() {
        super.startDecode()
        zebraScanner.sdlApiStartDecode()
    }

    override fun cancelDecode() {
        zebraScanner.sdlApiStopDecode()
    }

    override fun timeout(timeout: Int) {
        // available in 0.1 second increments from 0.5 to 9.9 seconds (value 5 from 99)
        zebraScanner.sdlApiSetNumParameter(136, timeout % 9900 / 100)
    }

    override fun light(enable: Boolean) {
        // 298 illumination
        // 306 aiming pattern
        val value = if (enable) 1 else 0
        zebraScanner.sdlApiSetNumParameter(298, value)
        zebraScanner.sdlApiSetNumParameter(306, value)
    }

    override fun lightLevel(lightLevel: LightLevel) {
        when (lightLevel) {
            LightLevel.Low -> zebraScanner.sdlApiSetNumParameter(764, 1)
            LightLevel.Medium -> zebraScanner.sdlApiSetNumParameter(764, 5)
            LightLevel.High -> zebraScanner.sdlApiSetNumParameter(764, 10)
        }
    }

    override fun supportLight(): Boolean = true

    override fun supportCode(): Boolean = true

    override fun supportLightLevel(): Boolean = true

    override fun updateCode(codeDetails: List<CodeDetails>) {
        codeDetails.forEach {
            it.run {
                when (name) {
                    /***** 1D start *****/
                    EAN8.name -> {
                        setEnable(4)
                        setTransmitCheckDigit(1881)
                        setDigit2()
                        setDigit5()
                    }

                    EAN13.name -> {
                        setEnable(3)
                        setTransmitCheckDigit(1882)
                        setDigit2()
                        setDigit5()
                    }

                    UPC_A.name -> {
                        setEnable(1)
                        setTransmitCheckDigit(40)
                        upcPreamble(34)
                    }

                    UPC_E.name -> {
                        //UPC-E0
                        setEnable(2)
                        setTransmitCheckDigit(41)
                        upcPreamble(35)

                        //UPC-E1
                        setEnable(12)
                        setTransmitCheckDigit(42)
                        upcPreamble(36)
                    }

                    Code11.name -> {
                        setEnable(10)
                        setMinLength(28)
                        setMaxLength(29)
                        setCheckDigit(52)
                        setTransmitCheckDigit(47)
                    }

                    Code39.name -> {
                        setEnable(0)
                        setMinLength(18)
                        setMaxLength(19)
                        setCheckDigit(48)
                        setTransmitCheckDigit(43)
                        setFullAscii(17)
                    }

                    Code93.name -> {
                        setEnable(9)
                        setMinLength(26)
                        setMaxLength(27)
                    }

                    Code128.name -> {
                        setEnable(8)
                        setMinLength(209)
                        setMaxLength(210)
                    }

                    UCC_EAN128.name -> setEnable(14)
                    Matrix25.name -> {
                        setEnable(618)
                        setMinLength(619)
                        setMaxLength(620)
                        setCheckDigit(622)
                        setTransmitCheckDigit(623)
                    }

                    CodaBar.name -> {
                        setEnable(7)
                        setMinLength(24)
                        setMaxLength(25)
                        setStartStopCharacters(855)
                    }

                    MSI.name -> {
                        setEnable(11)
                        setMinLength(30)
                        setMaxLength(31)
                        setCheckDigit(50)
                        setTransmitCheckDigit(46)
                        setCheckDigitAlgorithm(51)
                    }

                    INT25.name -> {
                        setEnable(6)
                        setMinLength(22)
                        setMaxLength(23)
                        setCheckDigit(49)
                        setTransmitCheckDigit(44)
                        setEnable(5)//Discrete 2 of 5
                    }

                    ISBN.name -> setEnable(84)
                    RSS.name -> setEnable(338)
                    Composite.name -> {
                        setEnable(341)//Composite CC-C
                        setEnable(342)//Composite CC-A/B
                        setEnable(371)//Composite CC-A/B
                    }
                    /***** 1D end *****/

                    /***** 2D start *****/
                    QR.name -> {
                        setEnable(293)
                        setEnable(573)//MicroQR
                    }

                    DataMatrix.name -> {
                        setEnable(292)
                        setEnable(1336)//GS1 Data Matrix
                    }

                    Aztec.name -> setEnable(574)
                    MaxiCode.name -> setEnable(294)
                    MicroPDF.name -> setEnable(227)
                    PDF417.name -> setEnable(15)
                    HanXin.name -> setEnable(1167)
                    /***** 2D end *****/


                    /***** POST start *****/
                    AustraliaPost.name -> setEnable(291)
                    JapanPostal.name -> setEnable(290)
                    UKPostal.name -> setEnable(92)
                    USPostnet.name -> setEnable(89)
                    /***** POST end *****/
                }
            }
        }
    }

    private fun CodeDetails.setEnable(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, if (this.enable) 1 else 0)
    }

    private fun CodeDetails.setTransmitCheckDigit(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(
            dwParameter,
            if (this.transmitCheckDigit) 1 else 0
        )
    }

    private fun CodeDetails.setDigit2() {
        zebraScanner.sdlApiSetNumParameter(207, if (this.supplemental2) 1 else 0)
    }

    private fun CodeDetails.setDigit5() {
        zebraScanner.sdlApiSetNumParameter(208, if (this.supplemental5) 1 else 0)
    }

    private fun CodeDetails.upcPreamble(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, this.upcPreamble)
    }

    private fun CodeDetails.setMinLength(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, this.minLength)
    }

    private fun CodeDetails.setMaxLength(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, this.maxLength)
    }

    private fun CodeDetails.setCheckDigit(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, if (this.checkDigit) 1 else 0)
    }

    private fun CodeDetails.setFullAscii(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, if (this.fullAscii) 1 else 0)
    }

    private fun CodeDetails.setStartStopCharacters(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, if (this.startStopCharacters) 1 else 0)
    }

    private fun CodeDetails.setCheckDigitAlgorithm(dwParameter: Int) {
        zebraScanner.sdlApiSetNumParameter(dwParameter, this.algorithm)
    }

    companion object {
        val instance: ZebraDecoder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ZebraDecoder()
        }
    }
}