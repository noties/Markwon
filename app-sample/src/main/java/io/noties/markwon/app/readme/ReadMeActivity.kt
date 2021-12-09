package io.noties.markwon.app.readme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.debug.Debug
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.app.R
import io.noties.markwon.app.utils.ReadMeUtils
import io.noties.markwon.app.utils.hidden
import io.noties.markwon.app.utils.loadReadMe
import io.noties.markwon.app.utils.textOrHide
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.emoji.ext.EmojiPlugin
import io.noties.markwon.emoji.ext.EmojiSpanProvider
import io.noties.markwon.ext.inlinelatex.InLineLatexPlugIn
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.iframe.ext.IFramePlugIn
import io.noties.markwon.image.ImageClickResolver
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.SimpleEntry
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.recycler.table.TableEntryPlugin
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.FencedCodeBlock
import java.io.IOException

@PrismBundle(includeAll = true)
class ReadMeActivity : Activity() {

    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_me)

        progressBar = findViewById(R.id.progress_bar)

        val data = intent.data

        Debug.i(data)

        initAppBar(data)

        initRecyclerView(data)
    }

    private val markwon: Markwon
        get() = Markwon.builder(this)
            .usePlugin(CorePlugin.create().addImageClickResolver(object: ImageClickResolver {
                override fun clickResolve(view: View, link: String) {
                    Log.d("ImageClick", link)
                }
            }))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TableEntryPlugin.create(this))
            .usePlugin(SyntaxHighlightPlugin.create(Prism4j(GrammarLocatorDef()), Prism4jThemeDefault.create(0)))
            .usePlugin(TaskListPlugin.create(this))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(ReadMeImageDestinationPlugin(intent.data))
            .usePlugin(IFramePlugIn.create())
            .usePlugin(GlideImagesPlugin.create(this))
            .usePlugin(EmojiPlugin.create(EmojiSpanProvider.create(this, 36.0f)))
            .usePlugin(InLineLatexPlugIn.create(46.0f, 1080))
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(46.0f))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    builder.on(FencedCodeBlock::class.java) { visitor, block ->
                        // we actually won't be applying code spans here, as our custom view will
                        // draw background and apply mono typeface
                        //
                        // NB the `trim` operation on literal (as code will have a new line at the end)
                        val code = visitor.configuration()
                                .syntaxHighlight()
                                .highlight(block.info, block.literal.trim())
                        visitor.builder().append(code)
                    }
                }

                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver(ReadMeLinkResolver())
                }
            })
            .build()

    private fun initAppBar(data: Uri?) {
        val appBar = findViewById<View>(R.id.app_bar)
        appBar.findViewById<View>(R.id.app_bar_icon).setOnClickListener { onBackPressed() }

        val (title: String, subtitle: String?) = if (data == null) {
            Pair("README.md", null)
        } else {
            Pair(data.lastPathSegment ?: "", data.toString())
        }

        appBar.findViewById<TextView>(R.id.title).text = title
        appBar.findViewById<TextView>(R.id.subtitle).textOrHide(subtitle)
    }

    private fun initRecyclerView(data: Uri?) {

        val adapter = MarkwonAdapter.builder(R.layout.adapter_node, R.id.text_view, "TEXT", Color.BLACK, "light")
                .include(FencedCodeBlock::class.java, SimpleEntry.create(R.layout.adapter_node_code_block, R.id.text_view, Color.BLACK, "light"))
                .include(TableBlock::class.java, TableEntry.create {
                    it
                            .tableLayout(R.layout.adapter_node_table_block, R.id.table_layout)
                            .textLayoutIsRoot(R.layout.view_table_entry_cell)
                })
                .build()

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        load(applicationContext, data) { result ->

            when (result) {
                is Result.Failure -> Debug.e(result.throwable)
                is Result.Success -> {
                    val markwon = markwon
                    val node = markwon.parse(result.markdown)
                    if (window != null) {
                        recyclerView.post {
                            adapter.setParsedMarkdown(markwon, node)
                            adapter.notifyDataSetChanged()
                            progressBar.hidden = true
                        }
                    }
                }
            }
        }
    }

    private sealed class Result {
        data class Success(val markdown: String) : Result()
        data class Failure(val throwable: Throwable) : Result()
    }

    companion object {
        fun makeIntent(context: Context): Intent {
            return Intent(context, ReadMeActivity::class.java)
        }

        private fun load(context: Context, data: Uri?, callback: (Result) -> Unit) = try {

            if (data == null) {
                callback.invoke(Result.Success(loadReadMe(context)))
            } else {
                val request = Request.Builder()
                        .get()
                        .url(ReadMeUtils.buildRawGithubUrl(data))
                        .build()
                OkHttpClient().newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.invoke(Result.Failure(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val md = response.body()?.string() ?: ""
                        callback.invoke(Result.Success(md))
                    }
                })
            }

        } catch (t: Throwable) {
            callback.invoke(Result.Failure(t))
        }
    }
}