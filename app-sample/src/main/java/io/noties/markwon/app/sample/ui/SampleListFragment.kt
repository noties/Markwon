package io.noties.markwon.app.sample.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
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
import io.noties.debug.Debug
import io.noties.markwon.Markwon
import io.noties.markwon.app.App
import io.noties.markwon.app.R
import io.noties.markwon.app.sample.Sample
import io.noties.markwon.app.sample.SampleManager
import io.noties.markwon.app.sample.SampleSearch
import io.noties.markwon.app.sample.SampleItem
import io.noties.markwon.app.widget.SearchBar
import io.noties.markwon.app.utils.Cancellable
import io.noties.markwon.app.utils.displayName
import io.noties.markwon.app.utils.onPreDraw
import io.noties.markwon.app.utils.recyclerView
import io.noties.markwon.app.utils.tagDisplayName
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

    private val sampleManager: SampleManager
        get() = App.sampleManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        context?.also {
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

        // additional padding for RecyclerView
        searchBar.onPreDraw {
            recyclerView.setPadding(
                    recyclerView.paddingLeft,
                    recyclerView.paddingTop + searchBar.height,
                    recyclerView.paddingRight,
                    recyclerView.paddingBottom
            )
        }

        val state: State? = savedInstanceState?.getParcelable(STATE)
        pendingRecyclerScrollPosition = state?.recyclerScrollPosition
        if (state?.search != null) {
            searchBar.search(state.search)
        } else {
            fetch()
        }
    }

    override fun onDestroyView() {
        val cancellable = this.cancellable
        if (cancellable != null && !cancellable.isCancelled) {
            cancellable.cancel()
            this.cancellable = null
        }
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val state = State(
                search,
                adapt.recyclerView?.scrollPosition
        )
        outState.putParcelable(STATE, state)
    }

    private fun initAppBar(view: View) {
        val appBar = view.findViewById<View>(R.id.app_bar)

        val appBarIcon: ImageView = appBar.findViewById(R.id.app_bar_icon)
        val appBarTitle: TextView = appBar.findViewById(R.id.app_bar_title)

        val type = this.type
        if (type is Type.All) {
            return
        }

        appBarIcon.setImageResource(R.drawable.ic_arrow_back_white_24dp)
        appBarIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val (text, background) = when (type) {
            is Type.Artifact -> Pair(type.artifact.displayName, R.drawable.bg_artifact)
            is Type.Tag -> Pair(type.tag.tagDisplayName, R.drawable.bg_tag)
            else -> error("Unexpected type: $type")
        }

        appBarTitle.text = text
        appBarTitle.setBackgroundResource(background)
    }

    private fun bindSamples(samples: List<Sample>) {
        val items = samples.map {
            SampleItem(
                    markwon,
                    it,
                    { artifact -> openArtifact(artifact) },
                    { tag -> openTag(tag) },
                    { sample -> openSample(sample) }
            )
        }
        adapt.setItems(items)

        val scrollPosition = pendingRecyclerScrollPosition
        if (scrollPosition != null) {
            pendingRecyclerScrollPosition = null
            val recyclerView = adapt.recyclerView ?: return
            recyclerView.onPreDraw {
                (recyclerView.layoutManager as? LinearLayoutManager)
                        ?.scrollToPositionWithOffset(scrollPosition.position, scrollPosition.offset)
            }
        }
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

        // clear current
        cancellable?.let {
            if (!it.isCancelled) {
                it.cancel()
            }
        }

        cancellable = sampleManager.samples(sampleSearch) {
            bindSamples(it)
        }
    }

    companion object {
        private const val ARG_ARTIFACT = "arg.Artifact"
        private const val ARG_TAG = "arg.Tag"
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
            val holder = findViewHolderForLayoutPosition(0) ?: return null
            val position = holder.adapterPosition
            val offset = holder.itemView.top
            return RecyclerScrollPosition(position, offset)
        }

    private sealed class Type {
        class Artifact(val artifact: MarkwonArtifact) : Type()
        class Tag(val tag: String) : Type()
        object All : Type()
    }
}