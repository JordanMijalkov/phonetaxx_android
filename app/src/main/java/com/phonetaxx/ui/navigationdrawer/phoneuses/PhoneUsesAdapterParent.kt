package com.phonetaxx.ui.navigationdrawer.phoneuses

import android.content.Context
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
import com.phonetaxx.viewholder.ProgressViewHolder
import java.util.*

class PhoneUsesAdapterParent(mContext: Context?, listener: BaseRecyclerListener<CallLogsDbModel>) :
    FilterableAdapter<CallLogsDbModel, BaseRecyclerListener<CallLogsDbModel>>(listener) {
    var context = mContext
    var SELECTED_TAB_TYPE = 0
    var filterType = Const.TODAY
    override fun onBindData(holder: RecyclerView.ViewHolder, item: CallLogsDbModel?) {
        if (holder is ItemViewHolder) {
            val viewHolder: ItemViewHolder = holder

            viewHolder.tvAssign.visibility = View.GONE
            viewHolder.ivBusiness.visibility = View.GONE
            viewHolder.ivPersonal.visibility = View.GONE

            if (SELECTED_TAB_TYPE == 0) {
                viewHolder.ivBusiness.visibility = View.VISIBLE
            } else if (SELECTED_TAB_TYPE == 1) {
                viewHolder.ivPersonal.visibility = View.VISIBLE
            } else if (SELECTED_TAB_TYPE == 2) {
                viewHolder.tvAssign.visibility = View.VISIBLE
            }


            viewHolder.tvName.setText(item?.name)
            viewHolder.tvCallDuration.setText(DateTimeHelper.getInstance().getTimeFormateFromSeconds(item?.callDurationInSecond))
            viewHolder.tvPhoneNumber.setText(item?.phoneNumber)

            if (viewHolder.adapterPosition > 0) {

                if (filterType == Const.TODAY) {
                    if (item?.callDate.equals(allItems.get(viewHolder.adapterPosition - 1)?.callDate)) {
                        viewHolder.tvDateAndTime.visibility = View.GONE
                    } else {
                        viewHolder.tvDateAndTime.visibility = View.VISIBLE
                        viewHolder.tvDateAndTime.setText(item?.callDate)
                    }
                } else if (filterType == Const.WEEKLY) {
                    if (item?.callWeek.equals(allItems.get(viewHolder.adapterPosition - 1)?.callWeek)) {
                        viewHolder.tvDateAndTime.visibility = View.GONE
                    } else {
                        viewHolder.tvDateAndTime.visibility = View.VISIBLE
                        viewHolder.tvDateAndTime.setText(item?.callWeek)
                    }
                } else if (filterType == Const.MONTHLY) {
                    if (item?.callMonth.equals(allItems.get(viewHolder.adapterPosition - 1)?.callMonth)) {
                        viewHolder.tvDateAndTime.visibility = View.GONE
                    } else {
                        viewHolder.tvDateAndTime.visibility = View.VISIBLE
                        viewHolder.tvDateAndTime.setText(item?.callMonth)
                    }
                }
            } else {
                viewHolder.tvDateAndTime.visibility = View.VISIBLE
                if (filterType == Const.TODAY) {
                    viewHolder.tvDateAndTime.setText(item?.callDate)
                } else if (filterType == Const.WEEKLY) {
                    viewHolder.tvDateAndTime.setText(item?.callWeek)
                } else if (filterType == Const.MONTHLY) {
                    viewHolder.tvDateAndTime.setText(item?.callMonth)
                }
            }

            viewHolder.tvAssign.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    listener.onRecyclerItemClick(p0!!, viewHolder.adapterPosition, item)
                }
            })

        }
    }

    override fun onBindViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == Const.ITEM_TYPE) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.phone_uses_raw_item_parent, parent, false)
            return ItemViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_view, parent, false)
            return ProgressViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (allItems.get(position) == null) {
            return Const.LOADING_TYPE
        } else {
            return Const.ITEM_TYPE
        }
    }

    public fun setSelectedTabPosition(selectedTabPosition: Int) {
        SELECTED_TAB_TYPE = selectedTabPosition
    }


    fun setFilterDataType(data: String) {
        filterType = data
        notifyDataSetChanged()
    }

    override fun compareFieldValue(item: CallLogsDbModel?, searchItemList: ArrayList<String>): ArrayList<String> {
        return searchItemList
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val recyclerView = itemView.findViewById<FilterRecyclerView>(R.id.recyclerView)
        val tvDateAndTime = itemView.findViewById<AppCompatTextView>(R.id.tvDateAndTime)
        var tvName = itemView.findViewById<AppCompatTextView>(R.id.tvName)
        var tvCallDuration = itemView.findViewById<AppCompatTextView>(R.id.tvCallDuration)
        var tvPhoneNumber = itemView.findViewById<AppCompatTextView>(R.id.tvPhoneNumber)
        var tvAmount = itemView.findViewById<AppCompatTextView>(R.id.tvAmount)
        var tvAssign = itemView.findViewById<AppCompatTextView>(R.id.tvAssign)
        var ivBusiness = itemView.findViewById<AppCompatImageView>(R.id.ivBusiness)
        var ivPersonal = itemView.findViewById<AppCompatImageView>(R.id.ivPersonal)

    }
}
