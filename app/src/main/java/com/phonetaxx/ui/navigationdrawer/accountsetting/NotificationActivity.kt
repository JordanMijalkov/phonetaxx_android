package com.phonetaxx.ui.navigationdrawer.accountsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.utils.PreferenceHelper
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.toolbar_layout.*


class NotificationActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, NotificationActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initView()
    }

    var notification = 0
    private fun initView() {
        cardviewToolbar.setBackgroundResource(R.drawable.toolbar_card_background)
        tvToolbarTitle.setText(getString(R.string.notification))
        ivBackToolbar.setOnClickListener(this);
        tvNotificationSound.setOnClickListener(this);
        notification = PreferenceHelper.getInstance().getProfileData()!!.pushNotification
        if (notification == 1) {
            swPushNotification.isChecked = true
        } else {
            swPushNotification.isChecked = false
        }
        swPushNotification.setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener {
            override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
                var notification = 0
                if (isChecked) {
                    notification = 1
                } else {
                    notification = 0
                }
                if (PreferenceHelper.getInstance().getProfileData()!!.pushNotification != notification) {
                    updatePushNotification(notification)
                }
            }

        })
    }

    private fun updatePushNotification(notification: Int) {

        showProgressDialog()

        FireBaseUserHelper.getInstance().updatePushNotification(notification,
            object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }

            })


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackToolbar -> {
                onBackPressed()
            }
            R.id.tvNotificationSound -> {
                startActivity(NotificationSoundActivity.getIntent(this))
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
