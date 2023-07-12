package org.k.barcode.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.k.barcode.data.AppDatabase
import org.k.barcode.decoder.DecoderManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "decoder.db")
            .createFromAsset("database/decoder.db").build()

    @Provides
    @Singleton
    fun provideDecoderManager() = DecoderManager.instance
}