package com.phonetaxx.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterRecyclerView : RecyclerView {

    private var tvEmptyMsgHolder: TextView? = null
    val emptyMsgHolder: View?
        get() = tvEmptyMsgHolder

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun setEmptyMsgHolder(tvEmptyMsgHolder: TextView) {
        this.tvEmptyMsgHolder = tvEmptyMsgHolder
    }

    fun showEmptyDataView(errorMessage: String) {

        if (tvEmptyMsgHolder != null) {
            if (adapter != null && adapter!!.itemCount == 0) {

                tvEmptyMsgHolder!!.visibility = View.VISIBLE
                tvEmptyMsgHolder!!.text = errorMessage

            } else {
                tvEmptyMsgHolder!!.visibility = View.GONE
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter = null
    }

}
