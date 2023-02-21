package com.phonetaxx.ui.navigationdrawer.accountsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.phonetaxx.R
import com.phonetaxx.ui.BaseActivity


class PrivacyPolicyActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, PrivacyPolicyActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        initView()
    }

    private fun initView() {}

}
