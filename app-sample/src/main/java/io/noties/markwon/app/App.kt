package io.noties.markwon.app

import android.app.Application
import io.noties.debug.AndroidLogDebugOutput
import io.noties.debug.Debug
import io.noties.markwon.app.sample.SampleManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("unused")
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Debug.init(AndroidLogDebugOutput(BuildConfig.DEBUG))

        executorService = Executors.newCachedThreadPool()
        sampleManager = SampleManager(this, executorService)
    }

    companion object {
        lateinit var executorService: ExecutorService
        lateinit var sampleManager: SampleManager
    }
}