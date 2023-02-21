package com.phonetaxx.ui.contactui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.l4digital.fastscroll.FastScroller
import com.phonetaxx.R
import com.phonetaxx.model.ContactModel
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


internal class ContactAdapter(
    private var contactList: List<ContactModel>,
    contactSeletion: ContactSeletion
) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>(), FastScroller.SectionIndexer {

    var context: Context? = null
    var contactSeletion = contactSeletion

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvPhoneNumber: TextView = view.findViewById(R.id.tvPhoneNumber)
        var ivPhoto: CircleImageView = view.findViewById(R.id.ivPhoto)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contactList[position]
        holder.tvName.text = contact.contactName
        holder.tvPhoneNumber.text = contact.contactNumber
        if (contact.contactUri != null) {
            Glide.with(context!!).load(File(contact.contactUri))
                .apply(RequestOptions().placeholder(R.drawable.ic_contact_placeholder))
                .into(holder.ivPhoto)
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_contact_placeholder)
        }

        holder.itemView.setOnClickListener { contactSeletion.onContactSelect(contact) }

    }

    fun getItem(position: Int): ContactModel {
        return contactList.get(position)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun getSectionText(position: Int): CharSequence? =
        contactList[position].contactName!!.first().toString()

}