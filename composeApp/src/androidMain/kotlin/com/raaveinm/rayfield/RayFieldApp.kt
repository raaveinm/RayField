package com.raaveinm.rayfield

import android.app.Application
import com.raaveinm.rayfield.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class RayFieldApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@RayFieldApp)
        }
    }
}
