package org.k.barcode

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Binder
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import org.k.barcode.Constant.BARCODE_CHANNEL_ID
import org.k.barcode.Constant.NOTIFICATION_ID
import org.k.barcode.data.AppDatabase
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.data.DecoderRepository
import org.k.barcode.decoder.BarcodeListener
import org.k.barcode.decoder.DecodeMode
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails
import org.k.barcode.model.Settings
import org.k.barcode.ui.MainActivity
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

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var notificationManager:NotificationManager

    @Inject
    lateinit var vibrator:Vibrator

    private lateinit var settings: Settings

    private lateinit var notification: Notification

    private var decoderEventFlow: Job? = null
    private var barcodeFlow: Job? = null
    private var settingsDataFlow: Job? = null

    private lateinit var soundPool: SoundPool
    private var scannerSoundId = 0
    private var continuousDecodeState = ContinuousDecodeState.Stop
    private var barcodeListener: BarcodeListener? = null

    private var settingsUI = false

    inner class BarcodeServiceBind : Binder() {
        fun addBarcodeInfoListener(barcodeListener: BarcodeListener) {
            this@BarcodeService.barcodeListener = barcodeListener
        }

        fun startDecode() {
            this@BarcodeService.startDecode()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return BarcodeServiceBind()
    }

    override fun onCreate() {
        super.onCreate()

        val builder = SoundPool.Builder()
        val attrBuilder = AudioAttributes.Builder()
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        builder.setAudioAttributes(attrBuilder.build())
        soundPool = builder.build()
        scannerSoundId = soundPool.load(this, R.raw.scan_buzzer, 1)
        runBlocking { settings = databaseRepository.getSettings() }

        setupNotification()
        backupData()
        registerScannerKeyReceiver()
        observeSettingsDataFlow()
        observeDecoderEventFlow()
        EventBus.getDefault().register(this)
        println("barcode service started!")
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        unregisterScannerKeyReceiver()
        cancelSettingsDataFlow()
        cancelDecoderEventFlow()
        EventBus.getDefault().unregister(this)
    }

    private fun setupNotification() {
        val channel = NotificationChannel(
            BARCODE_CHANNEL_ID,
            getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW
        )
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        notification = Notification.Builder(this, BARCODE_CHANNEL_ID)
            .setChannelId(BARCODE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_qr)
            .setContentTitle(getString(R.string.service_stop))
            .setContentIntent(pendingIntent)
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setOngoing(true)
            .build()
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
    }

    private fun backupData() {
        if (!prefs.getBoolean("settings_backup_done", false)) {
            runBlocking {
                databaseRepository.getSettings()
            }.also {
                prefs.edit()
                    .putString("settings_backup", Gson().toJson(it))
                    .putBoolean("settings_backup_done", true)
                    .apply()
            }
        }
        if (!prefs.getBoolean("codes_backup_done", false)) {
            runBlocking {
                databaseRepository.getCodes()
            }.also {
                prefs.edit()
                    .putString("codes_backup", Gson().toJson(it))
                    .putBoolean("codes_backup_done", true)
                    .apply()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun observeSettingsDataFlow() {
        if (settingsDataFlow?.isActive == true)
            return

        settingsDataFlow = databaseRepository.getSettingsFlow().onEach {
            if (it.decoderEnable) {
                decoderManager.open(databaseRepository.getCodes())
            } else {
                decoderManager.cancelDecode()
                decoderManager.close()
            }

            if (!it.continuousDecode)
                continuousDecodeState = ContinuousDecodeState.Stop
            if (settings.decoderLight != it.decoderLight)
                decoderManager.setLight(it.decoderLight)

            settings = it
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun cancelSettingsDataFlow() {
        settingsDataFlow?.cancel()
    }

    private fun observeDecoderEventFlow() {
        if (decoderEventFlow?.isActive == true)
            return

        decoderEventFlow = decoderRepository.getEvent()
            .onEach {
                when (it) {
                    DecoderEvent.Opened -> {
                        println("decoder opened")
                        val notification = Notification.Builder.recoverBuilder(this, notification)
                                .setContentTitle(getString(R.string.service_start))
                                .build()
                        notificationManager.notify(NOTIFICATION_ID, notification)
                        observeBarcodeFlow()
                    }

                    DecoderEvent.Closed -> {
                        println("decoder closed")
                        val notification = Notification.Builder.recoverBuilder(this, notification)
                            .setContentTitle(getString(R.string.service_stop))
                            .build()
                        notificationManager.notify(NOTIFICATION_ID, notification)
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

    private fun cancelDecoderEventFlow() {
        decoderEventFlow?.cancel()
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
                        if (!settings.continuousDecode &&
                            !settings.releaseDecode
                        ) cancelDecode()
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
                updateCode(arrayListOf(event.arg1 as CodeDetails))
            }
        } else if (event.message == Message.RestoreSettings) {
            GlobalScope.launch {
                val gson = Gson()

                gson.fromJson(
                    prefs.getString("settings_backup", ""),
                    Settings::class.java
                )?.let { appDatabase.settingsDao().update(it) }

                gson.fromJson<List<CodeDetails>>(
                    prefs.getString("codes_backup", ""),
                    object : TypeToken<List<CodeDetails>>() {}.type
                )?.let { updateCode(it) }
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateCode(codesList: List<CodeDetails>) {
        GlobalScope.launch {
            for (codeDetails in codesList)
                appDatabase.codeDetailDDao().update(codeDetails)
            decoderManager.updateCode(codesList)
        }
    }

    private fun BarcodeInfo.toKeyEvents(): MutableList<KeyEventEx> {
        val keyList: MutableList<KeyEventEx> = ArrayList()
        for (b in this.sourceData) {
            when (b.toInt()) {
                in 65..90 -> {
                    keyList.add(KeyEventEx(b.toInt() - 36, true))
                }

                in 97..122 -> {
                    keyList.add(KeyEventEx(b.toInt() - 68))
                }

                in 48..57 -> {
                    keyList.add(KeyEventEx(b.toInt() - 41))
                }

                32 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SPACE))
                }

                33 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_1, true))
                }

                34 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_APOSTROPHE, true))
                }

                35 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_POUND))
                }

                36 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_4, true))
                }

                37 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_5, true))
                }

                38 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_7, true))
                }

                39 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_APOSTROPHE))
                }

                40 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_9, true))
                }

                41 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_0, true))
                }

                42 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_STAR))
                }

                43 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_PLUS))
                }

                44 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_COMMA))
                }

                45 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_MINUS))
                }

                46 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_PERIOD))
                }

                47 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SLASH))
                }

                58 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SEMICOLON, true))
                }

                59 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SEMICOLON))
                }

                60 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_COMMA, true))
                }

                61 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_EQUALS))
                }

                62 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_PERIOD, true))
                }

                63 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SLASH, true))
                }

                64 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_AT))
                }

                91 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_LEFT_BRACKET))
                }

                92 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_BACKSLASH))
                }

                93 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_RIGHT_BRACKET))
                }

                94 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_6, true))
                }

                95 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_MINUS, true))
                }

                96 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_GRAVE))
                }

                123 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_LEFT_BRACKET, true))
                }

                124 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_BACKSLASH, true))
                }

                125 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_RIGHT_BRACKET, true))
                }

                126 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_GRAVE, true))
                }
            }
        }
        return keyList
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

    data class KeyEventEx(val keycode: Int, val shift: Boolean = false)
}