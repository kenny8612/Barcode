package org.k.barcode.decoder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.k.barcode.model.CodeDetails

class HardDecoder : BaseDecoder() {
    private var handler: Long = 0L
    private var msgJob: Job? = null

    override fun init(codeDetails: List<CodeDetails>): Boolean {
        return nativeOpen()
    }

    override fun deInit() {
        nativeClose()
        msgJob?.cancel()
    }

    override fun startDecode() {
        nativeStartDecode()
    }

    override fun cancelDecode() {
        nativeCancelDecode()
    }

    private fun postEventFromNative(what: Int, `object`: Any) {
        if(msgJob?.isActive == true) return

        msgJob = flow {
            emit(NativeMessage(what, `object` as ByteArray))
        }.onEach {
            when (it.what) {
                MSG_DECODE_COMPLETE -> {
                    barcodeResultCallback?.onSuccess(it.value)
                }

                MSG_DECODE_TIMEOUT -> {
                    barcodeResultCallback?.onTimeout()
                }

                MSG_DECODE_CANCEL -> {
                    barcodeResultCallback?.onCancel()
                }

                MSG_DECODE_ERROR -> {
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    data class NativeMessage(val what: Int, val value: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is NativeMessage) return false

            if (what != other.what) return false
            if (!value.contentEquals(other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = what
            result = 31 * result + value.contentHashCode()
            return result
        }
    }

    companion object {
        const val MSG_DECODE_COMPLETE = 0x000001
        const val MSG_DECODE_TIMEOUT = 0x000002
        const val MSG_DECODE_CANCEL = 0x000004
        const val MSG_DECODE_ERROR = 0x000008
    }

    private external fun nativeOpen(): Boolean
    private external fun nativeClose()
    private external fun nativeStartDecode()
    private external fun nativeCancelDecode()

    init {
        System.loadLibrary("hw_decoder")
    }
}