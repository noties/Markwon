package io.noties.markwon.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class MarkwonSample {

    fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(layoutResId, container, false)
    }

    abstract fun onViewCreated(view: View)

    protected abstract val layoutResId: Int
}