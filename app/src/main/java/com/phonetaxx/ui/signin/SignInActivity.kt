package com.phonetaxx.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.jamangle.webapi.RemoteCallback
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.FirebaseDatabasePaginationListener
import com.phonetaxx.firebase.helper.FireBaseCallCategoryHelper
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.model.RegisterRequest
import com.phonetaxx.model.RegisterResponse
import com.phonetaxx.roomdb.DatabaseHelper
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.ui.navigationdrawer.NavigationDrawerActivity
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.PreferenceHelper
import com.sixmmedicine.webapi.WebAPIManager
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.*


class SignInActivity : BaseActivity(), View.OnClickListener {

    private var RC_SIGN_IN = 234
    private var fcmToken: String? = ""
    private var deviceId: String? = null

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        FacebookSdk.sdkInitialize(applicationContext)

        initView()
        generateToken()
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        mAuth = FirebaseAuth.getInstance()
        setGoogleSignIn()
        setFacebookSignIn()
        if (PreferenceHelper.getInstance().isRememberMe!!) {
            swRememberMe.isChecked = true
            etEmailPhoneNumber.setText(PreferenceHelper.getInstance().getProfileData()?.email)
            etPassword.setText(PreferenceHelper.getInstance().getProfileData()?.password)
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = mAuth!!.currentUser
//        updateUI(currentUser)
    }

