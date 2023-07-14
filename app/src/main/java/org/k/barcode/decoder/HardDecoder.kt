package org.k.barcode.decoder

class HardDecoder private constructor() : BaseDecoder() {
    private var handler: Long = 0L

    //private val queue = LinkedBlockingQueue<BarcodeInfo>()

    override fun init(): Boolean = nativeOpen()
    override fun deInit() = nativeClose()
    override fun startDecode() {
        super.startDecode()
        nativeStartDecode()
    }

    override fun cancelDecode() = nativeCancelDecode()
    override fun timeout(timeout: Int) = nativeDecodeTimeout(timeout)
    override fun light(enable: Boolean) = nativeLight(enable)
    override fun supportLight(): Boolean = nativeSupportLight()

    private fun postEventFromNative(what: Int, `object`: Any) {
        when (what) {
            MSG_DECODE_COMPLETE -> {
                val data = `object` as ByteArray
                sendBarcodeInfo(
                    sourceData = if (nativeSupportAIM()) data.copyOfRange(3, data.size) else data,
                    aim = if (nativeSupportAIM()) String(data, 0, 3) else null
                )
            }

            MSG_DECODE_TIMEOUT -> notifyTimeout()
            MSG_DECODE_CANCEL, MSG_DECODE_ERROR -> notifyCancel()
        }
    }

    private external fun nativeInit()
    private external fun nativeOpen(): Boolean
    private external fun nativeClose()
    private external fun nativeStartDecode()
    private external fun nativeCancelDecode()
    private external fun nativeDecodeTimeout(timeout: Int)
    private external fun nativeLight(enable: Boolean)
    private external fun nativeSupportLight(): Boolean
    private external fun nativeSupportAIM(): Boolean

    companion object {
        val instance: HardDecoder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HardDecoder()
        }

        const val MSG_DECODE_COMPLETE = 0x000001
        const val MSG_DECODE_TIMEOUT = 0x000002
        const val MSG_DECODE_CANCEL = 0x000004
        const val MSG_DECODE_ERROR = 0x000008
    }

    init {
        System.loadLibrary("hw_decoder")
        nativeInit()
    }
}