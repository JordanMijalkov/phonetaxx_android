package com.phonetaxx.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.jamangle.webapi.RemoteCallback
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.model.RegisterRequest
import com.phonetaxx.model.RegisterResponse
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.ui.navigationdrawer.NavigationDrawerActivity
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.PreferenceHelper
import com.sixmmedicine.webapi.WebAPIManager
import kotlinx.android.synthetic.main.activity_otp_verification.*
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : BaseActivity(), View.OnKeyListener, View.OnClickListener {


    companion object {
        fun getIntent(context: Context, user: UsersDbModel): Intent {
            val intent = Intent(context, OtpVerificationActivity::class.java)
            var bundle = Bundle()
            bundle.putParcelable(Const.USER_MODEL, user)
            intent.putExtras(bundle)
            return intent
        }
    }

    private var fcmToken: String? = null
    private var deviceId: String? = null



    var userData: UsersDbModel? = null

    var from_forgot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        initView()
        generateToken()
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        mAuth = FirebaseAuth.getInstance()
        if (intent != null && intent.extras != null) {
            from_forgot = intent.getBooleanExtra(Const.FROM_FORGOT, false)

            userData = intent.extras!!.getParcelable(Const.USER_MODEL)
            if (userData != null) {
                tvVerificatioNumberMessage.setText(getString(R.string.verify_your_account_by_entering_the_4_digits_code_we_sent_to) + "\n" + userData!!.countryCode + " " + userData!!.phoneNumber)

                sendOtpFromFirebase()
            }
        }
    }

    private fun sendOtpFromFirebase() {
        showProgressDialog()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            userData!!.countryCode + userData!!.phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        )
    }

    var storedVerificationId = ""
    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            hideProgressDialog()
            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e: FirebaseException) {
            hideProgressDialog()
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            showErrorMessage(e.message!!)
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            hideProgressDialog()
            showErrorMessage(getString(R.string.code_send_success_message))
            storedVerificationId = verificationId
            startTimer()

        }
    }
    var mAuth: FirebaseAuth? = null
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {


        showProgressDialog()
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = task.result?.user
                    userData?.uuId = user!!.uid
                    if (from_forgot) {
                        redirectToChangePassword()
                    } else {
                        createUserAndRedirect()
                    }

                    // ...
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showErrorMessage(getString(R.string.invalid_varification_code))
                    }
                }
            }
    }

    private fun startTimer() {
        tvResendOtp.isEnabled = false
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var second = millisUntilFinished / 1000;
                var strSecond = "00:0";
                if (second < 10) {
                    strSecond = "00:0" + second
                } else {
                    strSecond = "00:" + second;
                }
                tvResendOtp.setText(getString(R.string.resend_code_in) + " " + strSecond)
            }

            override fun onFinish() {
                tvResendOtp.setText(R.string.resend_code)
                tvResendOtp.isEnabled = true
            }
        }
        timer.start()
    }

    private fun initView() {

        etOtp1.setOnKeyListener(this)
        etOtp2.setOnKeyListener(this)
        etOtp3.setOnKeyListener(this)
        etOtp4.setOnKeyListener(this)
        etOtp5.setOnKeyListener(this)
        etOtp6.setOnKeyListener(this)
        tvResendOtp.setOnClickListener(this)
        btnVerifyOtp.setOnClickListener(this)
        etOtp1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 0) {
                    etOtp2.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        etOtp2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 0) {
                    etOtp3.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        etOtp3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 0) {
                    etOtp4.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        etOtp4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 0) {
                    etOtp5.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        etOtp5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 0) {
                    etOtp6.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        etOtp6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == KeyEvent.KEYCODE_DEL) {
            etOtp1.setText("")
            etOtp2.setText("")
            etOtp3.setText("")
            etOtp4.setText("")
            etOtp5.setText("")
            etOtp6.setText("")
            etOtp1.requestFocus()
            //this is for backspace
        }
        return false;
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnVerifyOtp -> {
                if (isValid()) {
                    verifyCode()

                }
            }
            R.id.tvResendOtp -> {
                sendOtpFromFirebase()
            }
        }
    }

    private fun verifyCode() {

        var code = etOtp1.text.toString().trim() +
                etOtp2.text.toString().trim() +
                etOtp3.text.toString().trim() +
                etOtp4.text.toString().trim() +
                etOtp5.text.toString().trim() +
                etOtp6.text.toString().trim()

        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)

        signInWithPhoneAuthCredential(credential)
    }

    private fun createUserAndRedirect() {

        FireBaseUserHelper.getInstance()
            .createUser(this.userData!!, object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()
                    registerApi()
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)
                }

            })
    }

    private fun changePasswordofUser() {

        FireBaseUserHelper.getInstance()
            .getUserDataFromUuid(
                this.userData!!.uuId,
                object : FirebaseDatabaseListener<UsersDbModel> {
                    override fun onSuccess(data: UsersDbModel) {
                        hideProgressDialog()
//                    redirectToHomeScreen()
                        redirectToChangePassword()
                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        hideProgressDialog()
                        showErrorMessage(errorMessage)
                    }

                })
    }

    private fun redirectToHomeScreen() {
        PreferenceHelper.getInstance().setProfileData(this.userData!!)
        PreferenceHelper.getInstance().lastSyncCallTimeStamp = userData?.lastSyncTime
        PreferenceHelper.getInstance().isLogin = true

        startActivity(NavigationDrawerActivity.getIntent(this))
    }

    private fun redirectToChangePassword() {

        startActivity(
            ChangePasswordActivity.getIntent(this).putExtra(Const.UUID, userData!!.uuId)
        )
    }

    private fun isValid(): Boolean {
        if (etOtp1.text.isNullOrEmpty()
            || etOtp2.text.isNullOrEmpty()
            || etOtp3.text.isNullOrEmpty()
            || etOtp4.text.isNullOrEmpty()
            || etOtp5.text.isNullOrEmpty()
            || etOtp6.text.isNullOrEmpty()
        ) {
            showErrorMessage(getString(R.string.please_enter_otp))
            return false;
        }

        return true
    }

    private fun generateToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
              //  fcmToken = it.result.toString()
            }
        }
    }


    private fun registerApi(){

        showProgressDialog()
        val request = RegisterRequest(
            userData!!.uuId,
            userData!!.fullName,
            userData!!.countryCode,
            userData!!.phoneNumber,
            userData!!.email,
            userData!!.password,
            "A",
            fcmToken,
            deviceId)

        WebAPIManager.instance.register(request)
            .enqueue(object: RemoteCallback<RegisterResponse>(){
                override fun onSuccess(response: RegisterResponse?) {
                    if(response?.getStatus()!!) {
                        redirectToHomeScreen()
                    }

                        hideProgressDialog()
                }

                override fun onUnauthorized(throwable: Throwable) {
                    hideProgressDialog()
                }

                override fun onFailed(throwable: Throwable) {
                    hideProgressDialog()
                }

                override fun onInternetFailed() {
                    hideProgressDialog()
                }

                override fun onEmptyResponse(message: String) {
                    hideProgressDialog()
                }

            })
    }




}
