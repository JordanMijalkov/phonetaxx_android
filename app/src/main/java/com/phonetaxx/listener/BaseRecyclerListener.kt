package com.phonetaxx.listener

import android.view.View
import androidx.annotation.StringRes

interface BaseRecyclerListener<T> {
    fun showEmptyDataView(@StringRes resId: Int)

    fun onRecyclerItemClick(view: View, position: Int, item: T?)
}
