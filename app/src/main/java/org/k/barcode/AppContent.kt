package org.k.barcode

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import org.k.barcode.ui.MainActivity

@HiltAndroidApp
class AppContent : Application() {
    override fun onCreate() {
        super.onCreate()
        //开启Thread策略模式
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectNetwork() //监测主线程使用网络io
                    .detectCustomSlowCalls() //监测自定义运行缓慢函数
                    //.detectDiskReads() // 检测在UI线程读磁盘操作
                    //.detectDiskWrites() // 检测在UI线程写磁盘操作
                    .penaltyLog() //写入日志
                    .penaltyDialog() //监测到上述状况时弹出对话框
                    .build()
            )
            //开启VM策略模式
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects() //监测sqlite泄露
                    .detectLeakedClosableObjects() //监测没有关闭IO对象
                    .setClassInstanceLimit(
                        MainActivity::class.java,
                        1
                    ) // 设置某个类的同时处于内存中的实例上限，可以协助检查内存泄露
                    .detectActivityLeaks()
                    .penaltyLog() //写入日志
                    .build()
            )
        }
    }

    companion object {
        const val TAG = "BarcodeDecoder"
    }
}