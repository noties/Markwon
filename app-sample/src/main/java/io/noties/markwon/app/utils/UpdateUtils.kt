package io.noties.markwon.app.utils

import io.noties.markwon.app.App
import io.noties.markwon.app.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object UpdateUtils {

    sealed class Result {
        class UpdateAvailable(val url: String) : Result()
        object NoUpdate : Result()
        class Error(val throwable: Throwable) : Result()
    }

    fun checkForUpdate(updateAction: (Result) -> Unit): Cancellable {
        var action: ((Result) -> Unit)? = updateAction

        val future = App.executorService
                .submit {
                    val url = "${BuildConfig.GIT_REPOSITORY}/raw/sample-store/version"
                    val request = Request.Builder()
                            .get()
                            .url(url)
                            .build()
                    OkHttpClient().newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            action?.invoke(Result.Error(e))
                        }

                        override fun onResponse(call: Call, response: Response) {
                            try {
                                val revision = response.body()?.string()
                                val hasUpdate = revision != null && BuildConfig.GIT_SHA != revision
                                if (hasUpdate) {
                                    action?.invoke(Result.UpdateAvailable(apkUrl))
                                } else {
                                    action?.invoke(Result.NoUpdate)
                                }
                            } catch (e: IOException) {
                                action?.invoke(Result.Error(e))
                            }
                        }
                    })
                }

        return object : Cancellable {
            override val isCancelled: Boolean
                get() = future.isDone

            override fun cancel() {
                action = null
                future.cancel(true)
            }
        }
    }

    private const val apkUrl = "${BuildConfig.GIT_REPOSITORY}/raw/sample-store/markwon-debug.apk"
}