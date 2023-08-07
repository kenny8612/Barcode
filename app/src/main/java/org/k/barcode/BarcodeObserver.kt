package org.k.barcode

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.k.barcode.repository.DecoderRepository
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.room.Settings
import org.k.barcode.utils.BarcodeInfoUtils.transformData

class BarcodeObserver(
    private val decoderRepository: DecoderRepository,
    val settings: StateFlow<Settings>
) : DefaultLifecycleObserver {
    private var barcodeJob: Job? = null

    var barcodeList = mutableStateListOf<String>()
    var barcodeInfo = mutableStateOf(BarcodeInfo())

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        barcodeJob = decoderRepository.getBarcode().onEach { barcode ->
            barcode.transformData(settings.value).also {
                it.formatData?.apply {
                    barcodeList.add(this)
                    if (barcodeList.size > 1000)
                        barcodeList.clear()
                }
                barcodeInfo.value = it
            }
        }.launchIn(owner.lifecycleScope)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        barcodeJob?.cancel()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        barcodeList.clear()
    }

    fun clearBarcode() {
        if (barcodeList.isNotEmpty())
            barcodeList.clear()
        barcodeInfo.value = BarcodeInfo()
    }
}