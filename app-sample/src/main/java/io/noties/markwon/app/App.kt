package io.noties.markwon.app

import android.app.Application
import io.noties.debug.AndroidLogDebugOutput
import io.noties.debug.Debug
import java.util.concurrent.Executors

@Suppress("unused")
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Debug.init(AndroidLogDebugOutput(BuildConfig.DEBUG))

        sampleManager = SampleManager(this, Executors.newCachedThreadPool())
    }

    companion object {
        lateinit var sampleManager: SampleManager
    }
}