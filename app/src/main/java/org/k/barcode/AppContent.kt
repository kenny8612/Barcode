package org.k.barcode

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppContent : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        lateinit var app: Application
            private set
    }
}