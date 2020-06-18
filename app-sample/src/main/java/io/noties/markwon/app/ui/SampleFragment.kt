package io.noties.markwon.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import io.noties.markwon.app.Sample

class SampleFragment : Fragment() {

    companion object {
        private const val ARG_SAMPLE = "arg.Sample"

        fun init(sample: Sample): SampleFragment {
            val fragment = SampleFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_SAMPLE, sample)
            }
            return fragment
        }
    }
}