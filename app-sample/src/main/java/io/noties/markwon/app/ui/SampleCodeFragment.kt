package io.noties.markwon.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.noties.markwon.app.R
import io.noties.markwon.app.Sample
import io.noties.markwon.app.utils.readCode

class SampleCodeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sample_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = view.findViewById(R.id.text_view)
        val code = sample.readCode(requireContext())

        textView.text = code.sourceCode
    }

    private val sample: Sample by lazy(LazyThreadSafetyMode.NONE) {
        arguments!!.getParcelable<Sample>(ARG_SAMPLE)
    }

    companion object {
        private const val ARG_SAMPLE = "arg.Sample"

        fun init(sample: Sample): SampleCodeFragment {
            return SampleCodeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SAMPLE, sample)
                }
            }
        }
    }
}