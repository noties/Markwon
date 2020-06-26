package io.noties.markwon.app.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.noties.markwon.app.App
import io.noties.markwon.app.R
import io.noties.markwon.app.sample.Sample
import io.noties.markwon.app.utils.hidden
import io.noties.markwon.app.utils.readCode
import io.noties.markwon.syntax.Prism4jSyntaxHighlight
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle

@PrismBundle(include = ["java", "kotlin"], grammarLocatorClassName = ".GrammarLocatorSourceCode")
class SampleCodeFragment : Fragment() {

    private lateinit var progressBar: View
    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sample_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progress_bar)
        textView = view.findViewById(R.id.text_view)

        load()
    }

    private fun load() {
        App.executorService.submit {
            val code = sample.readCode(requireContext())
            val prism = Prism4j(GrammarLocatorSourceCode())
            val highlight = Prism4jSyntaxHighlight.create(prism, Prism4jThemeDefault.create(0))
            val language = when (code.language) {
                Sample.Language.KOTLIN -> "kotlin"
                Sample.Language.JAVA -> "java"
            }
            val text = highlight.highlight(language, code.sourceCode)

            textView.post {
                //attached
                if (context != null) {
                    progressBar.hidden = true
                    textView.text = text
                }
            }
        }
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