package com.phonetaxx.ui

import RuntimePermissionHelper
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.phonetaxx.R
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.ui.navigationdrawer.NavigationDrawerActivity
import com.phonetaxx.ui.signin.SignInActivity
import com.phonetaxx.utils.PreferenceHelper
import java.util.*


class SplashActivity : BaseActivity(), RuntimePermissionHelper.PermissionCallbacks {
    override fun onPermissionAllow(permissionCode: Int) {
        moveToNext();

    }

    override fun onPermissionDeny(permissionCode: Int) {
    }

    private val RC_CALL_LOG = 123
    private val TAG = SplashActivity::class.java.simpleName
    internal var handler = Handler()
    internal var runnable: Runnable = Runnable { openNextActivity() }
    private val SPLASH_TIME_OUT = 2000
    private var listOfCalls: HashMap<String, CallLogsDbModel> = hashMapOf<String, CallLogsDbModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        );

/*
        if (PreferenceHelper.getInstance().isLogin!!) {
            methodRequiresTwoPermission()
        }
*/
    }

    override fun onResume() {
        super.onResume()
        moveToNext()
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    private fun openNextActivity() {
        finish()
        if (PreferenceHelper.getInstance().isLogin!!) {
            startActivity(NavigationDrawerActivity.getIntent(this))
//            startActivity(SubscriptionActivity.getIntent(this))
        } else {
            startActivity(SignInActivity.getIntent(this))
        }

    }


    private fun moveToNext() {
        handler.postDelayed(runnable, SPLASH_TIME_OUT.toLong())
    }

    private fun requestPermissionForCall() {
        val permissions = arrayOf<String?>(
            Manifest.permission.READ_CALL_LOG
        )
        val runtimePermission = RuntimePermissionHelper.newInstance(
            permissions,
            RC_CALL_LOG,
            "Application need call log permission."
        )
        runtimePermission.setAppPermissionListener(this)
        runtimePermission.show(supportFragmentManager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}


