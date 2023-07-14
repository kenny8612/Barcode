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
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.k.barcode.AppContent.Companion.TAG
import org.k.barcode.AppContent.Companion.app
import org.k.barcode.AppContent.Companion.prefs
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
    lateinit var databaseRepository: DatabaseRepository

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var decoderRepository: DecoderRepository

    @Inject
    lateinit var decoderManager: DecoderManager

    private lateinit var notificationManager: NotificationManager
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var powerManager: PowerManager
    private lateinit var vibrator: Vibrator

    private lateinit var settings: Settings
    private lateinit var codeDetailsList: List<CodeDetails>

    private lateinit var notification: Notification

    private var decoderEventFlow: Job? = null
    private var barcodeFlow: Job? = null
    private var settingsFlow: Job? = null
    private var codesFlow: Job? = null

    private lateinit var soundPool: SoundPool
    private var scannerSoundId = 0
    private var continuousDecodeState = ContinuousDecodeState.Stop

    private var lowPowerConsumptionWorkUUID: UUID? = null
    private var decoderDelayOpenJob: Job? = null

    private var screenState = true
    private var cameraState = false
    private var lastDecoderTime = 0L
    private var decoderReady = false
    private var numberOfCameras = 0
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val builder = SoundPool.Builder()
        val attrBuilder = AudioAttributes.Builder()
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        builder.setAudioAttributes(attrBuilder.build())
        soundPool = builder.build()
        scannerSoundId = soundPool.load(this, R.raw.scan_buzzer, 1)

        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        numberOfCameras = cameraManager.cameraIdList.size

        setupNotification()
        loadSettingsData()
        backupSettingsData()
        settings.decoderEnable = false

        observeDecoderEventFlow()
        observeCodesFlow()
        observeSettingsFlow()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(screenReceiver, intentFilter)

        registerReceiver(scannerKeyReceiver, IntentFilter(ACTION_SCANNER_KEYCODE))

        registerDecodeReceiver(settings)
        if (decoderManager.supportCode())
            registerReceiver(cameraStateReceiver, IntentFilter(ACTION_CAMERA_STATUS))
        /** for sdk **/
        registerReceiver(decoderSettingsReceiver, IntentFilter(ACTION_SCANNER_SETTINGS))

        EventBus.getDefault().register(this)
        Log.d(TAG, "barcode service start")
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        notificationManager.cancelAll()
        notificationManager.deleteNotificationChannel(BARCODE_CHANNEL_ID)

        cancelDecoderEventFlow()
        cancelCodesFlow()
        cancelSettingsDataFlow()
        unregisterReceiver(screenReceiver)
        unregisterReceiver(scannerKeyReceiver)

        unregisterDecodeReceiver()
        if (decoderManager.supportCode())
            unregisterReceiver(cameraStateReceiver)
        unregisterReceiver(decoderSettingsReceiver)
        EventBus.getDefault().unregister(this)
        Log.d(TAG, "barcode service stop")
    }

    private fun registerDecodeReceiver(settings: Settings) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(settings.broadcastStartDecode)
        intentFilter.addAction(settings.broadcastStopDecode)
        registerReceiver(decodeReceiver, intentFilter)
    }

    private fun unregisterDecodeReceiver() {
        unregisterReceiver(decodeReceiver)
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

    private fun loadSettingsData() {
        runBlocking {
            settings = appDatabase.settingsDao().get()
            codeDetailsList = appDatabase.codeDetailDDao().getCodes()
        }
    }

    private fun backupSettingsData() {
        if (!prefs.getBoolean("settings_backup_done", false)) {
            prefs.edit()
                .putString("settings_backup", Gson().toJson(settings))
                .putBoolean("settings_backup_done", true)
                .apply()
        }
        if (!prefs.getBoolean("codes_backup_done", false)) {
            prefs.edit()
                .putString("codes_backup", Gson().toJson(codeDetailsList))
                .putBoolean("codes_backup_done", true)
                .apply()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun observeSettingsFlow() {
        if (settingsFlow?.isActive == true)
            return

        settingsFlow = databaseRepository.getSettingsFlow().onEach {
            if (it.decoderEnable != settings.decoderEnable) {
                if (it.decoderEnable) {
                    decoderManager.open()
                } else {
                    decoderManager.cancelDecode()
                    decoderManager.close()
                }
            }
            if (it.continuousDecode != settings.continuousDecode) {
                if (!it.continuousDecode)
                    continuousDecodeState = ContinuousDecodeState.Stop
            }
            if (settings.decoderEnable) {
                if (it.decoderLight != settings.decoderLight)
                    decoderManager.setLight(it.decoderLight)

                if (it.broadcastStartDecode != settings.broadcastStartDecode ||
                    it.broadcastStopDecode != settings.broadcastStopDecode
                ) {
                    unregisterDecodeReceiver()
                    registerDecodeReceiver(it)
                }
            }
            settings = it
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun cancelSettingsDataFlow() {
        settingsFlow?.cancel()
    }

    private fun observeCodesFlow() {
        if (codesFlow?.isActive == true) return

        codesFlow = databaseRepository.getCodesFlow().onEach {
            val diffCodesList = it.filter { codeDetails ->
                !codeDetailsList.contains(codeDetails)
            }
            if (diffCodesList.isNotEmpty()) {
                codeDetailsList = it
                if (settings.decoderEnable)
                    decoderManager.updateCode(diffCodesList)
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun cancelCodesFlow() {
        codesFlow?.cancel()
    }

    private fun observeDecoderEventFlow() {
        if (decoderEventFlow?.isActive == true)
            return

        decoderEventFlow = decoderRepository.getEvent().onEach {
            when (it) {
                DecoderEvent.Opened -> {
                    decoderManager.updateCode(codeDetailsList)
                    decoderManager.setLight(settings.decoderLight)
                    decoderManager.setDecodeTimeout(DEFAULT_DECODE_TIMEOUT)
                    observeBarcodeFlow()
                    decoderReady = true
                    updateNotification(serviceStart = true)
                }

                DecoderEvent.Closed -> {
                    decoderReady = false
                    updateNotification(serviceStart = false)
                    cancelBarcodeFlows()
                    continuousDecodeState = ContinuousDecodeState.Stop
                }

                DecoderEvent.Error -> {
                    if (settings.decoderEnable) {
                        settings.decoderEnable = false
                        settings.update(appDatabase)
                    }
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun cancelDecoderEventFlow() {
        decoderEventFlow?.cancel()
    }

    private fun updateNotification(serviceStart: Boolean) {
        val notification = Notification.Builder.recoverBuilder(this, notification)
            .setContentTitle(getString(if (serviceStart) R.string.service_start else R.string.service_stop))
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
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
        barcodeInfo.transformData(settings)?.run {
            if (settings.decoderSound)
                playSound()
            if (settings.decoderVibrate)
                vibrate()

            when (settings.decoderMode) {
                DecodeMode.InputBox -> barcodeInfo.injectInputBox(app, settings)
                DecodeMode.Broadcast -> barcodeInfo.broadcast(app, settings)
                DecodeMode.Simulate -> barcodeInfo.simulate(app, settings)
                DecodeMode.Clipboard -> barcodeInfo.clipboard(app)
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
                    screenState = true
                    exitLowPowerConsumption()
                    if (!isScreenLocked())
                        openDecoderDelayJob()
                }

                Intent.ACTION_SCREEN_OFF -> {
                    screenState = false
                    cancelOpenDecoderDelayJob()
                    enterLowPowerConsumption()
                }

                Intent.ACTION_USER_PRESENT -> {
                    openDecoderDelayJob()
                }
            }
        }
    }

    private val decodeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (settings.broadcastStartDecode == intent?.action) {
                startDecode(isSDK = true)
            } else if (settings.broadcastStopDecode == intent?.action) {
                cancelDecode()
            }
        }
    }

    private val decoderSettingsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_SCANNER_SETTINGS == intent?.action) {
                if (intent.hasExtra("out_mode")) {
                    val mode = intent.getIntExtra("out_mode", 0)
                    if (mode != settings.decoderMode.ordinal)
                        settings.copy(decoderMode = DecodeMode.values()[mode]).update(appDatabase)
                }
            }
        }
    }

    private val cameraStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!settings.decoderEnable) return

            if (ACTION_CAMERA_STATUS == intent?.action) {
                cameraState = intent.getBooleanExtra("state", false)
                val cameraId = intent.getStringExtra("cameraId")?.toInt()

                if (cameraId != numberOfCameras - 1) {
                    if (cameraState) {
                        cancelOpenDecoderDelayJob()
                        decoderManager.cancelDecode()
                        decoderManager.close()
                    } else {
                        openDecoderDelayJob()
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun openDecoderDelayJob() {
        if (decoderDelayOpenJob?.isActive == true) return

        decoderDelayOpenJob = GlobalScope.launch {
            delay(1000)
            if (!cameraState && screenState)
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
                .setInitialDelay(1, TimeUnit.MINUTES).build()
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

    private fun startDecode(isSDK: Boolean = false) {
        if (!settings.decoderEnable
            || !isScreenOn()
            || isScreenLocked()
            || !decoderReady
        ) return

        if (System.currentTimeMillis() - lastDecoderTime < DOUBLE_DECODE_MIN_INTERVAL)
            return

        lastDecoderTime = System.currentTimeMillis()

        if (settings.continuousDecode) {
            if (continuousDecodeState == ContinuousDecodeState.Stop) {
                continuousDecodeState = ContinuousDecodeState.Start
                decoderManager.startDecode()
            } else if (!isSDK) {
                continuousDecodeState = ContinuousDecodeState.Stop
                decoderManager.cancelDecode()
            }
        } else {
            decoderManager.startDecode()
        }
    }

    private fun cancelDecode() {
        if (settings.decoderEnable && decoderReady) {
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
            Message.UpdateSettings -> (event.arg1 as Settings).update(appDatabase)
            Message.UpdateCode -> (event.arg1 as CodeDetails).update(appDatabase)
            Message.RestoreSettings -> restoreSettings()
            Message.CloseDecoder -> decoderManager.close()
            Message.StartDecode -> startDecode()
        }
        EventBus.getDefault().removeStickyEvent(event)
    }

    private fun restoreSettings() {
        val gson = Gson()

        gson.fromJson<List<CodeDetails>>(
            prefs.getString("codes_backup", ""),
            object : TypeToken<List<CodeDetails>>() {}.type
        )?.apply {
            if (this != codeDetailsList) {
                cancelCodesFlow()
                update(appDatabase)
                runBlocking {
                    delay(500)
                    observeCodesFlow()
                }
            }
        }

        gson.fromJson(
            prefs.getString("settings_backup", ""),
            Settings::class.java
        )?.apply {
            if (this != settings)
                update(appDatabase)
        }
    }

    companion object {
        const val BARCODE_CHANNEL_ID = "BARCODE_CHANNEL_ID"
        const val NOTIFICATION_ID = 1
        const val DEFAULT_DECODE_TIMEOUT = 5000
        const val DOUBLE_DECODE_MIN_INTERVAL = 200

        const val ACTION_SCANNER_KEYCODE = "action.scanner.keycode"
        const val ACTION_SCANNER_SETTINGS = "action.scanner.settings"
        const val ACTION_CAMERA_STATUS = "com.action.camera.status"
    }

    enum class ContinuousDecodeState {
        Start,
        Stop
    }
}