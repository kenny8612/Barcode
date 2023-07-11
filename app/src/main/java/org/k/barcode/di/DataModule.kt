package org.k.barcode.di

import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.k.barcode.AppContent.Companion.TAG
import org.k.barcode.decoder.BarcodeDecoder
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.decoder.DecoderType
import org.k.barcode.decoder.HardDecoder
import org.k.barcode.decoder.NlsDecoder
import java.io.BufferedReader
import java.io.FileReader
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideBarcodeType(): DecoderType {
        var decoderType = DecoderType.Hard
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/scanner_type"))
            val type = reader.readLine().toInt()
            enumValues<DecoderType>().forEach {
                if (it.ordinal == type) {
                    Log.w(TAG, "found decoder $it")
                    decoderType  = it
                    return@forEach
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader?.close()
        }

        return decoderType
    }

    @Provides
    @Singleton
    fun provideBarcodeDecoder(
        @ApplicationContext context: Context,
        decoderType: DecoderType
    ): BarcodeDecoder =
        when (decoderType) {
            DecoderType.Nls -> {
                NlsDecoder(context)
            }

            else -> HardDecoder()
        }

    @Provides
    @Singleton
    fun provideDecoderManager(
        barcodeDecoder: BarcodeDecoder
    ): DecoderManager = DecoderManager.getInstance(barcodeDecoder)!!

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager =
        context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideVibrator(
        @ApplicationContext context: Context
    ): Vibrator =
        if (Build.VERSION.SDK_INT >= 31) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        }

    @Provides
    @Singleton
    fun provideKeyguardManager(
        @ApplicationContext context: Context
    ): KeyguardManager =
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    @Provides
    @Singleton
    fun providePowerManager(
        @ApplicationContext context: Context
    ): PowerManager =
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
}