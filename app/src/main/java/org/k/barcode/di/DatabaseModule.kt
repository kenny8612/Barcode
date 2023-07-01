package org.k.barcode.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.k.barcode.data.AppDatabase
import org.k.barcode.data.SettingsDao
import org.k.barcode.data.CodeDetailDDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "decoder.db")
            .createFromAsset("database/decoder.db").build()

    @Provides
    @Singleton
    fun provideBaseDao(
        database: AppDatabase
    ): SettingsDao = database.settingsDao()

    @Provides
    @Singleton
    fun provideCodeDao(
        database: AppDatabase
    ): CodeDetailDDao = database.codeDetailDDao()
}