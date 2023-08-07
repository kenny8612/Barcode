package org.k.barcode

import androidx.compose.runtime.MutableState
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.repository.DecoderRepository
import org.k.barcode.room.Settings
import org.k.barcode.utils.BarcodeInfoUtils.transformData

class BarcodeObserver(
    private val decoderRepository: DecoderRepository,
    val barcodeInfo: MutableState<BarcodeInfo>,
    val settings: StateFlow<Settings>
) : DefaultLifecycleObserver {
    private var barcodeJob: Job? = null

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        barcodeJob = decoderRepository.getBarcode().onEach { barcode ->
            barcode.transformData(settings.value).also { barcodeInfo.value = it }
        }.launchIn(owner.lifecycleScope)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        barcodeJob?.cancel()
    }

    fun clearBarcode() {
        barcodeInfo.value = BarcodeInfo()
    }
}