package com.redc4ke.taniechlanie.data

import android.app.Application
import com.onesignal.OneSignal

class ApplicationClass : Application() {

    val ONESIGNAL_APP_ID = "f1c7d2a1-d1e7-4362-b2a0-73da60cce601"

    override fun onCreate() {
        super.onCreate()

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }

}