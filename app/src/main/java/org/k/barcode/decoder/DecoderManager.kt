package org.k.barcode.decoder

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.k.barcode.AppContent.Companion.TAG
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails
import java.io.BufferedReader
import java.io.FileReader

class DecoderManager private constructor() {
    private var state = DecoderState.Closed

    private val backgroundThread = HandlerThread("barcode_handler")

    private var barcodeDataJob: Job? = null

    private val _eventFlow = MutableStateFlow(DecoderEvent.Closed)
    private val eventFlow: StateFlow<DecoderEvent> = _eventFlow.asStateFlow()

    private var decoder: Decoder

    init {
        backgroundThread.start()
        decoder = getBarcodeDecoder()
    }

    private fun getBarcodeDecoder(): Decoder {
        var decoderType = DecoderType.Hard
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/scanner_type"))
            val type = reader.readLine().toInt()
            enumValues<DecoderType>().forEach {
                if (it.ordinal == type) {
                    decoderType = it
                    return@forEach
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader?.close()
        }
        Log.w(TAG, "found decoder $decoderType")

        return when (decoderType) {
            DecoderType.Nls -> NlsDecoder.instance
            DecoderType.Hsm -> HsmDecoder.instance
            DecoderType.Zebra -> ZebraDecoder.instance
            else -> HardDecoder.instance
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun observeBarcodeDataFlow() {
        if (barcodeDataJob?.isActive == true) return

        barcodeDataJob = GlobalScope.launch {
            getBarcodeFlow().collect {
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
                        Log.d(TAG, "opening barcode decoder")
                        if (decoder.init()) {
                            observeBarcodeDataFlow()
                            state = DecoderState.Idle
                            Log.d(TAG, "decoder opened")
                            _eventFlow.value = DecoderEvent.Opened
                        } else {
                            Log.e(TAG, "decoder error")
                            _eventFlow.value = DecoderEvent.Error
                        }
                    }
                }

                MSG_CLOSE -> {
                    if (state != DecoderState.Closed) {
                        Log.d(TAG, "closing barcode decoder")
                        cancelBarcodeDataFlow()
                        decoder.deInit()
                        state = DecoderState.Closed
                        Log.d(TAG, "decoder closed")
                        _eventFlow.value = DecoderEvent.Closed
                    }
                }

                MSG_START_DECODE -> {
                    if (state == DecoderState.Idle) {
                        Log.d(TAG, "start decode")
                        state = DecoderState.Decoding
                        decoder.startDecode()
                    }
                }

                MSG_CANCEL_DECODER -> {
                    if (state == DecoderState.Decoding) {
                        Log.d(TAG, "canceling decode")
                        decoder.cancelDecode()
                    }
                }

                MSG_DECODE_RESULT -> {
                    if (state == DecoderState.Decoding) {
                        val barcodeInfo = msg.obj as BarcodeInfo
                        if (barcodeInfo.sourceData == null) {
                            if (barcodeInfo.decodeTime == 0L) {
                                Log.d(TAG, "decode canceled")
                            } else {
                                Log.w(TAG, "decode timeout")
                                Thread.sleep(100)
                            }
                        } else {
                            Log.d(TAG, "decode done")
                        }
                        state = DecoderState.Idle
                    }
                }

                MSG_UPDATE_CODES -> {
                    if (state != DecoderState.Closed) {
                        val objectOfTypeAny = msg.obj
                        if (objectOfTypeAny is List<*>) {
                            val codeDetailsList = objectOfTypeAny.filterIsInstance<CodeDetails>()
                            decoder.updateCode(codeDetailsList)
                        }
                    }
                }

                MSG_DECODER_LIGHT -> {
                    if (state != DecoderState.Closed)
                        decoder.light(msg.obj as Boolean)
                }

                MSG_DECODE_TIMEOUT -> {
                    if (state != DecoderState.Closed)
                        decoder.timeout(msg.obj as Int)
                }
            }
            super.handleMessage(msg)
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

    fun setLight(enable: Boolean) {
        workHandler.obtainMessage(MSG_DECODER_LIGHT, enable).sendToTarget()
    }

    fun setDecodeTimeout(timeout: Int) {
        workHandler.obtainMessage(MSG_DECODE_TIMEOUT, timeout).sendToTarget()
    }

    fun supportLight() = decoder.supportLight()

    fun supportCode() = decoder.supportCode()

    fun getBarcodeFlow() = decoder.getBarcodeFlow()

    fun getEventFlow() = eventFlow

    companion object {
        val instance: DecoderManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DecoderManager()
        }

        //private var instance: DecoderManager? = null
        //@Synchronized
        //fun getInstance(barcodeDecoder: BarcodeDecoder): DecoderManager? {
        //    if (instance == null)
        //        instance = DecoderManager(barcodeDecoder)
        //    return instance
        //}

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