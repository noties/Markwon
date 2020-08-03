package io.noties.markwon.app.sample.ui

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.app.R

abstract class MarkwonRecyclerViewSample : MarkwonSample() {

    protected lateinit var context: Context
    protected lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View) {
        context = view.context
        recyclerView = view.findViewById(R.id.recycler_view)
        render()
    }

    override val layoutResId: Int
        get() = R.layout.sample_recycler_view

    abstract fun render()
}