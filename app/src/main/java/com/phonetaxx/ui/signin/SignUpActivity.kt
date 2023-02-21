package com.phonetaxx.ui.signin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.QuerySnapshot
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.ui.NaicsCodeView
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : BaseActivity(), View.OnClickListener {

    var selectedCountryCode = "+1"

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, SignUpActivity::class.java)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initView()
    }

    private fun initView() {
        setSpannableString()
        btnCreateAccount.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)
        etnSearch.setOnClickListener(this)
        ccp.typeFace = ResourcesCompat.getFont(this, R.font.rubik_regular)
        ccp.setOnCountryChangeListener { selectedCountry ->
            selectedCountryCode = "+" + selectedCountry.phoneCode
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnCreateAccount -> {
                if (isValid()) {
                    checkEmailNumberExist()
                }
            }
            R.id.btnSignIn -> {
                finish()
                startActivity(SignInActivity.getIntent(this))
            }
            R.id.etnSearch-> {
                val intent = Intent(this@SignUpActivity, NaicsCodeView::class.java)
                startActivity(intent)

//                val url = "https://www.census.gov/naics/"
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                startActivity(i)
            }
        }
    }

    private fun isValid(): Boolean {
        if (etFullName.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_full_name))
            return false
        }
        if (etBusinessName.text.isNullOrEmpty()) {
            showErrorMessage("Please enter business name")
            return false
        }
        if (etPhoneNumber.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_enter_phone))
            return false
        }
        if (etEmail.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_enter_email))
            return false
        }
//        if (etEin.text.isNullOrEmpty()) {
//            showErrorMessage("Please enter EIN")
//            return false
//        }
//        if (etNaicsCode.text.isNullOrEmpty()) {
//            showErrorMessage("Please enter NAICS code")
//            return false
//        }
//        if (etLocation.text.isNullOrEmpty()) {
//            showErrorMessage("Please enter location")
//            return false
//        }
//        if (et_SupervisorEmail.text.isNullOrEmpty()) {
//            showErrorMessage("Please enter supervisor email")
//            return false
//        }
        if (etPassword.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_password))
            return false
        }
        if (!cbTermCondition.isChecked) {
            showErrorMessage(getString(R.string.err_term_and_condition))
            return false
        }

        return true
    }

    private fun setSpannableString() {
        val textHeadingSpannable = SpannableString(resources.getString(R.string.term_and_condition))


        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.terms_of_services_url))
                )
                startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setUnderlineText(false);
            }
        }
        textHeadingSpannable.setSpan(clickSpan, 31, 37, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        textHeadingSpannable.setSpan(clickSpan, 38, textHeadingSpannable.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        textHeadingSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)),
            31,
            37,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        textHeadingSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)),
            38,
            textHeadingSpannable.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        tvTermAndCondition.movementMethod = LinkMovementMethod.getInstance()
        tvTermAndCondition.text = textHeadingSpannable

    }


    fun checkEmailNumberExist() {
        showProgressDialog()
        FireBaseUserHelper.getInstance().checkEmailExist(
            etEmail.text.toString().trim()
            , object : FirebaseDatabaseListener<QuerySnapshot> {
                override fun onSuccess(data: QuerySnapshot) {
                    if (data.size() > 0) {
                        hideProgressDialog()
                        showErrorMessage(getString(R.string.email_already_exist))
                    } else {
                        checkPhoneNumberExist()
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)

                }

            })

    }

    fun checkPhoneNumberExist() {

        FireBaseUserHelper.getInstance().checkPhoneNumberExist(etPhoneNumber.text.toString().trim()
            , selectedCountryCode, object : FirebaseDatabaseListener<QuerySnapshot> {
                override fun onSuccess(data: QuerySnapshot) {
                    hideProgressDialog()
                    if (data.size() > 0) {
                        showErrorMessage(getString(R.string.phone_number_is_already_exist))
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
        userData.email = etEmail.text.toString().trim()
        userData.password = etPassword.text.toString().trim()
        userData.fullName = etFullName.text.toString().trim()
        userData.loginType = "Normal"
        userData.callDetection = 1
        userData.phoneNumber = etPhoneNumber.text.toString().trim()
        userData.lastSyncTime = 0
        userData.uuId = ""
        userData.Subscription = "Free"
        userData.businessName = etBusinessName.text.toString().trim()
        userData.eincode = etEin.text.toString().trim()
        userData.naicscode = etNaicsCode.text.toString().trim()
        userData.location = etLocation.text.toString().trim()
        userData.supervisoremail = etLocation.text.toString().trim()

        startActivity(OtpVerificationActivity.getIntent(this, userData))
    }
}
