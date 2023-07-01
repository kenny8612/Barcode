package org.k.barcode.decoder

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.k.barcode.model.BarcodeInfo

interface BarcodeResultCallback {
    fun onSuccess(data: ByteArray, aim: String)
    fun onTimeout()
}

class DecoderManager private constructor() : BarcodeResultCallback {
    private val barcodeDecoder: BarcodeDecoder

    private var decoderType = DECODER_TYPE_HARD
    private var state = DecoderState.Closed

    private var decoderEventListeners: MutableList<DecoderEventListener> = ArrayList()
    private var barcodeListeners: MutableList<BarcodeListener> = ArrayList()

    private var startDecodeTime = 0L

    private val backgroundThread = HandlerThread("barcode_handler")

    private var decodeTimeoutJob: Job? = null

    init {
        decoderType = initDecoderType()
        barcodeDecoder = when (decoderType) {
            DECODER_TYPE_NLS -> {
                NlsDecoder()
            }

            else -> {
                HardDecoder()
            }
        }
        barcodeDecoder.setBarcodeListener(this)
        backgroundThread.start()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val workHandler: Handler = object : Handler(backgroundThread.looper) {
        override fun handleMessage(msg: Message) {
            decodeTimeoutJob?.cancel()
            when (msg.what) {
                MSG_OPEN -> {
                    if (state == DecoderState.Closed) {
                        println("open barcode decoder")
                        if (barcodeDecoder.init())
                            state = DecoderState.Idle
                        synchronized(decoderEventListeners) {
                            for (listener in decoderEventListeners)
                                listener.onEvent(
                                    if (state == DecoderState.Closed) DecoderEvent.Closed
                                    else DecoderEvent.Opened
                                )
                        }
                    }
                }

                MSG_CLOSE -> {
                    if (state != DecoderState.Closed) {
                        println("close barcode decoder")
                        barcodeDecoder.deInit()
                        state = DecoderState.Closed
                        synchronized(decoderEventListeners) {
                            for (listener in decoderEventListeners)
                                listener.onEvent(DecoderEvent.Closed)
                        }
                    }
                }

                MSG_START_DECODE -> {
                    if (state == DecoderState.Idle) {
                        println("start decode")
                        state = DecoderState.Decoding
                        startDecodeTime = System.currentTimeMillis()
                        barcodeDecoder.startDecode()
                        decodeTimeoutJob = GlobalScope.launch {
                            delay(MAX_DECODE_TIMEOUT)
                            sendEmptyMessage(MSG_DECODE_TIMEOUT)
                        }
                    }
                }

                MSG_CANCEL_DECODER -> {
                    if (state == DecoderState.Decoding) {
                        println("cancel decode")
                        barcodeDecoder.cancelDecode()
                        state = DecoderState.Idle
                    }
                }

                MSG_DECODE_RESULT -> {
                    if (state == DecoderState.Decoding) {
                        val barcodeInfo = msg.obj as BarcodeInfo
                        synchronized(barcodeListeners) {
                            for (listener in barcodeListeners)
                                listener.onBarcode(barcodeInfo)
                        }
                        state = DecoderState.Idle
                    }
                }

                MSG_DECODE_TIMEOUT -> {
                    if (state == DecoderState.Decoding) {
                        synchronized(decoderEventListeners) {
                            for (listener in decoderEventListeners)
                                listener.onEvent(DecoderEvent.DecodeTimeout)
                        }
                        state = DecoderState.Idle
                    }
                }
            }
            super.handleMessage(msg)
        }
    }

    private fun initDecoderType(): Int {
        return DECODER_TYPE_HARD
    }

    fun addEventListener(decoderEventListener: DecoderEventListener) {
        synchronized(decoderEventListeners) {
            if (!decoderEventListeners.contains(decoderEventListener))
                decoderEventListeners.add(decoderEventListener)
        }
    }

    fun removeEventListener(decoderEventListener: DecoderEventListener) {
        synchronized(decoderEventListeners) {
            decoderEventListeners.remove(decoderEventListener)
        }
    }

    fun addBarcodeListener(barcodeListener: BarcodeListener) {
        synchronized(barcodeListeners) {
            if (!barcodeListeners.contains(barcodeListener))
                barcodeListeners.add(barcodeListener)
        }
    }

    fun removeBarcodeListener(barcodeListener: BarcodeListener) {
        synchronized(barcodeListeners) {
            barcodeListeners.remove(barcodeListener)
        }
    }

    fun open() {
        workHandler.obtainMessage(MSG_OPEN).sendToTarget()
    }

    fun close() {
        workHandler.obtainMessage(MSG_CLOSE).sendToTarget()
    }

    fun startDecode() {
        workHandler.obtainMessage(MSG_START_DECODE).sendToTarget()
    }

    fun cancelDecode() {
        workHandler.obtainMessage(MSG_CANCEL_DECODER).sendToTarget()
    }

    override fun onSuccess(data: ByteArray, aim: String) {
        val barcodeInfo = BarcodeInfo(data, aim, System.currentTimeMillis() - startDecodeTime)
        workHandler.obtainMessage(MSG_DECODE_RESULT, barcodeInfo).sendToTarget()
    }

    override fun onTimeout() {
        workHandler.obtainMessage(MSG_DECODE_TIMEOUT).sendToTarget()
    }

    companion object {
        val instance: DecoderManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DecoderManager()
        }

        const val DECODER_TYPE_HARD = 0
        const val DECODER_TYPE_NLS = 1

        private const val MSG_OPEN = 0
        private const val MSG_CLOSE = 1
        private const val MSG_START_DECODE = 2
        private const val MSG_CANCEL_DECODER = 3
        private const val MSG_DECODE_RESULT = 4
        private const val MSG_DECODE_TIMEOUT = 5

        private const val MAX_DECODE_TIMEOUT = 5000L
    }

    enum class DecoderState {
        Closed,
        Idle,
        Decoding
    }
}