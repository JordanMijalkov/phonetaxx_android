package com.phonetaxx.ui.navigationdrawer

import RuntimePermissionHelper
import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.telecom.TelecomManager
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore
import com.phonetaxx.AppClass
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseCallLogHelper
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.*
import com.phonetaxx.listener.DrawerMenuChangeListener
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.model.HeaderDataModel
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.ui.EditProfile
import com.phonetaxx.ui.navigationdrawer.accountsetting.AccountSettingFragment
import com.phonetaxx.ui.navigationdrawer.accountsetting.MonthlyPhoneBillDialogFragment
import com.phonetaxx.ui.navigationdrawer.home.MyHomeFragment
import com.phonetaxx.ui.navigationdrawer.home.ReportDialogFragment
import com.phonetaxx.ui.navigationdrawer.phoneuses.PhoneUsesFragment
import com.phonetaxx.ui.navigationdrawer.uncategorized.UncateGerizedFragment
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.Const.Companion.CALL_LIMIT
import com.phonetaxx.utils.PreferenceHelper
import com.phonetaxx.utils.ReadCallLogHelper
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import kotlinx.android.synthetic.main.navigation_drawer_menu.*
import java.util.*


class NavigationDrawerActivity : BaseActivity(), View.OnClickListener, DrawerMenuChangeListener,
    RuntimePermissionHelper.PermissionCallbacks {
    private var mUsageStatsManager: UsageStatsManager? = null

    val TAG: String = "NavigationDrawer"
    var selectedMenu: String = ""
    val PERM_USAGE_ACCESS: Int = 1023

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, NavigationDrawerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    fun getUserDataFromUuid(
        uuId: String
    ) {
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()

        database.collection(FireBaseUserHelper.tblUsers).document(uuId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(UsersDbModel::class.java)
                PreferenceHelper.getInstance().setProfileData(user!!)
            }
            .addOnFailureListener(OnFailureListener { _ ->
            })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)

        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
        intent.putExtra(
            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
            this@NavigationDrawerActivity.packageName
        )
        startActivity(intent)


        AppClass.getInstance().setAlarmForNotification()
        getUserDataFromUuid(PreferenceHelper.getInstance().getProfileData()!!.uuId)
        if (PreferenceHelper.getInstance().getProfileData()!!.mothlyBillAmount > 0) {
            setupUsageAccessPermission()
        } else {
            checkMonthlyBilling()
        }

//        if (TextUtils.isEmpty(
//                PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId
//            )
//        ) {
//            //Call API to create Customer
//            createStripeCustomer()
//
//        }

    }

