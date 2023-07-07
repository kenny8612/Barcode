package org.k.barcode

import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.k.barcode.AppContent.Companion.TAG
import org.k.barcode.Constant.BARCODE_CHANNEL_ID
import org.k.barcode.Constant.DEFAULT_DECODE_TIMEOUT
import org.k.barcode.Constant.NOTIFICATION_ID
import org.k.barcode.data.AppDatabase
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.data.DecoderRepository
import org.k.barcode.decoder.DecodeMode
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.CodeDetails
import org.k.barcode.model.Settings
import org.k.barcode.ui.MainActivity
import org.k.barcode.utils.BarcodeInfoUtils.broadcast
import org.k.barcode.utils.BarcodeInfoUtils.clipboard
import org.k.barcode.utils.BarcodeInfoUtils.injectInputBox
import org.k.barcode.utils.BarcodeInfoUtils.simulate
import org.k.barcode.utils.BarcodeInfoUtils.transformData
import org.k.barcode.utils.DatabaseUtils.update
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class BarcodeService : Service() {
    @Inject
    lateinit var decoderManager: DecoderManager

    @Inject
    lateinit var databaseRepository: DatabaseRepository

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var decoderRepository: DecoderRepository

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var keyguardManager: KeyguardManager

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    lateinit var vibrator: Vibrator

    private lateinit var settings: Settings

    private lateinit var notification: Notification

    private var decoderEventFlow: Job? = null
    private var barcodeFlow: Job? = null
    private var settingsDataFlow: Job? = null

    private lateinit var soundPool: SoundPool
    private var scannerSoundId = 0
    private var continuousDecodeState = ContinuousDecodeState.Stop

    private var lowPowerConsumptionWorkUUID: UUID? = null
    private var decoderDelayOpenJob: Job? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val builder = SoundPool.Builder()
        val attrBuilder = AudioAttributes.Builder()
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        builder.setAudioAttributes(attrBuilder.build())
        soundPool = builder.build()
        scannerSoundId = soundPool.load(this, R.raw.scan_buzzer, 1)

        backupData()
        setupNotification()
        registerReceiver(scannerKeyReceiver, IntentFilter(ACTION_SCANNER_KEYCODE))
        registerReceiver(decoderControlReceiver, IntentFilter(ACTION_SCANNER_CONTROL))

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(screenReceiver, intentFilter)

        observeSettingsDataFlow()
        observeDecoderEventFlow()
        EventBus.getDefault().register(this)
        Log.d(TAG, "barcode service start")
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        notificationManager.cancelAll()
        notificationManager.deleteNotificationChannel(BARCODE_CHANNEL_ID)
        unregisterReceiver(scannerKeyReceiver)
        unregisterReceiver(screenReceiver)
        cancelSettingsDataFlow()
        cancelDecoderEventFlow()
        EventBus.getDefault().unregister(this)
        Log.d(TAG, "barcode service stop")
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun backupData() {
        GlobalScope.launch {
            if (!prefs.getBoolean("settings_backup_done", false)) {
                val settings = databaseRepository.getSettings()
                prefs.edit()
                    .putString("settings_backup", Gson().toJson(settings))
                    .putBoolean("settings_backup_done", true)
                    .apply()
            }
            if (!prefs.getBoolean("codes_backup_done", false)) {
                val codes = databaseRepository.getCodes()
                prefs.edit()
                    .putString("codes_backup", Gson().toJson(codes))
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
                decoderManager.open()
            } else {
                decoderManager.cancelDecode()
                decoderManager.close()
            }

            if (!it.continuousDecode)
                continuousDecodeState = ContinuousDecodeState.Stop
            if (decoderManager.supportLight())
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

                        decoderManager.updateCode(databaseRepository.getCodes())
                        decoderManager.setLight(settings.decoderLight)
                        decoderManager.setDecodeTimeout(DEFAULT_DECODE_TIMEOUT)

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

                    DecoderEvent.Error -> {
                        println("decoder error")
                        settings.copy(decoderEnable = false).update(appDatabase)
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

    private suspend fun parseBarcode(barcodeInfo: BarcodeInfo) {
        if (barcodeInfo.sourceData == null) {
            if (barcodeInfo.decodeTime == 0L) {
                Log.d(TAG, "decode cancel")
            } else {
                Log.d(TAG, "decode timeout")
                delay(300)
            }
            return
        }

        barcodeInfo.transformData(settings)
        if (settings.decoderSound)
            playSound()
        if (settings.decoderVibrate)
            vibrate()

        when (settings.decoderMode) {
            DecodeMode.Focus -> {
                barcodeInfo.injectInputBox(this, settings)
            }

            DecodeMode.Broadcast -> {
                barcodeInfo.broadcast(this)
            }

            DecodeMode.Simulate -> {
                barcodeInfo.simulate(this, settings)
            }

            DecodeMode.Clipboard -> {
                barcodeInfo.clipboard(this)
            }
        }
    }

    private val scannerKeyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_SCANNER_KEYCODE == intent?.action) {
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

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!settings.decoderEnable) return

            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    exitLowPowerConsumption()
                    if (!isScreenLocked())
                        openDecoderDelayJob()
                }

                Intent.ACTION_SCREEN_OFF -> {
                    cancelOpenDecoderDelayJob()
                    enterLowPowerConsumption()
                }

                Intent.ACTION_USER_PRESENT -> {
                    openDecoderDelayJob()
                }
            }
        }
    }

    private val decoderControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_SCANNER_CONTROL == intent?.action) {
                val decode = intent.getBooleanExtra("decode", false)


                if (decode) {
                    if (settings.decoderEnable)
                        startDecode()
                } else {
                    cancelDecode()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun openDecoderDelayJob() {
        if (decoderDelayOpenJob?.isActive == true) return

        decoderDelayOpenJob = GlobalScope.launch {
            delay(1000)
            //camera not open
            decoderManager.open()
        }
    }

    private fun cancelOpenDecoderDelayJob() {
        decoderDelayOpenJob?.cancel()
    }

    private fun isScreenLocked() = keyguardManager.isKeyguardLocked

    private fun isScreenOn() = powerManager.isInteractive

    private fun enterLowPowerConsumption() {
        if (lowPowerConsumptionWorkUUID == null) {
            val request = OneTimeWorkRequest.Builder(LowPowerConsumptionWork::class.java)
                .setInitialDelay(5, TimeUnit.SECONDS).build()
            lowPowerConsumptionWorkUUID = request.id
            WorkManager.getInstance(this).enqueue(request)
        }
    }

    private fun exitLowPowerConsumption() {
        lowPowerConsumptionWorkUUID?.let {
            WorkManager.getInstance(this).cancelWorkById(it)
            lowPowerConsumptionWorkUUID = null
        }
    }

    class LowPowerConsumptionWork(context: Context, workerParams: WorkerParameters) : Worker(
        context,
        workerParams
    ) {
        override fun doWork(): Result {
            EventBus.getDefault().post(MessageEvent(Message.CloseDecoder))
            return Result.success()
        }
    }

    private fun startDecode() {
        if (!settings.decoderEnable || !isScreenOn() || isScreenLocked()) return

        if (settings.continuousDecode) {
            continuousDecodeState = if (continuousDecodeState == ContinuousDecodeState.Stop) {
                decoderManager.startDecode()
                ContinuousDecodeState.Start
            } else {
                decoderManager.cancelDecode()
                ContinuousDecodeState.Stop
            }
        } else {
            decoderManager.startDecode()
        }
    }

    private fun cancelDecode() {
        if (settings.decoderEnable) {
            decoderManager.cancelDecode()
            continuousDecodeState = ContinuousDecodeState.Stop
        }
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

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    fun onEventMessage(event: MessageEvent) {
        when (event.message) {
            //Message.UpdateCode -> {
            //    val codeDetailsList = arrayListOf(event.arg1 as CodeDetails)
            //    codeDetailsList.update(appDatabase)
            //    decoderManager.updateCode(codeDetailsList)
            //}

            Message.RestoreSettings -> {
                val gson = Gson()

                gson.fromJson(
                    prefs.getString("settings_backup", ""),
                    Settings::class.java
                )?.update(appDatabase)
                gson.fromJson<List<CodeDetails>>(
                    prefs.getString("codes_backup", ""),
                    object : TypeToken<List<CodeDetails>>() {}.type
                )?.let {
                    it.update(appDatabase)
                    decoderManager.updateCode(it)
                }
            }

            Message.CloseDecoder -> {
                decoderManager.close()
            }

            Message.StartDecode -> {
                startDecode()
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }

    companion object {
        const val ACTION_SCANNER_KEYCODE = "action.scanner.keycode"
        const val ACTION_SCANNER_CONTROL = "action.scanner.control"
    }

    enum class ContinuousDecodeState {
        Start,
        Stop
    }
}