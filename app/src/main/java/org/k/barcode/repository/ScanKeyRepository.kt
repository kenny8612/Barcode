package org.k.barcode.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import org.k.barcode.model.KeyInfo
import javax.inject.Inject

class ScanKeyRepository @Inject constructor(context: Context) {
    @OptIn(DelicateCoroutinesApi::class)
    private val _scanKey = callbackFlow {
        val scanKeyReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val keycode = intent.getIntExtra("keycode", 0)
                val action = intent.getIntExtra("action", KeyEvent.ACTION_DOWN)
                trySend(KeyInfo(keycode, action))
            }
        }
        context.registerReceiver(scanKeyReceiver, IntentFilter("action.scanner.keycode"))
        awaitClose {
            context.unregisterReceiver(scanKeyReceiver)
        }
    }.shareIn(scope = GlobalScope, started = SharingStarted.WhileSubscribed(), replay = 0)

    fun getScanKey() = _scanKey
}