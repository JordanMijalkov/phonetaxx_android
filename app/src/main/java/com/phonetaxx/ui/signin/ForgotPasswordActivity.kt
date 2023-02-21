package com.phonetaxx.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.QuerySnapshot
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.utils.Const
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {

    var selectedCountryCode = "+1"

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        initView()
    }

    private fun initView() {
        btnForgot.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)
        ccp.typeFace = ResourcesCompat.getFont(this, R.font.rubik_regular)
        ccp.setOnCountryChangeListener { selectedCountry ->
            selectedCountryCode = "+" + selectedCountry.phoneCode
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnForgot -> {
                if (isValid()) {
                    checkPhoneNumberExist()
                }
            }
            R.id.btnSignIn -> {
                finish()
                startActivity(SignInActivity.getIntent(this))
            }
        }
    }

    private fun isValid(): Boolean {

        if (etPhoneNumber.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_enter_phone))
            return false
        }

        return true
    }

    fun checkPhoneNumberExist() {

        FireBaseUserHelper.getInstance().checkPhoneNumberExist(etPhoneNumber.text.toString().trim(),
            selectedCountryCode,
            object : FirebaseDatabaseListener<QuerySnapshot> {
                override fun onSuccess(data: QuerySnapshot) {
                    hideProgressDialog()
                    if (data.size() <= 0) {
                        showErrorMessage(getString(R.string.phone_number_does_not_exist))
                    } else {
                        redirectToOtpScreen()
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)

                }
            })
    }

    private fun redirectToOtpScreen() {

        var userData = UsersDbModel()
        userData.countryCode = selectedCountryCode
        userData.loginType = "Normal"
        userData.phoneNumber = etPhoneNumber.text.toString().trim()

        startActivity(
            OtpVerificationActivity.getIntent(this, userData).putExtra(Const.FROM_FORGOT, true)
        )
    }
}