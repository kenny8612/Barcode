package org.k.barcode.decoder

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import org.k.barcode.model.BarcodeInfo
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class HardDecoder : BarcodeDecoder {
    private var handler: Long = 0L
    private val queue = LinkedBlockingQueue<BarcodeInfo>()
    private var startTime = 0L

    override fun init(): Boolean {
        return true//nativeOpen()
    }

    override fun deInit() {
        //nativeClose()
    }

    override fun startDecode() {
        startTime = System.currentTimeMillis()
        //nativeStartDecode()
        queue.put(
            BarcodeInfo(
                "hhtpakdfjdsofiudsofi".toByteArray(),
                "QR",
                decodeTime = System.currentTimeMillis() - startTime
            )
        )
    }

    override fun cancelDecode() {
        //nativeCancelDecode()
    }

    override fun getBarcodeFlow(): Flow<BarcodeInfo> = flow
    override fun timeout(timeout: Int) {
        //nativeDecodeTimeout(timeout)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val flow = callbackFlow {
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

    private fun postEventFromNative(what: Int, `object`: Any) {
        when (what) {
            MSG_DECODE_COMPLETE -> {
                queue.put(
                    BarcodeInfo(
                        `object` as ByteArray,
                        decodeTime = System.currentTimeMillis() - startTime
                    )
                )
            }

            MSG_DECODE_TIMEOUT -> {
                queue.put(BarcodeInfo(decodeTime = System.currentTimeMillis() - startTime))
            }

            MSG_DECODE_CANCEL -> {
                queue.put(BarcodeInfo())
            }

            MSG_DECODE_ERROR -> {
            }
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
    private external fun nativeDecodeTimeout(timeout: Int)

    init {
        //System.loadLibrary("hw_decoder")
    }
}