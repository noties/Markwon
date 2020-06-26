package io.noties.markwon.app.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.noties.markwon.app.sample.Sample

class SamplePreviewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return markwonSample.createView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        markwonSample.onViewCreated(view)
    }

    private val markwonSample: MarkwonSample by lazy(LazyThreadSafetyMode.NONE) {
        val sample: Sample = arguments!!.getParcelable<Sample>(ARG_SAMPLE)!!
        Class.forName(sample.javaClassName).newInstance() as MarkwonSample
    }

    companion object {
        private const val ARG_SAMPLE = "arg.Sample"

        fun init(sample: Sample): SamplePreviewFragment {
            return SamplePreviewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SAMPLE, sample)
                }
            }
        }
    }
}