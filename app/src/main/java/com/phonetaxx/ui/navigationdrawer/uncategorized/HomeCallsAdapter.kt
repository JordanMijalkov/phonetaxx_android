package com.phonetaxx.ui.navigationdrawer.uncategorized

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.phonetaxx.R
import com.phonetaxx.adapter.FilterableAdapter
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.listener.BaseRecyclerListener
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.DateTimeHelper
import java.util.*

class HomeCallsAdapter(listener: BaseRecyclerListener<CallLogsDbModel>) :
    FilterableAdapter<CallLogsDbModel, BaseRecyclerListener<CallLogsDbModel>>(listener) {

    var filterType = Const.TODAY
    var selectedTabPosition = 0

    override fun onBindData(holder: RecyclerView.ViewHolder, item: CallLogsDbModel?) {
        val viewHolder: ItemViewHolder = holder as ItemViewHolder

        viewHolder.tvName.setText(item?.name)
        Log.v("viewHolder", ": " + item?.callDurationInSecond);
        viewHolder.tvCallDuration.setText(DateTimeHelper.getInstance().getTimeFormateFromSeconds(item?.callDurationInSecond))
        viewHolder.tvDateTime.setText(DateTimeHelper.getInstance().getWeekDayAndTimeFromTimeStamp(item?.callDateTimeUTC!!))
        viewHolder.tvPhoneNumber.setText(item.phoneNumber)

        viewHolder.tvUncategorized.visibility = View.GONE
        viewHolder.ivPersonalCategory.visibility = View.GONE
        viewHolder.ivBusinessCategory.visibility = View.GONE
        if (selectedTabPosition == 0) {
            if (item.callCategory.equals(Const.uncategorized)) {
                viewHolder.tvUncategorized.visibility = View.VISIBLE
            } else if (item.callCategory.equals(Const.personal)) {
                viewHolder.ivPersonalCategory.visibility = View.VISIBLE
            } else if (item.callCategory.equals(Const.business)) {
                viewHolder.ivBusinessCategory.visibility = View.VISIBLE
            }
        }

    }

    override fun onBindViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_call_adapter_raw_item, parent, false)
        return ItemViewHolder(view)

    }

    override fun compareFieldValue(item: CallLogsDbModel?, searchItemList: ArrayList<String>): ArrayList<String> {
        return searchItemList;
    }

    fun setFilterDataType(data: String) {
        filterType = data
        notifyDataSetChanged()
    }

    fun setTabPosition(position: Int) {
        selectedTabPosition = position
        notifyDataSetChanged()
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.findViewById<AppCompatTextView>(R.id.tvName)
        var tvDateTime = itemView.findViewById<AppCompatTextView>(R.id.tvDateTime)
        var tvCallDuration = itemView.findViewById<AppCompatTextView>(R.id.tvCallDuration)
        var tvPhoneNumber = itemView.findViewById<AppCompatTextView>(R.id.tvPhoneNumber)
        var tvUncategorized = itemView.findViewById<AppCompatTextView>(R.id.tvUncategorized)
        var ivBusinessCategory = itemView.findViewById<AppCompatImageView>(R.id.ivBusinessCategory)
        var ivPersonalCategory = itemView.findViewById<AppCompatImageView>(R.id.ivPersonalCategory)

    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
}
