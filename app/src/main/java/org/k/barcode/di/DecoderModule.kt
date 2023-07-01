package org.k.barcode.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.k.barcode.decoder.DecoderManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DecoderModule {
    @Provides
    @Singleton
    fun provideDecoderManager(
    ): DecoderManager = DecoderManager.instance
}