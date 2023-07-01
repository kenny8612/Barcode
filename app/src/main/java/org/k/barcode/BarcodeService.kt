package org.k.barcode

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.k.barcode.data.AppDatabase
import org.k.barcode.model.CodeDetails
import org.k.barcode.model.Settings
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.data.DecoderRepository
import org.k.barcode.decoder.BarcodeListener
import org.k.barcode.decoder.DecodeMode
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.KeyEventEx
import org.k.barcode.utils.BarcodeUtils.toKeyEvents
import java.nio.charset.Charset
import javax.inject.Inject

@AndroidEntryPoint
class BarcodeService : Service() {
    @Inject
    lateinit var decoderManager: DecoderManager

    @Inject
    lateinit var databaseRepository: DatabaseRepository

    @Inject
    lateinit var decoderRepository: DecoderRepository

    @Inject
    lateinit var appDatabase: AppDatabase

    lateinit var settings: Settings

    private var decoderEventFlow: Job? = null
    private var barcodeFlow: Job? = null
    private var dataBaseFlow: Job? = null

    private lateinit var soundPool: SoundPool
    private var scannerSoundId = 0
    private var continuousDecodeState = ContinuousDecodeState.Stop
    private var barcodeListener: BarcodeListener? = null

    private var settingsUI = false

