package com.phonetaxx.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phonetaxx.R
import com.phonetaxx.firebase.model.card.Card

class MyRecyclerViewAdapter internal constructor(
    context: Context?,
    data: List<Card>,
    isSubScriptionAvailable: Boolean
) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    private val mData: List<Card>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null
    private var isSubScriptionAvailable = isSubScriptionAvailable
    var context: Context? = null

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view: View = mInflater.inflate(R.layout.card_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.tvBrandType.text = data.card.brand
        holder.tvlastDigit.text = "**** **** **** " + data.card.last4

        if (isSubScriptionAvailable) {
            holder.tvSubOnOff.text = context!!.getString(R.string.subscription_on)
            holder.tvSubOnOff.setTextColor(ContextCompat.getColor(context!!, R.color.green))

        } else {
            holder.tvSubOnOff.text = context!!.getString(R.string.subscription_off)
            holder.tvSubOnOff.setTextColor(ContextCompat.getColor(context!!, R.color.red))

        }

        if (data.default) {
            holder.tvDefault.visibility = View.VISIBLE
            holder.tvSubOnOff.visibility = View.VISIBLE
        } else {
            holder.tvDefault.visibility = View.GONE
            holder.tvSubOnOff.visibility = View.GONE


        }

        if (data.card.brand.contains("Visa")) {
            Glide.with(context!!)
                .load(R.drawable.ic_visa)
                .placeholder(R.drawable.ic_card_placeholder)
                .into(holder.ivcardImage)
        } else if (data.card.brand.contains("MasterCard")) {
            Glide.with(context!!)
                .load(R.drawable.ic_mastercard)
                .placeholder(R.drawable.ic_card_placeholder)
                .into(holder.ivcardImage)
        } else if (data.card.brand.contains("Discover")) {
            Glide.with(context!!)
                .load(R.drawable.ic_discover)
                .placeholder(R.drawable.ic_card_placeholder)
                .into(holder.ivcardImage)
        } else if (data.card.brand.contains("American Express")) {
            Glide.with(context!!)
                .load(R.drawable.ic_americanexpress)
                .placeholder(R.drawable.ic_card_placeholder)
                .into(holder.ivcardImage)
        } else {
            Glide.with(context!!)
                .load(R.drawable.ic_card_placeholder)
                .into(holder.ivcardImage)
        }

    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvBrandType: TextView
        var tvlastDigit: TextView
        var tvDefault: TextView
        var ivcardImage: AppCompatImageView
        var tvSubOnOff: TextView

        override fun onClick(view: View) {

            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            tvBrandType = itemView.findViewById(R.id.tvBrandType)
            tvlastDigit = itemView.findViewById(R.id.tvlastDigit)
            tvDefault = itemView.findViewById(R.id.tvDefault)
            ivcardImage = itemView.findViewById(R.id.ivcardImage)
            tvSubOnOff = itemView.findViewById(R.id.tvSubOnOff)
            itemView.setOnClickListener(this)
        }
    }


    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
    }
}