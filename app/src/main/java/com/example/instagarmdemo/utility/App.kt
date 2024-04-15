package com.example.instagarmdemo.utility

import android.app.Application
import com.example.instagarmdemo.BuildConfig
import timber.log.Timber


class App :Application() {
    override fun onCreate() {
        super.onCreate()

          if (BuildConfig.DEBUG) {
              Timber.plant(Timber.DebugTree())
          }

    }
}