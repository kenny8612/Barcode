package org.k.barcode.decoder

import android.content.Context
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.k.barcode.AppContent
import org.k.barcode.model.BarcodeInfo

abstract class BaseDecoder : Decoder {
    private var startDecodeTime = 0L

    private val _barcodeFlow = MutableSharedFlow<BarcodeInfo>(
        replay = 0,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val barcodeFlow: SharedFlow<BarcodeInfo> = _barcodeFlow.asSharedFlow()

    protected var numberOfCameras = 0

    init {
        val cameraManager = AppContent.app.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        numberOfCameras = cameraManager.cameraIdList.size
    }

    override fun startDecode() {
        startDecodeTime = System.currentTimeMillis()
    }

    override fun getBarcodeFlow(): Flow<BarcodeInfo> = barcodeFlow

    fun sendBarcodeInfo(sourceData: ByteArray, aim: String? = null) {
        _barcodeFlow.tryEmit(
            BarcodeInfo(
                sourceData,
                aim,
                System.currentTimeMillis() - startDecodeTime
            )
        )
    }

    fun notifyTimeout() {
        _barcodeFlow.tryEmit(BarcodeInfo(decodeTime = System.currentTimeMillis()-startDecodeTime))
    }

    fun notifyCancel() {
        _barcodeFlow.tryEmit(BarcodeInfo())
    }
}