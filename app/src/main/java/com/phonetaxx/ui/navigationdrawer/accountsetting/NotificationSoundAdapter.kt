package com.phonetaxx.ui.navigationdrawer.accountsetting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.phonetaxx.R
import com.phonetaxx.adapter.FilterableAdapter
import com.phonetaxx.listener.BaseRecyclerListener
import com.phonetaxx.model.NotificationSoundModel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class NotificationSoundAdapter(mContext: Context?, listener: BaseRecyclerListener<NotificationSoundModel>) :
    FilterableAdapter<NotificationSoundModel, BaseRecyclerListener<NotificationSoundModel>>(listener) {
    var context = mContext
    override fun onBindData(holder: RecyclerView.ViewHolder, item: NotificationSoundModel?) {
        val viewHolder: ItemViewHolder = holder as ItemViewHolder

        viewHolder.tvName.setText(item?.name)
        if (item?.isSelected!!) {
            viewHolder.ivTick.visibility = View.VISIBLE
        } else {
            viewHolder.ivTick.visibility = View.INVISIBLE
        }

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                for (i in 0..allItems.size - 1) {
                    if (i == viewHolder.adapterPosition) {
                        allItems.get(i)!!.isSelected = true
                    } else {
                        allItems.get(i)!!.isSelected = false
                    }
                }
                notifyDataSetChanged()
                listener.onRecyclerItemClick(p0!!, viewHolder.adapterPosition, item)
            }

        })
    }

    override fun onBindViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_sound_raw_item, parent, false)
        return ItemViewHolder(view)

    }

    override fun compareFieldValue(
        item: NotificationSoundModel?,
        searchItemList: ArrayList<String>
    ): ArrayList<String> {
        return searchItemList
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvName = itemView.findViewById<AppCompatTextView>(R.id.tvName)
        var ivTick = itemView.findViewById<CircleImageView>(R.id.icProfile)
    }
}
