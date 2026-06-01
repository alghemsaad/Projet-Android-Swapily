package com.swapily.app

import android.app.Application
import com.cloudinary.android.MediaManager

class SwapilyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = mapOf(
            "cloud_name" to "du2fmrli0",
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}