    inner class BarcodeServiceBind : Binder() {
        fun addBarcodeInfoListener(barcodeListener: BarcodeListener) {
            this@BarcodeService.barcodeListener = barcodeListener
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return BarcodeServiceBind()
    }

    override fun onCreate() {
        super.onCreate()

        runBlocking {
            if (PreferenceManager.getDefaultSharedPreferences(this@BarcodeService)
                    .getBoolean("first_init", true)
            ) {
                appDatabase.openHelper.writableDatabase.execSQL("CREATE TABLE settings_bak AS SELECT * FROM settings")
                appDatabase.openHelper.writableDatabase.execSQL("CREATE TABLE codeDetails_bak AS SELECT * FROM codeDetails")
                PreferenceManager.getDefaultSharedPreferences(this@BarcodeService).edit()
                    .putBoolean("first_init", false).apply()
            }
        }

        val builder = SoundPool.Builder()
        val attrBuilder = AudioAttributes.Builder()
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        builder.setAudioAttributes(attrBuilder.build())
        soundPool = builder.build()
        scannerSoundId = soundPool.load(this, R.raw.scan_buzzer, 1)

        registerScannerKeyReceiver()
        observeDecoderEventFlow()
        observeDatabaseFlow()
        EventBus.getDefault().register(this)
        println("barcode service started!")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterScannerKeyReceiver()
        soundPool.release()
        EventBus.getDefault().unregister(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun observeDatabaseFlow() {
        if (dataBaseFlow?.isActive == true)
            return

        dataBaseFlow = databaseRepository.settings.onEach {
            settings = it
            if (it.decoderEnable) {
                decoderManager.open()
            } else {
                decoderManager.cancelDecode()
                decoderManager.close()
            }
            if (!it.continuousDecode)
                continuousDecodeState = ContinuousDecodeState.Stop
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun observeDecoderEventFlow() {
        if (decoderEventFlow?.isActive == true)
            return

        decoderEventFlow = decoderRepository.getEvent()
            .onEach {
                when (it) {
                    DecoderEvent.Opened -> {
                        println("decoder opened")
                        observeBarcodeFlow()
                    }

                    DecoderEvent.Closed -> {
                        println("decoder closed")
                        cancelBarcodeFlows()
                        continuousDecodeState = ContinuousDecodeState.Stop
                    }

                    DecoderEvent.DecodeTimeout -> {
                        println("decode timeout")
                        continuousDecode()
                    }
                }
            }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private suspend fun continuousDecode() {
        if (settings.continuousDecode &&
            continuousDecodeState == ContinuousDecodeState.Start
        ) {
            delay(settings.continuousDecodeInterval.toLong())
            decoderManager.startDecode()
        }
    }

    private fun observeBarcodeFlow() {
        if (barcodeFlow?.isActive == true)
            return

        barcodeFlow = decoderRepository.getBarcode().onEach {
            parseBarcode(it)
            continuousDecode()
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun cancelBarcodeFlows() {
        barcodeFlow?.cancel()
    }

    private fun registerScannerKeyReceiver() {
        registerReceiver(scannerKeyReceiver, IntentFilter(ACTION_SCANNER_KEYCODE))
    }

    private fun unregisterScannerKeyReceiver() {
        unregisterReceiver(scannerKeyReceiver)
    }

    private val scannerKeyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_SCANNER_KEYCODE == intent?.action) {
                if (settings.decoderEnable && !settingsUI) {
                    val action = intent.getIntExtra("action", KeyEvent.ACTION_DOWN)
                    if (action == KeyEvent.ACTION_DOWN) {
                        startDecode()
                    } else {
                        if (!settings.continuousDecode)
                            cancelDecode()
                    }
                }
            }
        }
    }

    private fun startDecode() {
        if (settingsUI) return

        if (settings.continuousDecode) {
            continuousDecodeState = if (continuousDecodeState == ContinuousDecodeState.Stop) {
                decoderManager.startDecode()
                ContinuousDecodeState.Start
            } else {
                cancelDecode()
                ContinuousDecodeState.Stop
            }
        } else {
            decoderManager.startDecode()
        }
    }

    private fun cancelDecode() {
        decoderManager.cancelDecode()
        continuousDecodeState = ContinuousDecodeState.Stop
    }

    private fun parseBarcode(barcodeInfo: BarcodeInfo) {
        var formatData = String(barcodeInfo.sourceData, 0, barcodeInfo.sourceData.size)
        if (settings.decoderCharset != "AUTO") {
            try {
                formatData = String(
                    barcodeInfo.sourceData,
                    0,
                    barcodeInfo.sourceData.size,
                    Charset.forName(settings.decoderCharset)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (settings.decoderPrefix.isNotEmpty())
            formatData = settings.decoderPrefix + formatData
        if (settings.decodeSuffix.isNotEmpty())
            formatData += settings.decodeSuffix

        if (settings.decoderFilterCharacters.isNotEmpty())
            formatData = formatData.replace(settings.decoderFilterCharacters, "")

        barcodeInfo.formatData = formatData

        if (settings.decoderSound)
            playSound()
        if (settings.decoderVibrate)
            vibrate()

        when (settings.decoderMode) {
            DecodeMode.Focus -> {
                val intent = Intent(ACTION_INPUT_INJECT)
                intent.putExtra("content", barcodeInfo.formatData)
                intent.putExtra("simulateKeyboard", settings.attachKeycode != 0)
                intent.putExtra("simulateKeyboard_keycode", settings.attachKeycode)
                intent.putExtra("deleteSurroundingText", false)
                sendBroadcast(intent)
            }

            DecodeMode.Broadcast -> {
                val intent = Intent(ACTION_DECODE_DATA)
                intent.putExtra("source_byte", barcodeInfo.sourceData)
                intent.putExtra("barcode_string", barcodeInfo.formatData)
                intent.putExtra("decode_time", barcodeInfo.decodeTime)
                intent.putExtra("aim_string", barcodeInfo.aim)
                sendBroadcast(intent)
            }

            DecodeMode.Simulate -> {
                val keyList = barcodeInfo.toKeyEvents()
                for (key in keyList) {
                    simulateKey(key)
                    Thread.sleep(10)
                }
                if (settings.attachKeycode != 0)
                    simulateKey(KeyEventEx(settings.attachKeycode))
            }

            DecodeMode.Clipboard -> {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        "BarcodeText",
                        barcodeInfo.formatData
                    )
                )
            }
        }

        barcodeListener?.onBarcode(barcodeInfo) // for scan test
    }

    private fun playSound() {
        soundPool.play(scannerSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    50,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }

    private fun simulateKey(keyEventEx: KeyEventEx) {
        val intent = Intent(ACTION_INPUT_INJECT)
        intent.putExtra("content", "")
        intent.putExtra("simulateKeyboard", true)
        intent.putExtra("simulateKeyboard_keycode", keyEventEx.keycode)
        intent.putExtra("shift", keyEventEx.shift)
        sendBroadcast(intent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    fun onEventMessage(event: MessageEvent) {
        if (event.message == Message.SettingsUI) {
            settingsUI = event.arg1 as Boolean
            if (settingsUI) cancelDecode()
        } else if (event.message == Message.UpdateSettings) {
            GlobalScope.launch {
                appDatabase.settingsDao().update(event.arg1 as Settings)
            }
        } else if (event.message == Message.UpdateCode) {
            GlobalScope.launch {
                appDatabase.codeDetailDDao().update(event.arg1 as CodeDetails)
            }
        } else if (event.message == Message.RestoreSettings) {
            GlobalScope.launch {
                //appDatabase.settingsDao().get(2).also {
                //    appDatabase.settingsDao().update(it.also { it.uid = settings.uid })
                //}
                appDatabase.openHelper.writableDatabase.execSQL("DROP TABLE settings")
                appDatabase.openHelper.writableDatabase.execSQL("CREATE TABLE settings AS SELECT * FROM settings_bak")
                //appDatabase.openHelper.writableDatabase.execSQL("INSERT OR IGNORE INTO settings SELECT * FROM settings_bak")
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }

    companion object {
        private const val ACTION_SCANNER_KEYCODE = "action.scanner.keycode"
        private const val ACTION_INPUT_INJECT = "com.action.INPUT_INJECT"
        const val ACTION_DECODE_DATA = "com.action.DECODE_DATA"
    }

    enum class ContinuousDecodeState {
        Start,
        Stop
    }
}