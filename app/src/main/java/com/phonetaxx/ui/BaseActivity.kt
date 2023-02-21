package com.phonetaxx.ui

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.PurchasesUpdatedListener
import com.phonetaxx.extension.showToast

public abstract class BaseActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showErrorMessage(message: String) {
        showToast(message)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}