    private fun setFacebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create()
        fb_login_button.setReadPermissions(listOf("email"))
        fb_login_button.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })
    }

    private fun generateToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
               // fcmToken = it.result.toString()
            }
        }
    }


    private fun registerApi(userData: UsersDbModel) {

        showProgressDialog()
        val request = RegisterRequest(
            userData.uuId,
            userData.fullName,
            userData.countryCode,
            userData.phoneNumber,
            userData.email,
            userData.password,
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

    private fun handleFacebookAccessToken(token: AccessToken?) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token!!.token)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth!!.currentUser

                    var userData = UsersDbModel()
                    userData.countryCode = ""
                    userData.email = user!!.email!!
                    userData.profileUrl = user.photoUrl.toString()
                    userData.socialId = user.uid.toString()
                    userData.password = ""
                    userData.fullName = user.displayName!!
                    userData.loginType = Const.USER_TYPE_FACEBOOK
                    userData.phoneNumber = ""
                    userData.lastSyncTime = 0
                    userData.callDetection = 1
                    userData.uuId = user.uid
                    userData.Subscription = "Free"

                    Toast.makeText(
                        baseContext, "Authentication Success. " + user.displayName,
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(user, credential)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null, credential)
                }

                // ...
            }
    }

    private fun updateUI(
        user: FirebaseUser?,
        credential: AuthCredential
    ) {

        checkEmailAddressAccountFacebook(user, credential)
    }

    var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var mCallbackManager: CallbackManager

    private fun setGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient?.getSignInIntent()

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data != null) {
                if (mCallbackManager != null) {
                    mCallbackManager.onActivityResult(requestCode, resultCode, data)
                } else {
                    Log.e("mCallbackManager", "mCallbackManager null")
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        if (requestCode === RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                Log.v(TAG, "onActivityResult :photoUrl -   " + account?.photoUrl)
                Log.v(TAG, "onActivityResult :displayName -  " + account?.displayName)
                Log.v(TAG, "onActivityResult :email -  " + account?.email)
                Log.v(TAG, "onActivityResult :id -  " + account?.id)

                checkEmailAddressAccount(account)

            } catch (e: ApiException) {
                Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun checkEmailAddressAccount(account: GoogleSignInAccount?) {

        showProgressDialog()
        FireBaseUserHelper.getInstance().checkEmailExist(
            account!!.email!!, object : FirebaseDatabaseListener<QuerySnapshot> {
                override fun onSuccess(data: QuerySnapshot) {
                    if (data.size() > 0) {
                        for (document in data) {
                            var usersDbModel = document.toObject(UsersDbModel::class.java)
                            if (usersDbModel.loginType.equals(Const.USER_TYPE_GOOGLE) && usersDbModel.socialId.equals(
                                    account.id
                                )
                            ) {
                                setPreferenceAndMoveToNext(usersDbModel)
                            } else {
                                hideProgressDialog()
                                showErrorMessage(getString(R.string.email_already_exist))
                            }
                            break
                        }
                    } else {
                        //authenticating with firebase
                        firebaseAuthWithGoogle(account)
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)

                }

            })

    }

    private fun checkEmailAddressAccountFacebook(
        account: FirebaseUser?,
        credential: AuthCredential
    ) {

        showProgressDialog()
        FireBaseUserHelper.getInstance().checkEmailExist(
            account!!.email!!, object : FirebaseDatabaseListener<QuerySnapshot> {
                override fun onSuccess(data: QuerySnapshot) {
                    if (data.size() > 0) {
                        for (document in data) {
                            val usersDbModel = document.toObject(UsersDbModel::class.java)
                            if (usersDbModel.loginType.equals(Const.USER_TYPE_FACEBOOK) && usersDbModel.socialId.equals(
                                    account.uid
                                )
                            ) {
                                setPreferenceAndMoveToNext(usersDbModel)
                            } else {
                                hideProgressDialog()
                                showErrorMessage(getString(R.string.email_already_exist))
                            }
                            break
                        }
                    } else {
                        //authenticating with firebase
                        firebaseAuthWithFaceBook(account, credential)
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)
                }
            })
    }

    private fun firebaseAuthWithFaceBook(
        facebookAccount: FirebaseUser,
        credential: AuthCredential
    ) {
        //getting the auth credential

        //Now using firebase we are signing in the user here
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.getCurrentUser()

                    var userData = UsersDbModel()
                    userData.countryCode = ""
                    userData.email = facebookAccount.email!!
                    userData.profileUrl = facebookAccount.photoUrl.toString()
                    userData.socialId = facebookAccount.uid.toString()
                    userData.password = ""
                    userData.fullName = facebookAccount.displayName!!
                    userData.loginType = Const.USER_TYPE_FACEBOOK
                    userData.phoneNumber = ""
                    userData.lastSyncTime = 0
                    userData.callDetection = 1
                    userData.uuId = user!!.uid
                    userData.Subscription = "Free"

                    createUserAndRedirect(userData)
                } else {
                    // If sign in fails, display a message to the user.
                    hideProgressDialog()
                    Toast.makeText(
                        this@SignInActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // ...
            }
    }

    private fun firebaseAuthWithGoogle(googleAccount: GoogleSignInAccount) {

        //getting the auth credential
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)

        //Now using firebase we are signing in the user here
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.getCurrentUser()

                    var userData = UsersDbModel()
                    userData.countryCode = ""
                    userData.email = googleAccount.email!!
                    userData.profileUrl = googleAccount.photoUrl.toString()
                    userData.socialId = googleAccount.id.toString()
                    userData.password = ""
                    userData.fullName = googleAccount.displayName!!
                    userData.loginType = Const.USER_TYPE_GOOGLE
                    userData.phoneNumber = ""
                    userData.lastSyncTime = 0
                    userData.callDetection = 1
                    userData.uuId = user!!.uid
                    userData.Subscription = "Free"

                    createUserAndRedirect(userData)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@SignInActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                // ...
            }
    }

    private fun createUserAndRedirect(usersDbModel: UsersDbModel) {


        FireBaseUserHelper.getInstance()
            .createUser(usersDbModel, object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()
                    setPreferenceAndMoveToNext(data)
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)
                }

            })
    }


    private fun initView() {
        btnSignIn.setOnClickListener(this)
        btnFacebook.setOnClickListener(this)
        btnGoogle.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
        btnForgot.setOnClickListener(this)
//        if (BuildConfig.BUILD_TYPE.equals("debug")) {
//            etEmailPhoneNumber.setText("7990254268")
//            etPassword.setText("Test105*")
//        }


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSignIn -> {
                if (isValid()) {
                    checkloginWithFirebase()
//                startActivity(NavigationDrawerActivity.getIntent(this))
                }
            }
            R.id.btnFacebook -> {
                showErrorMessage("Facebook")
//                fb_login_button.performClick()
//                startActivity(NavigationDrawerActivity.getIntent(this))
            }
            R.id.btnGoogle -> {
                googleSignIn()
            }
            R.id.btnRegister -> {
                startActivity(SignUpActivity.getIntent(this))
            }
            R.id.btnForgot -> {
                startActivity(ForgotPasswordActivity.getIntent(this))

            }
        }
    }

    private fun checkloginWithFirebase() {
        if (isValidMail(etEmailPhoneNumber.text.toString().trim())) {
            checkUserWithEmail()
        } else if (isValidMobile(etEmailPhoneNumber.text.toString().trim())) {
            checkWithPhoneNumber()
        } else {
            showErrorMessage(getString(R.string.please_enter_valid_email_or_phone_number))
        }
    }

    private fun checkWithPhoneNumber() {

        showProgressDialog()

        FireBaseUserHelper.getInstance().loginWithPhoneNumber(etEmailPhoneNumber.text.toString(),
            etPassword.text.toString(),
            object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()
                    setPreferenceAndMoveToNext(data)
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)
                }

            })
    }

    private fun setPreferenceAndMoveToNext(data: UsersDbModel) {

        PreferenceHelper.getInstance().setProfileData(data)
        PreferenceHelper.getInstance().lastSyncCallTimeStamp = data.lastSyncTime
        PreferenceHelper.getInstance().isLogin = true
        getCategorizedData(data)

    }

    private fun getCategorizedData(userData: UsersDbModel) {
        showProgressDialog()
        FireBaseCallCategoryHelper.getInstance()
            .getCategorizedCall(object :
                FirebaseDatabasePaginationListener<ArrayList<CallCategoryDbModel?>> {
                override fun onSuccess(
                    lastDocument: DocumentSnapshot,
                    data: ArrayList<CallCategoryDbModel?>
                ) {
                    hideProgressDialog()
                    if (data.size > 0) {
                        insertIntoLocalDatabase(data)
                    }
                    registerApi(userData)
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }

                override fun onEmpty() {
                    hideProgressDialog()
                    redirectToHomeScreen()

                }

            })
    }

    val TAG: String = "SignInActivity"
    private fun insertIntoLocalDatabase(data: ArrayList<CallCategoryDbModel?>) {

        var appDatabase = DatabaseHelper.getInstance(this).appDataBase
        appDatabase.getCallCategoryDao().insertAllData(data)

        Log.v(
            TAG,
            "insertIntoLocalDatabase : " + appDatabase.getCallCategoryDao().getAllData().size
        )

    }


    private fun checkUserWithEmail() {

        showProgressDialog()

        FireBaseUserHelper.getInstance().loginWithEmailId(etEmailPhoneNumber.text.toString(),
            etPassword.text.toString(),
            object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()
                    setPreferenceAndMoveToNext(data)
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(errorMessage)
                }

            })
    }

    private fun redirectToHomeScreen() {

        if (swRememberMe.isChecked) {
            PreferenceHelper.getInstance().isRememberMe = true
        } else {
            PreferenceHelper.getInstance().isRememberMe = false
        }
//        startActivity(SubscriptionActivity.getIntent(this))
        startActivity(NavigationDrawerActivity.getIntent(this))

    }

    fun isValid(): Boolean {
        if (etEmailPhoneNumber.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_enter_email))
            return false
        }
        if (etPassword.text.isNullOrEmpty()) {
            showErrorMessage(getString(R.string.err_password))
            return false
        }
        return true
    }

    private fun isValidMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidMobile(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }

}
