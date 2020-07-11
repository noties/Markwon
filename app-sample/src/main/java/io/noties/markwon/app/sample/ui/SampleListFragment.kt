package io.noties.markwon.app.sample.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.DiffUtilDataSetChanged
import io.noties.adapt.Item
import io.noties.debug.Debug
import io.noties.markwon.Markwon
import io.noties.markwon.app.App
import io.noties.markwon.app.BuildConfig
import io.noties.markwon.app.R
import io.noties.markwon.app.readme.ReadMeActivity
import io.noties.markwon.app.sample.Sample
import io.noties.markwon.app.sample.SampleManager
import io.noties.markwon.app.sample.SampleSearch
import io.noties.markwon.app.sample.ui.adapt.CheckForUpdateItem
import io.noties.markwon.app.sample.ui.adapt.SampleItem
import io.noties.markwon.app.sample.ui.adapt.VersionItem
import io.noties.markwon.app.utils.Cancellable
import io.noties.markwon.app.utils.UpdateUtils
import io.noties.markwon.app.utils.displayName
import io.noties.markwon.app.utils.hidden
import io.noties.markwon.app.utils.onPreDraw
import io.noties.markwon.app.utils.recyclerView
import io.noties.markwon.app.utils.stackTraceString
import io.noties.markwon.app.utils.tagDisplayName
import io.noties.markwon.app.widget.SearchBar
import io.noties.markwon.movement.MovementMethodPlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import kotlinx.android.parcel.Parcelize

class SampleListFragment : Fragment() {

    private val adapt: Adapt = Adapt.create(DiffUtilDataSetChanged.create())
    private lateinit var markwon: Markwon

    private val type: Type by lazy(LazyThreadSafetyMode.NONE) {
        parseType(arguments!!)
    }

    private var search: String? = null

    // postpone state restoration
    private var pendingRecyclerScrollPosition: RecyclerScrollPosition? = null

    private var cancellable: Cancellable? = null
    private var checkForUpdateCancellable: Cancellable? = null

    private lateinit var progressBar: View

    private val versionItem: VersionItem by lazy(LazyThreadSafetyMode.NONE) {
        VersionItem()
    }

    private val sampleManager: SampleManager
        get() = App.sampleManager

