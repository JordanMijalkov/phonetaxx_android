package com.phonetaxx.ui.contactui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonetaxx.R
import com.phonetaxx.contact.ContactHelper
import com.phonetaxx.model.ContactModel
import com.phonetaxx.utils.Const
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ContactActivity : AppCompatActivity(), View.OnClickListener, ContactSeletion {

    var handler = Handler(Looper.getMainLooper())
    var runnable = Runnable {
        try {
            loader.visibility = View.VISIBLE
            var contactList = ContactHelper.getContactListNew(applicationContext)
            contactList.sortBy { it.contactName!! }
            var contactAdapter = ContactAdapter(contactList, this)
            val layoutManager = LinearLayoutManager(applicationContext)
            rvContact.layoutManager = layoutManager
            rvContact.itemAnimator = DefaultItemAnimator()
            rvContact.adapter = contactAdapter
            loader.visibility = View.GONE
        } catch (e: Exception) {

        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        init()
        loadContacts()
    }

    private fun init() {
        cardviewToolbar.setBackgroundResource(R.drawable.toolbar_card_background)
        tvToolbarTitle.setText(getString(R.string.contacts))
        ivBackToolbar.setOnClickListener(this)

    }

    private fun loadContacts() {
        handler.postDelayed(runnable, 500)
//        loader.visibility = View.VISIBLE
//        var contactList = ContactHelper.getContactListNew(applicationContext)
//        contactList.sortBy { it.contactName!! }
//        var contactAdapter = ContactAdapter(contactList, this)
//        val layoutManager = LinearLayoutManager(applicationContext)
//        rvContact.layoutManager = layoutManager
//        rvContact.itemAnimator = DefaultItemAnimator()
//        rvContact.adapter = contactAdapter
//        loader.visibility = View.GONE

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBackToolbar -> {
                onBackPressed()
            }
        }
    }

    override fun onContactSelect(contactModel: ContactModel) {
        var intent = Intent()
        intent.putExtra(Const.NAME, contactModel.contactName)
        intent.putExtra(Const.PHONE, contactModel.contactNumber)
        intent.putExtra(Const.IMAGE, contactModel.contactUri)

        setResult(RESULT_OK, intent)
        finish()

    }
}