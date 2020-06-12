package io.noties.markwon.app

import android.app.Activity
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.debug.Debug
import io.noties.markwon.app.adapt.SampleItem
import io.noties.markwon.app.base.SearchBar
import io.noties.markwon.sample.annotations.MarkwonArtifact

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar: SearchBar = findViewById(R.id.search_bar)
        searchBar.onSearchListener = {
            Debug.i("search: '$it'")
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.clipToPadding = false

        searchBar.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                searchBar.viewTreeObserver.removeOnPreDrawListener(this)
                recyclerView.setPadding(
                        recyclerView.paddingLeft,
                        recyclerView.paddingTop + searchBar.height,
                        recyclerView.paddingRight,
                        recyclerView.paddingBottom
                )
                return true
            }
        })

        val adapt = Adapt.create()
        recyclerView.adapter = adapt

        val list = listOf(
                MarkwonSampleItem(
                        "first",
                        "1",
                        "Title first",
                        "Description her egoes and goes ang goes, so will it ever stop?",
                        listOf(MarkwonArtifact.CORE, MarkwonArtifact.EDITOR),
                        listOf("first", "second")
                )
        )

        adapt.setItems(list.map { SampleItem(it) })
    }
}