package io.noties.markwon.app

import android.content.Context
import android.os.Build
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.app.sample.Sample
import io.noties.markwon.app.sample.ui.MarkwonSample
import io.noties.markwon.app.utils.SampleUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(org.robolectric.ParameterizedRobolectricTestRunner::class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = [Build.VERSION_CODES.O])
class AllSamples(private val sample: Sample) {

    @Test
    fun sample() {
        val markwonSample: MarkwonSample = Class.forName(sample.javaClassName).newInstance() as MarkwonSample

        val inflater = mock(LayoutInflater::class.java).apply {
            // mock must be initialized (_finished_) before we
            //  can start `thenReturn` or creating another mock
            val view = view
            val inflater = this
            // html-details require this, it is creating views manually
            val context = spy(RuntimeEnvironment.application).apply {
                `when`(getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE)))
                        .thenReturn(inflater)
            }
            `when`(view.context).thenReturn(context)
            `when`(this.inflate(anyInt(), any(ViewGroup::class.java), anyBoolean()))
                    .thenReturn(view)
        }

        val view = markwonSample.createView(
                inflater,
                mock(ViewGroup::class.java))
        markwonSample.onViewCreated(view)
    }

    private val view: View
        get() {
            val view: View = mock(View::class.java)
            view.apply {
                // textView
                val textView = textView
                `when`(findViewById<TextView>(eq(R.id.text_view)))
                        .thenReturn(textView)

                `when`(findViewById<EditText>(eq(R.id.edit_text)))
                        .thenReturn(mock(EditText::class.java))

                // scrollView
                `when`(findViewById<ScrollView>(eq(R.id.scroll_view)))
                        .thenReturn(mock(ScrollView::class.java))

                // recyclerView
                `when`(findViewById<RecyclerView>(eq(R.id.recycler_view)))
                        .thenReturn(mock(RecyclerView::class.java))

                // html-details ViewGroup
                `when`(findViewById<ViewGroup>(R.id.content))
                        .thenReturn(mock(ViewGroup::class.java))

                // special editor views
                arrayOf(
                        R.id.bold,
                        R.id.italic,
                        R.id.strike,
                        R.id.quote,
                        R.id.code)
                        .forEach {
                            val button = mock(Button::class.java).apply {
                                `when`(text).thenReturn("")
                            }
                            `when`(findViewById<Button>(eq(it)))
                                    .thenReturn(button)
                        }
            }
            return view
        }

    private val textView: TextView
        get() {
            val textView: TextView = mock(TextView::class.java, RETURNS_MOCKS)
            textView.apply {
                `when`(text)
                        .thenReturn(SpannableString(""))
                `when`(getTag(eq(R.id.markwon_drawables_scheduler_last_text_hashcode)))
                        .thenReturn(0)
            }
            return textView
        }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        public fun samples(): Collection<Any> {
            return AllSamples::class.java.classLoader!!.getResourceAsStream("samples.json").use { inputStream ->
                SampleUtils
                        .readSamples(inputStream)
                        .map { arrayOf<Any>(it) }
            }
        }
    }
}