package io.noties.markwon.app.sample

import android.os.Bundle
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.noties.debug.Debug
import io.noties.markwon.app.App
import io.noties.markwon.app.sample.ui.SampleFragment
import io.noties.markwon.app.sample.ui.SampleListFragment

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportFragmentManager.findFragmentById(Window.ID_ANDROID_CONTENT) == null) {

            supportFragmentManager.beginTransaction()
                    .add(Window.ID_ANDROID_CONTENT, SampleListFragment.init())
                    .commitNowAllowingStateLoss()

            // process deeplink if we are not restored
            val deeplink = Deeplink.parse(intent.data)

            val deepLinkFragment: Fragment? = if (deeplink != null) {
                when (deeplink) {
                    is Deeplink.Sample -> App.sampleManager.sample(deeplink.id)
                            ?.let { SampleFragment.init(it) }
                    is Deeplink.Search -> SampleListFragment.init(deeplink.search)
                }
            } else null

            if (deepLinkFragment != null) {
                supportFragmentManager.beginTransaction()
                        .replace(Window.ID_ANDROID_CONTENT, deepLinkFragment)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }
}