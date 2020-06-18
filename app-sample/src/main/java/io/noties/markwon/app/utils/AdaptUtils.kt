package io.noties.markwon.app.utils

import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.debug.Debug

val Adapt.recyclerView: RecyclerView?
    get() {
        // internally throws if recycler is not present (detached from recyclerView)
        return try {
            recyclerView()
        } catch (t: Throwable) {
            Debug.e(t)
            null
        }
    }