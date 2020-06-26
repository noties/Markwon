package io.noties.markwon.app.sample

import android.os.Bundle
import android.view.Window
import androidx.fragment.app.FragmentActivity
import io.noties.markwon.app.sample.ui.SampleListFragment

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportFragmentManager.findFragmentById(Window.ID_ANDROID_CONTENT) == null) {
            supportFragmentManager.beginTransaction()
                    .add(Window.ID_ANDROID_CONTENT, SampleListFragment.init())
                    .commitNowAllowingStateLoss()
        }
    }
}