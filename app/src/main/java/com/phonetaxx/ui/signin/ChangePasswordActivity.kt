package com.phonetaxx.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.utils.Const
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : BaseActivity(), View.OnClickListener {


    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, ChangePasswordActivity::class.java)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        init()
    }

    private fun init() {
        btnChangePassword.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnChangePassword -> {
                if (isValid()) {
                    updatePassword()
                }

            }
        }
    }

    private fun isValid(): Boolean {

        if (etNewPassword.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_new_password))
            return false
        }
        if (etConfrimPassword.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_confirm_password))
            return false
        }
        if (!etNewPassword.text.toString().equals(etConfrimPassword.text.toString())) {
            showErrorMessage(getString(R.string.err_match_password))
            return false
        }

        return true
    }

    private fun moveToLogin() {
        startActivity(this@ChangePasswordActivity?.let { SignInActivity.getIntent(it) })

    }

    private fun updatePassword() {


        showProgressDialog()

        FireBaseUserHelper.getInstance()
            .updatePassword(intent.getStringExtra(Const.UUID).toString(),
                etConfrimPassword.text.toString(),
                object : FirebaseDatabaseListener<UsersDbModel> {
                    override fun onSuccess(data: UsersDbModel) {
                        hideProgressDialog()
                        showToast(getString(R.string.password_change_message))
                        moveToLogin()

                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        hideProgressDialog()
                    }

                })

    }

}