package com.phonetaxx.ui.navigationdrawer.accountsetting

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.phonetaxx.R
import com.phonetaxx.adapter.FilterableAdapter
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.listener.BaseRecyclerListener
import com.phonetaxx.ui.EditFrequentContact
import com.phonetaxx.ui.navigationdrawer.DeleteFrequent
import com.phonetaxx.utils.Const
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.*

class FrequentNumberAdapter(
    mContext: Context?,
    listener: BaseRecyclerListener<CallCategoryDbModel>,
    deleteFrequent: DeleteFrequent
) :
    FilterableAdapter<CallCategoryDbModel, BaseRecyclerListener<CallCategoryDbModel>>(listener) {
    var context = mContext
    var deleteFrequent: DeleteFrequent? = deleteFrequent
    override fun onBindData(holder: RecyclerView.ViewHolder, item: CallCategoryDbModel?) {
        val viewHolder: ItemViewHolder = holder as ItemViewHolder

        viewHolder.tvName.setText(item?.phoneName)
        viewHolder.tvPhoneNumber.setText(item?.phoneNumber)
        if (item?.callCategory.equals(Const.personal)) {
            viewHolder.tvRelation.setText(context?.getString(R.string.personal))
        } else if (item?.callCategory.equals(Const.business)) {
            viewHolder.tvRelation.setText(context?.getString(R.string.business))
        }

        if (!TextUtils.isEmpty(item!!.phoneImage)) {
            Glide.with(context!!).load(File(item.phoneImage))
                .apply(RequestOptions().placeholder(R.drawable.ic_contact_placeholder))
                .into(holder.ivProfile)
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_contact_placeholder)
        }

        viewHolder.ivDelete.setOnClickListener { deleteFrequent!!.onDeleteContact(item) }

        viewHolder.edit.setOnClickListener{
            viewHolder.itemView.context.startActivity(Intent(viewHolder.itemView.context, EditFrequentContact::class.java)
                .putExtra("uuid", item.uuId)
                .putExtra("call_category", item.callCategory)
                .putExtra("number_type", item.numberType)
                .putExtra("phone_number", item.phoneNumber)
                .putExtra("phone_name", item.phoneName)
                .putExtra("user_uuid", item.userUuid)
                .putExtra("phone_image", item.phoneImage)
                .putExtra("business_name", item.businessName)
                .putExtra("location", item.location)
                .putExtra("naics_code", item.naicsCode)
                .putExtra("malout", item.malout)
            )
        }
    }

    override fun onBindViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.frequent_number_raw_item, parent, false)
        return ItemViewHolder(view)

    }

    override fun compareFieldValue(item: CallCategoryDbModel?, searchItemList: ArrayList<String>): ArrayList<String> {
        return searchItemList
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRelation = itemView.findViewById<AppCompatTextView>(R.id.tvRelation)

        var tvName = itemView.findViewById<AppCompatTextView>(R.id.tvName)
        var tvPhoneNumber = itemView.findViewById<AppCompatTextView>(R.id.tvPhoneNumber)

        var ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)
        var ivProfile = itemView.findViewById<CircleImageView>(R.id.icProfile)

        var edit = itemView.findViewById<TextView>(R.id.ivEdit)

    }
}