//    private fun createStripeCustomer() {
//
//        showProgressDialog()
//        var createStripeCustomerRequest = CreateStripeCustomerRequest()
//        createStripeCustomerRequest.name =
//            PreferenceHelper.getInstance().getProfileData()!!.fullName
//        createStripeCustomerRequest.email = PreferenceHelper.getInstance().getProfileData()!!.email
//
//        WebAPIManager.instance.createStripeCustomer(createStripeCustomerRequest)
//            .enqueue(object : RemoteCallback<CreateStripeCustomerResponse>() {
//                override fun onSuccess(response: CreateStripeCustomerResponse?) {
//                    hideProgressDialog()
//                    if (response != null) {
//                        if (!TextUtils.isEmpty(response.getResult()!!.customer_id)) {
//                            updateStripeCustomerId(response.getResult()!!.customer_id!!)
//                        }
//                    }
//
//                }
//
//                override fun onUnauthorized(throwable: Throwable) {
//                    hideProgressDialog()
//                }
//
//                override fun onFailed(throwable: Throwable) {
//                    hideProgressDialog()
//                }
//
//                override fun onInternetFailed() {
//                    hideProgressDialog()
//                }
//
//                override fun onEmptyResponse(message: String) {
//                    hideProgressDialog()
//                }
//
//            })
//
//    }
//
//    private fun updateStripeCustomerId(stripeCustomerId: String) {
//
//        FireBaseUserHelper.getInstance().updateStripeCustomerId(stripeCustomerId,
//            object : FirebaseDatabaseListener<UsersDbModel> {
//                override fun onSuccess(data: UsersDbModel) {
//                    hideProgressDialog()
//                    Log.d(
//                        "STRIPE_CUSTOMER_ID : ",
//                        PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId!!
//                    )
//
//                }
//
//                override fun onFail(errorMessage: String, exception: Exception?) {
//                    hideProgressDialog()
//                }
//
//            })
//    }


    override fun onResume() {
        super.onResume()
        if (PreferenceHelper.getInstance()
                .getProfileData()!!.Subscription != null && !TextUtils.isEmpty(
                PreferenceHelper.getInstance()
                    .getProfileData()!!.Subscription
            )
        ) {
            if (PreferenceHelper.getInstance()
                    .getProfileData()!!.Subscription.equals(Const.MONTHLY_PLAN)
            ) {
                tvCallLeft.text =
                    "You have subscribed Monthly Plan"
            } else if (PreferenceHelper.getInstance()
                    .getProfileData()!!.Subscription.equals(Const.ANNUAL_PLAN)
            ) {
                tvCallLeft.text =
                    "You have subscribed Annual Plan"

            }else{
                tvCallLeft.text =
                    "You have Free Plan"

            }

        } else {
            var callleft =
                (CALL_LIMIT + 1) - PreferenceHelper.getInstance().callRead!!
            if (callleft > 1) {
                tvCallLeft.text =
                    "" + callleft + " calls left"
            } else {
                if (callleft < 0) {
                    tvCallLeft.text =
                        "" + 0 + " call left"
                } else {
                    tvCallLeft.text =
                        "" + callleft + " call left"
                }
            }
        }
    }

    private fun checkMonthlyBilling() {

        var monthlyPhoneBillDialogFragment =
            MonthlyPhoneBillDialogFragment.newInstance("", "")
        monthlyPhoneBillDialogFragment.registerCallbackListener(object :
            FragmentCallBackListener<String> {
            override fun onSuccess(data: String) {
                setupUsageAccessPermission()
            }
        })
        monthlyPhoneBillDialogFragment.show(
            supportFragmentManager,
            "monthlyPhoneBillDialogFragment"
        )

    }

    private fun setupUsageAccessPermission() {
        val appOpsManager: AppOpsManager? =
            baseContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager!!.checkOpNoThrow(
            "android:get_usage_stats",
            Process.myUid(), packageName
        )

        val granted = mode == AppOpsManager.MODE_ALLOWED

        if (granted) {
            getData()
        } else {
//            Toast.makeText(this@NavigationDrawerActivity, "No GRANTED", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            //intent.data = Uri.fromParts("package", packageName, null)
            startActivityForResult(intent, PERM_USAGE_ACCESS);
        }

    }

    var totalScreenTimeInSeconds: Long = 0
    private fun getData() {
        Log.v(TAG, "getData: ");
        mUsageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val stats: List<UsageStats> = mUsageStatsManager!!.queryUsageStats(
            UsageStatsManager.INTERVAL_YEARLY,
            0, Calendar.getInstance().timeInMillis
        )
        if (stats == null) {
//            Toast.makeText(this@NavigationDrawerActivity, "No Status", Toast.LENGTH_SHORT).show()
        } else {

            val statCount = stats.size
            totalScreenTimeInSeconds = 0
            for (i in 0 until statCount) {
                val pkgStats = stats[i]

                try {

                    if (pkgStats.packageName.toString().equals(packageName)) {
                        totalScreenTimeInSeconds =
                            totalScreenTimeInSeconds + (pkgStats.totalTimeInForeground / 1000)
                        Log.v(TAG, " CHIRAG getData : " + totalScreenTimeInSeconds)
                        Log.v(
                            "CHIRAG -- ", DateUtils.formatElapsedTime(
                                pkgStats.totalTimeInForeground / 1000
                            )
                        )
                    }

                } catch (e: PackageManager.NameNotFoundException) {
                    // This package may be gone.
                }
                PreferenceHelper.getInstance().totalScreenTimeInSecond = totalScreenTimeInSeconds
            }

        }

        initView()
        openHomeFragment()
        requestPermissionForCall()
    }


    private fun setHeaderData(data: HeaderDataModel) {
        tvCalls.setText(data.calls)
        tvMinuteTalk.setText(data.minuteTalked)
        tvBussinessExpense.setText(data.expense)
    }


    private fun getCallDetail() {
        showProgressDialog()

        Log.v(
            TAG,
            " lastSyncCallTimeStamp : " + PreferenceHelper.getInstance().lastSyncCallTimeStamp
        );

        ReadCallLogHelper.getInstance(this)
            .getCallDetailNew(
                PreferenceHelper.getInstance().lastSyncCallTimeStamp,
                object : ReadCallLogHelper.ReadCallLogListener {
                    override fun onCompleted(result: ArrayList<CallLogsDbModel>?) {
                        Log.v(TAG, "result : " + (result?.size));
                        var list = arrayListOf<CallLogsDbModel>()
                        Log.v(
                            TAG,
                            "lastSyncCallTimeStamp : " + PreferenceHelper.getInstance().lastSyncCallTimeStamp
                        );

                        var lasySyncTime = PreferenceHelper.getInstance().lastSyncCallTimeStamp

                        Log.v(
                            TAG,
                            ("lastSyncCallTimeStamp 2 : " + lasySyncTime.toString().equals("0"))
                        );

                        if (!lasySyncTime.toString()
                                .equals("0") && PreferenceHelper.getInstance()
                                .getProfileData()!!.Subscription != null && !TextUtils.isEmpty(
                                PreferenceHelper.getInstance()
                                    .getProfileData()!!.Subscription
                            )
                        ) {
                            Log.v(TAG, "result?.size 2 : " + result?.size);
                            list = result!!
                            if (PreferenceHelper.getInstance()
                                    .getProfileData()!!.Subscription.equals(Const.MONTHLY_PLAN)
                            ) {
                                tvCallLeft.text =
                                    "You have subscribed Monthly Plan"
                            } else if (PreferenceHelper.getInstance()
                                    .getProfileData()!!.Subscription.equals(Const.ANNUAL_PLAN)
                            ) {
                                tvCallLeft.text =
                                    "You have subscribed Annual Plan"

                            } else{
                                tvCallLeft.text =
                                    "You have free Plan"
                            }

                        } else {
                            var callRead = 0
                            var previouselyReadCall = PreferenceHelper.getInstance().callRead
                            if (lasySyncTime.toString()
                                    .equals("0")
                            ) {
                                Log.v(TAG, "result?.size 1 : " + result?.size);
                                if (result!!.size >= CALL_LIMIT + 1) {
                                    for (i in 0..CALL_LIMIT) {
                                        try {
                                            callRead++
                                            list.add(result.get(i))
                                            callRead--
                                        } catch (e: java.lang.Exception) {
                                            break
                                        }

                                    }
                                } else {
                                    for (i in 0..result.size) {
                                        try {
                                            callRead++
                                            list.add(result.get(i))
                                        } catch (e: java.lang.Exception) {
                                            callRead--
                                            break
                                        }

                                    }
                                }


                                PreferenceHelper.getInstance().callRead = callRead
                            } else if (previouselyReadCall!! <= CALL_LIMIT) {
                                Log.v(TAG, "result?.size 1 : " + result?.size);
                                callRead = previouselyReadCall
                                var readCall = (CALL_LIMIT + 1) - previouselyReadCall
                                if (result!!.size > 0) {
                                    for (i in 0..readCall) {
                                        try {
                                            callRead++
                                            list.add(result.get(i))
                                        } catch (e: java.lang.Exception) {
                                            break
                                        }
                                    }
                                }


                                PreferenceHelper.getInstance().callRead = callRead
                            }

                            var callleft =
                                (CALL_LIMIT + 1) - PreferenceHelper.getInstance().callRead!!
                            if (callleft > 1) {
                                tvCallLeft.text =
                                    "" + callleft + " calls left this month"
                            } else {
                                if (callleft < 0) {
                                    tvCallLeft.text =
                                        "" + 0 + " call left this month"
                                } else {
                                    tvCallLeft.text =
                                        "" + callleft + " call left this month"
                                }
                            }


                        }


                        if (list.size > 0 && list.size < 500) {
                            PreferenceHelper.getInstance().lastSyncCallTimeStamp =
                                System.currentTimeMillis()
                            FireBaseCallLogHelper.getInstance()
                                .addCallLog(list, object : FirebaseDatabaseListener<String> {
                                    override fun onSuccess(data: String) {
                                        hideProgressDialog()
                                        if (selectedMenu.equals(getString(R.string.menu_home))) {
                                            if (homeFragment != null) {
                                                homeFragment!!.setFilterData("")
                                            }
                                        }
                                    }

                                    override fun onFail(
                                        errorMessage: String,
                                        exception: Exception?
                                    ) {
                                        showErrorMessage(errorMessage)
                                        hideProgressDialog()
                                    }

                                })
                        } else {

                            if (list.size > 500) {
                                showErrorMessage("Unable to update more then 500 call to firestore")
                            }
                            hideProgressDialog()
                        }

                    }

                    override fun onStart() {
//                showToast("start")
                    }

                    override fun onFail() {
//                showToast("fail")
                        hideProgressDialog()
                    }

                })
    }

    private fun initView() {

        val cardviewToolbar: CardView = findViewById(R.id.cardviewToolbar);
        cardviewToolbar.setBackgroundResource(R.drawable.toolbar_card_background)
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        btnGetUnlimitedCalls.setOnClickListener(this)
        var callleft = (CALL_LIMIT + 1) - PreferenceHelper.getInstance().callRead!!

        tvCallLeft.text =
            "" + callleft + " calls left this month"

        val actionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {

                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val slideX = drawerView.width * slideOffset
                    rlMain.setTranslationX(slideX)
                }

                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    ivDrawerMenu.visibility = View.VISIBLE
                    ivClose.visibility = View.GONE

                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    ivDrawerMenu.visibility = View.GONE
                    ivClose.visibility = View.VISIBLE
                    setToolbarAmount()
                }
            }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        ivDrawerMenu.setOnClickListener(this)
        llDrawerHeader.setOnClickListener(this)
        ivDocument.setOnClickListener(this)
        rlToolbarTitle.setOnClickListener(this)
        menuHome.setOnClickListener(this)
        menuPhoneUsase.setOnClickListener(this)
        menuAccountSetting.setOnClickListener(this)
        menuHelpSupport.setOnClickListener(this)
        menuMonthlySummary.setOnClickListener(this)
        menuUncategorized.setOnClickListener(this)
        menuEditProfile.setOnClickListener(this)

        tvName.setText(PreferenceHelper.getInstance().getProfileData()?.fullName)
        tvEmail.setText(PreferenceHelper.getInstance().getProfileData()?.email)
        Glide.with(this)
            .load(PreferenceHelper.getInstance().getProfileData()!!.profileUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_avatar)
            .into(ivProfile)
        setToolbarAmount()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(p0: View?) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        when (p0?.id) {
            R.id.ivDrawerMenu -> {
//                showErrorMessage("ivDrawerMenu")

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            R.id.llDrawerHeader -> {
//                showErrorMessage("llDrawerHeader")

            }
            R.id.btnGetUnlimitedCalls -> {
                startActivity(SubscriptionActivity.getIntent(this))

            }
            R.id.ivDocument -> {
//                openReportFragment()

                requestPermissionForStorage()

            }
            R.id.rlToolbarTitle -> {
                openFilterDialog()
            }
            R.id.menuHome -> {
                openHomeFragment()

            }
            R.id.menuPhoneUsase -> {

                openPhoneusesFragment()
            }
            R.id.menuEditProfile -> {
                startActivity(Intent(this@NavigationDrawerActivity, EditProfile::class.java))
            }


            R.id.menuAccountSetting -> {

                openAccountSetting()
            }
            R.id.menuHelpSupport -> {
                openHelpAndSupport()
            }
            R.id.menuMonthlySummary -> {

                openMonthlySummary()
            }
            R.id.menuUncategorized -> {

                openUncategorizedFragment()
            }
        }
    }

    private fun openReportFragment() {
        if (selectedMenu.equals(getString(R.string.menu_home))) {
            if (homeFragment != null) {

                var filterDataList = homeFragment!!.getCurrentFilterCallLog()
                var reportDialogFragment =
                    ReportDialogFragment.newInstance(filterDataList, selectedFilterType)
                reportDialogFragment.show(supportFragmentManager, "reportDialogFragment")
            }
        }

    }

    var selectedFilterType = Const.TODAY
    private fun openFilterDialog() {
        val filterDialogFragment = FilterDialogFragment.newInstance("", "")
        filterDialogFragment.registerCallbackListener(object : FragmentCallBackListener<String> {
            override fun onSuccess(data: String) {
                selectedFilterType = data
                if (selectedMenu.equals(getString(R.string.menu_uncategorized))) {
                    if (uncateGerizedFragment != null) {
                        uncateGerizedFragment!!.setFilterData(data)
                    }
                }
                if (selectedMenu.equals(getString(R.string.menu_phone_usase))) {
                    if (phoneUsesFragment != null) {
                        phoneUsesFragment!!.setFilterData(data)
                    }
                }
                if (selectedMenu.equals(getString(R.string.menu_home))) {
                    if (homeFragment != null) {
                        homeFragment!!.setFilterData(data)
                    }
                }
                setToolbarTitle(data)
            }
        })
        filterDialogFragment.show(supportFragmentManager, "filterDialogFragment")
    }

    private fun openHomeFragment() {
        selectedMenu = getString(R.string.menu_home)
        ivDropdownToolbar.visibility = View.VISIBLE
        ivDocument.visibility = View.VISIBLE
        rlToolbarBottom.visibility = View.VISIBLE
        tvToolbarAmount.visibility = View.GONE
        rlToolbarTitle.isEnabled = true
        setToolbarTitle(getString(R.string.today))

        homeFragment = MyHomeFragment.newInstance()
        homeFragment!!.registerCallbackListener(object : FragmentCallBackListener<HeaderDataModel> {
            override fun onSuccess(data: HeaderDataModel) {
                setHeaderData(data)
            }

        })
        changeFragment(homeFragment!!)
    }

    var uncateGerizedFragment: UncateGerizedFragment? = null
    var phoneUsesFragment: PhoneUsesFragment? = null
    var homeFragment: MyHomeFragment? = null
    private fun openUncategorizedFragment() {

        selectedMenu = getString(R.string.menu_uncategorized)
        ivDropdownToolbar.visibility = View.VISIBLE
        ivDocument.visibility = View.GONE
        rlToolbarBottom.visibility = View.GONE
        tvToolbarAmount.visibility = View.VISIBLE
        rlToolbarTitle.isEnabled = true
        setToolbarTitle(getString(R.string.today))

        uncateGerizedFragment = UncateGerizedFragment.newInstance()
        changeFragment(uncateGerizedFragment!!)
    }

    private fun openPhoneusesFragment() {

        selectedMenu = getString(R.string.menu_phone_usase)
        ivDropdownToolbar.visibility = View.VISIBLE
        ivDocument.visibility = View.GONE
        rlToolbarBottom.visibility = View.GONE
        tvToolbarAmount.visibility = View.VISIBLE
        rlToolbarTitle.isEnabled = true
        setToolbarTitle(getString(R.string.today))
        phoneUsesFragment = PhoneUsesFragment.newInstance()
        changeFragment(phoneUsesFragment!!)
    }

    private fun openAccountSetting() {

        selectedMenu = getString(R.string.menu_account_settings)

        ivDropdownToolbar.visibility = View.GONE
        ivDocument.visibility = View.INVISIBLE
        rlToolbarBottom.visibility = View.GONE
        tvToolbarAmount.visibility = View.GONE
        rlToolbarTitle.isEnabled = false

        setToolbarTitle(getString(R.string.menu_account_settings))

        changeFragment(AccountSettingFragment.newInstance("", ""))
    }

    private fun openHelpAndSupport() {

        selectedMenu = getString(R.string.menu_help_support)

        ivDropdownToolbar.visibility = View.GONE
        ivDocument.visibility = View.INVISIBLE
        rlToolbarBottom.visibility = View.GONE
        tvToolbarAmount.visibility = View.GONE
        rlToolbarTitle.isEnabled = false

        setToolbarTitle(getString(R.string.menu_help_support))

        changeFragment(HelpAndSupportFragment.newInstance("", ""))
    }

    private fun openMonthlySummary() {

        selectedMenu = getString(R.string.menu_monthly_summaries)

        ivDropdownToolbar.visibility = View.GONE
        ivDocument.visibility = View.INVISIBLE
        rlToolbarBottom.visibility = View.GONE
        tvToolbarAmount.visibility = View.GONE
        rlToolbarTitle.isEnabled = false

        setToolbarTitle(getString(R.string.menu_monthly_summaries))


        changeFragment(MonthlySummaryFragment.newInstance("", ""))
    }

    private fun setToolbarTitle(title: String) {
        tvToolbarTitle.setText(title)

    }

    private fun setToolbarAmount() {
        tvToolbarAmount.setText(
            getString(R.string.currency_sign) + PreferenceHelper.getInstance()
                .getProfileData()?.mothlyBillAmount.toString()
        )
        if (PreferenceHelper.getInstance().getProfileData()?.callDetection == 1) {
            rlOnlineIndicator.visibility = View.VISIBLE
        } else {
            rlOnlineIndicator.visibility = View.GONE
        }
    }

    override fun setSelectedDrawerMenu(name: String) {
        when (name) {
            getString(R.string.menu_home) -> {
                openHomeFragment()
            }
            getString(R.string.menu_phone_usase) -> {
                openPhoneusesFragment()
            }
        }
    }


    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment).commitAllowingStateLoss()
    }

    private val PERMISSION_CALL_LOG_CODE = 123
    private val PERMISSION_STORAGE_CODE = 456

    private fun requestPermissionForCall() {
        Log.v(TAG, "requestPermissionForCall: ");
        val permissions = arrayOf<String?>(
            Manifest.permission.READ_CALL_LOG
        )
        val runtimePermission = RuntimePermissionHelper.newInstance(
            permissions,
            PERMISSION_CALL_LOG_CODE,
            "Application need call log permission."
        )
        runtimePermission.setAppPermissionListener(this)
        runtimePermission.show(supportFragmentManager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PERM_USAGE_ACCESS) {
            val appOpsManager: AppOpsManager? =
                baseContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager!!.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), packageName
            )
            val granted = mode == AppOpsManager.MODE_ALLOWED
            if (granted) {
                getData()
            } else {
                Toast.makeText(
                    this@NavigationDrawerActivity,
                    "Please enable the uses access for PhoneTAXX",
                    Toast.LENGTH_SHORT
                )
                    .show()
                initView()
                openHomeFragment()
                requestPermissionForCall()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPermissionAllow(permissionCode: Int) {
        when (permissionCode) {
            PERMISSION_CALL_LOG_CODE -> {
                Log.v(TAG, " onPermissionAllow: ");
                if (PreferenceHelper.getInstance().getProfileData()!!.callDetection == 1) {
                    getCallDetail()
                }
            }
            PERMISSION_STORAGE_CODE -> {
                openReportFragment()
            }
        }

    }

    override fun onPermissionDeny(permissionCode: Int) {

    }


    private fun requestPermissionForStorage() {

        val permissions = arrayOf<String?>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val runtimePermission = RuntimePermissionHelper.newInstance(
            permissions,
            PERMISSION_STORAGE_CODE,
            "Application need storage permission."
        )
        runtimePermission.setAppPermissionListener(this)
        runtimePermission.show(supportFragmentManager)

    }
}
