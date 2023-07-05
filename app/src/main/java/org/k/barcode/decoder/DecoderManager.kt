package org.k.barcode.decoder

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.k.barcode.AppContent.Companion.TAG
import org.k.barcode.model.CodeDetails

class DecoderManager private constructor(
    private val barcodeDecoder: BarcodeDecoder
) {
    private var state = DecoderState.Closed

    private var decoderEventListeners: MutableList<DecoderEventListener> = ArrayList()

    private val backgroundThread = HandlerThread("barcode_handler")

    private var barcodeDataJob: Job? = null

    init {
        backgroundThread.start()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun observeBarcodeDataFlow() {
        if (barcodeDataJob?.isActive == true) return

        barcodeDataJob = GlobalScope.launch {
            barcodeDecoder.getBarcodeFlow().collect {
                workHandler.obtainMessage(MSG_DECODE_RESULT, it).sendToTarget()
            }
        }
    }

    private fun cancelBarcodeDataFlow() {
        barcodeDataJob?.cancel()
    }

    private val workHandler = object : Handler(backgroundThread.looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_OPEN -> {
                    if (state == DecoderState.Closed) {
                        Log.d(TAG, "open barcode decoder")
                        if (barcodeDecoder.init()) {
                            observeBarcodeDataFlow()
                            state = DecoderState.Idle
                        }
                        val decoderEvent = if (state == DecoderState.Closed) DecoderEvent.Error
                        else DecoderEvent.Opened
                        synchronized(decoderEventListeners) {
                            for (listener in decoderEventListeners)
                                listener.onEvent(decoderEvent)
                        }
                    }
                }

                MSG_CLOSE -> {
                    if (state != DecoderState.Closed) {
                        Log.d(TAG, "close barcode decoder")
                        cancelBarcodeDataFlow()
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
                        Log.d(TAG, "start decode")
                        state = DecoderState.Decoding
                        barcodeDecoder.startDecode()
                    }
                }

                MSG_CANCEL_DECODER -> {
                    if (state == DecoderState.Decoding)
                        barcodeDecoder.cancelDecode()
                }

                MSG_DECODE_RESULT -> {
                    if (state == DecoderState.Decoding)
                        state = DecoderState.Idle
                }

                MSG_UPDATE_CODES -> {
                    if (state != DecoderState.Closed) {
                        val objectOfTypeAny = msg.obj
                        if (objectOfTypeAny is List<*>) {
                            val codeDetailsList = objectOfTypeAny.filterIsInstance<CodeDetails>()
                            barcodeDecoder.updateCode(codeDetailsList)
                        }
                    }
                }

                MSG_DECODER_LIGHT -> {
                    if (state != DecoderState.Closed)
                        barcodeDecoder.light(msg.obj as Boolean)
                }

                MSG_DECODE_TIMEOUT -> {
                    if (state != DecoderState.Closed)
                        barcodeDecoder.timeout(msg.obj as Int)
                }
            }
            super.handleMessage(msg)
        }
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

    fun updateCode(codeDetails: List<CodeDetails>) {
        workHandler.obtainMessage(MSG_UPDATE_CODES, codeDetails).sendToTarget()
    }

    fun supportLight() = barcodeDecoder.supportLight()

    fun supportCode() = barcodeDecoder.supportCode()

    fun getBarcodeFlow() = barcodeDecoder.getBarcodeFlow()

    fun setLight(enable: Boolean) {
        workHandler.obtainMessage(MSG_DECODER_LIGHT, enable).sendToTarget()
    }

    fun setDecodeTimeout(timeout: Int) {
        workHandler.obtainMessage(MSG_DECODE_TIMEOUT, timeout).sendToTarget()
    }

    companion object {
        private var instance: DecoderManager? = null

        //val instance: DecoderManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        //    DecoderManager()
        //}
        @Synchronized
        fun getInstance(barcodeDecoder: BarcodeDecoder): DecoderManager? {
            if (instance == null)
                instance = DecoderManager(barcodeDecoder)
            return instance
        }

        private const val MSG_OPEN = 0
        private const val MSG_CLOSE = 1
        private const val MSG_START_DECODE = 2
        private const val MSG_CANCEL_DECODER = 3
        private const val MSG_UPDATE_CODES = 4
        private const val MSG_DECODER_LIGHT = 5
        private const val MSG_DECODE_TIMEOUT = 6

        private const val MSG_DECODE_RESULT = 7
    }

    enum class DecoderState {
        Closed,
        Idle,
        Decoding
    }
}