    override fun onAttach(context: Context) {
        super.onAttach(context)

        context.also {
            markwon = markwon(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sample_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar(view)

        val context = requireContext()

        progressBar = view.findViewById(R.id.progress_bar)

        val searchBar: SearchBar = view.findViewById(R.id.search_bar)
        searchBar.onSearchListener = {
            search = it
            fetch()
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapt

//        // additional padding for RecyclerView
        // greatly complicates state restoration (items jump and a lot of times state cannot be
        //  even restored (layout manager scrolls to top item and that's it)
//        searchBar.onPreDraw {
//            recyclerView.setPadding(
//                    recyclerView.paddingLeft,
//                    recyclerView.paddingTop + searchBar.height,
//                    recyclerView.paddingRight,
//                    recyclerView.paddingBottom
//            )
//        }

        val state: State? = arguments?.getParcelable(STATE)
        val initialSearch = arguments?.getString(ARG_SEARCH)

        // clear it anyway
        arguments?.remove(ARG_SEARCH)

        Debug.i(state, initialSearch)

        pendingRecyclerScrollPosition = state?.recyclerScrollPosition

        val search = listOf(state?.search, initialSearch)
                .firstOrNull { it != null }

        if (search != null) {
            searchBar.search(search)
        } else {
            fetch()
        }
    }

    override fun onDestroyView() {

        val state = State(
                search,
                adapt.recyclerView?.scrollPosition
        )
        Debug.i(state)
        arguments?.putParcelable(STATE, state)

        val cancellable = this.cancellable
        if (cancellable != null && !cancellable.isCancelled) {
            cancellable.cancel()
            this.cancellable = null
        }
        super.onDestroyView()
    }

    // not called? yeah, whatever
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        val state = State(
//                search,
//                adapt.recyclerView?.scrollPosition
//        )
//        Debug.i(state)
//        outState.putParcelable(STATE, state)
//    }

    private fun initAppBar(view: View) {
        val appBar = view.findViewById<View>(R.id.app_bar)

        val appBarIcon: ImageView = appBar.findViewById(R.id.app_bar_icon)
        val appBarTitle: TextView = appBar.findViewById(R.id.app_bar_title)
        val appBarIconReadme: ImageView = appBar.findViewById(R.id.app_bar_icon_readme)

        val isInitialScreen = fragmentManager?.backStackEntryCount == 0

        appBarIcon.hidden = isInitialScreen
        appBarIconReadme.hidden = !isInitialScreen

        val type = this.type

        val (text, background) = when (type) {
            is Type.Artifact -> Pair(type.artifact.displayName, R.drawable.bg_artifact)
            is Type.Tag -> Pair(type.tag.tagDisplayName, R.drawable.bg_tag)
            is Type.All -> Pair(resources.getString(R.string.app_name), 0)
        }

        appBarTitle.text = text

        if (background != 0) {
            appBarTitle.setBackgroundResource(background)
        }

        if (isInitialScreen) {
            appBarIconReadme.setOnClickListener {
                context?.let {
                    val intent = ReadMeActivity.makeIntent(it)
                    it.startActivity(intent)
                }
            }
        } else {
            appBarIcon.setImageResource(R.drawable.ic_arrow_back_white_24dp)
            appBarIcon.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun bindSamples(samples: List<Sample>, addVersion: Boolean) {

        val items: List<Item<*>> = samples
                .map {
                    SampleItem(
                            markwon,
                            it,
                            { artifact -> openArtifact(artifact) },
                            { tag -> openTag(tag) },
                            { sample -> openSample(sample) }
                    )
                }
                .let {
                    if (addVersion) {
                        val list: List<Item<*>> = it
                        list.toMutableList().apply {
                            add(0, CheckForUpdateItem(this@SampleListFragment::checkForUpdate))
                            add(0, versionItem)
                        }
                    } else {
                        it
                    }
                }

        adapt.setItems(items)

        val recyclerView = adapt.recyclerView ?: return

        val scrollPosition = pendingRecyclerScrollPosition

        Debug.i(scrollPosition)

        if (scrollPosition != null) {
            pendingRecyclerScrollPosition = null
            recyclerView.onPreDraw {
                (recyclerView.layoutManager as? LinearLayoutManager)
                        ?.scrollToPositionWithOffset(scrollPosition.position, scrollPosition.offset)
            }
        } else {
            recyclerView.onPreDraw {
                recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun checkForUpdate() {
        val current = checkForUpdateCancellable
        if (current != null && !current.isCancelled) {
            return
        }

        progressBar.hidden = false
        checkForUpdateCancellable = UpdateUtils.checkForUpdate { result ->
            progressBar.post {
                processUpdateResult(result)
            }
        }
    }

    private fun processUpdateResult(result: UpdateUtils.Result) {
        val context = context ?: return

        progressBar.hidden = true

        val builder = AlertDialog.Builder(context)

        when (result) {
            is UpdateUtils.Result.UpdateAvailable -> {
                val md = """
                    ## Update available
                    Would you like to download it?
                """.trimIndent()
                builder.setMessage(markwon.toMarkdown(md))
                builder.setNegativeButton(android.R.string.cancel, null)
                builder.setPositiveButton("Download") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                    startActivity(Intent.createChooser(intent, null))
                }
            }

            is UpdateUtils.Result.NoUpdate -> {
                val md = """
                    ## No update
                    You are using latest version (${BuildConfig.GIT_SHA})
                """.trimIndent()
                builder.setMessage(markwon.toMarkdown(md))
                builder.setPositiveButton(android.R.string.ok, null)
            }

            is UpdateUtils.Result.Error -> {
                // trimIndent is confused by tabs in stack trace
                val md = """
## Error
```
${result.throwable.stackTraceString()}
```
"""
                builder.setMessage(markwon.toMarkdown(md))
                builder.setPositiveButton(android.R.string.ok, null)
            }
        }

        builder.show()
    }

    private fun openArtifact(artifact: MarkwonArtifact) {
        Debug.i(artifact)
        openResultFragment(init(artifact))
    }

    private fun openTag(tag: String) {
        Debug.i(tag)
        openResultFragment(init(tag))
    }

    private fun openResultFragment(fragment: SampleListFragment) {
        openFragment(fragment)
    }

    private fun openSample(sample: Sample) {
        openFragment(SampleFragment.init(sample))
    }

    private fun openFragment(fragment: Fragment) {
        fragmentManager!!.beginTransaction()
                .setCustomAnimations(R.anim.screen_in, R.anim.screen_out, R.anim.screen_in_pop, R.anim.screen_out_pop)
                .replace(Window.ID_ANDROID_CONTENT, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    private fun fetch() {

        val sampleSearch: SampleSearch = when (val type = this.type) {
            is Type.Artifact -> SampleSearch.Artifact(search, type.artifact)
            is Type.Tag -> SampleSearch.Tag(search, type.tag)
            else -> SampleSearch.All(search)
        }

        Debug.i(sampleSearch)

        // clear current
        cancellable?.let {
            if (!it.isCancelled) {
                it.cancel()
            }
        }

        cancellable = sampleManager.samples(sampleSearch) {
            val addVersion = sampleSearch is SampleSearch.All && TextUtils.isEmpty(sampleSearch.text)
            bindSamples(it, addVersion)
        }
    }

    companion object {
        private const val ARG_ARTIFACT = "arg.Artifact"
        private const val ARG_TAG = "arg.Tag"
        private const val ARG_SEARCH = "arg.Search"
        private const val STATE = "key.State"

        fun init(): SampleListFragment {
            val fragment = SampleListFragment()
            fragment.arguments = Bundle()
            return fragment
        }

        fun init(artifact: MarkwonArtifact): SampleListFragment {
            val fragment = SampleListFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_ARTIFACT, artifact.name)
            }
            return fragment
        }

        fun init(tag: String): SampleListFragment {
            val fragment = SampleListFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_TAG, tag)
            }
            return fragment
        }

        fun init(search: SampleSearch): SampleListFragment {
            val fragment = SampleListFragment()
            fragment.arguments = Bundle().apply {

                when (search) {
                    is SampleSearch.Artifact -> putString(ARG_ARTIFACT, search.artifact.name)
                    is SampleSearch.Tag -> putString(ARG_TAG, search.tag)
                }

                val query = search.text
                if (query != null) {
                    putString(ARG_SEARCH, query)
                }
            }
            return fragment
        }

        fun markwon(context: Context): Markwon {
            return Markwon.builder(context)
                    .usePlugin(MovementMethodPlugin.none())
                    .build()
        }

        private fun parseType(arguments: Bundle): Type {
            val name = arguments.getString(ARG_ARTIFACT)
            val tag = arguments.getString(ARG_TAG)
            return when {
                name != null -> Type.Artifact(MarkwonArtifact.valueOf(name))
                tag != null -> Type.Tag(tag)
                else -> Type.All
            }
        }
    }

    @Parcelize
    private data class State(
            val search: String?,
            val recyclerScrollPosition: RecyclerScrollPosition?
    ) : Parcelable

    @Parcelize
    private data class RecyclerScrollPosition(
            val position: Int,
            val offset: Int
    ) : Parcelable

    private val RecyclerView.scrollPosition: RecyclerScrollPosition?
        get() {
            val holder = findFirstVisibleViewHolder() ?: return null
            val position = holder.adapterPosition
            val offset = holder.itemView.top
            return RecyclerScrollPosition(position, offset)
        }

    // because findViewHolderForLayoutPosition doesn't work :'(
    private fun RecyclerView.findFirstVisibleViewHolder(): RecyclerView.ViewHolder? {
        if (childCount > 0) {
            val child = getChildAt(0)
            return findContainingViewHolder(child)
        }
        return null
    }

    private sealed class Type {
        class Artifact(val artifact: MarkwonArtifact) : Type()
        class Tag(val tag: String) : Type()
        object All : Type()
    }
}