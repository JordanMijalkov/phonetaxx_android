package com.phonetaxx.ui.navigationdrawer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.android.billingclient.api.*
import com.phonetaxx.R
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.DateTimeHelper
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_subscription.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SubscriptionActivity : BaseActivity(), View.OnClickListener {

    private var mSkuDetailMonth: SkuDetails? = null
    private var mSkuDetailYear: SkuDetails? = null
    lateinit private var billingClient: BillingClient
    val skuList = ArrayList<String>()
    private var selectPlan = 0
    private var activePlan = 0

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, SubscriptionActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        ivBack.setOnClickListener(this)
        cardMonthlyPlan.setOnClickListener(this)
        cardAnnualPlan.setOnClickListener(this)
        llTermAndCondition.setOnClickListener(this)
        btnChangeCard.setOnClickListener(this)
        btnCancelSubScription.setOnClickListener(this)

        llTermAndCondition.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.terms_of_services_url))
            )
            startActivity(browserIntent)
        }

        llTermAndConditionSub.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.terms_of_services_url))
            )
            startActivity(browserIntent)
        }

        setupBillingClient()
        checkPlan()

    }

    private fun checkPlan() {

        if (!PreferenceHelper.getInstance().getProfileData()?.Subscription.equals("Free")) {
            if(!PreferenceHelper.getInstance().getProfileData()?.PlanExpiryDate.equals("")) {
                val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy")
                val parsed =
                    sdf.parse(PreferenceHelper.getInstance().getProfileData()?.PlanExpiryDate)
                val now = Date(System.currentTimeMillis())

                if (parsed.compareTo(now) >= 0) {
                    activePlan = 1
                }
            }
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(this)
            .enablePendingPurchases()
            .setListener(purchaseUpdateListener)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i("subs", "Billing client successfully set up")

                    queryOneTimeProducts(0)
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i("subs", "Billing service disconnected")
            }
        })
    }

    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.v("TAG_INAPP", "billingResult responseCode : ${billingResult.responseCode}")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
//                        handleNonConcumablePurchase(purchase)
                    //  handleConsumedPurchases(purchase)
                    setPaymentSubscription(purchase);
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                showToast("Payment cancelled")
            } else {
                // Handle any other error codes.
                showToast("Payment not complete! please try again")
            }
        }

    private fun setPaymentSubscription(purchase: Purchase?) {

        val currentTimestamp = System.currentTimeMillis()
        var mPlan: String

        var starDate = DateTimeHelper.getInstance().getDisplayDateFormat(currentTimestamp)
        var endDate = ""

        if (selectPlan == 1) {
            mPlan = Const.MONTHLY_PLAN
            val current = Calendar.getInstance()
            current.add(Calendar.DATE, 30)
            endDate = DateTimeHelper.getInstance().getDisplayDateFormat(current.timeInMillis)
        } else {
            mPlan = Const.ANNUAL_PLAN
            val current = Calendar.getInstance()
            current.add(Calendar.DATE, 365)
            endDate = DateTimeHelper.getInstance().getDisplayDateFormat(current.timeInMillis)
        }

        FireBaseUserHelper.getInstance()
            .updateSubscription(mPlan, object :
                FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateStartDate(starDate, object :
                FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateEndDate(endDate, object :
                FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })


    }

    private fun queryOneTimeProducts(type: Int) {
        val skuListToQuery = ArrayList<String>()


//        skuListToQuery.add("phonetaxx_annual_subscription")
//        skuListToQuery.add("phonetaxx_monthly_subscriptior")
        skuListToQuery.add("android.test.purchased")
        skuListToQuery.add("android.test.purchased")


        // ‘coins_5’ is the product ID that was set in the Play Console.
        // Here is where we can add more product IDs to query for based on
        //   what was set up in the Play Console.

        val params = SkuDetailsParams.newBuilder()
        params
            .setSkusList(skuListToQuery)
            .setType(BillingClient.SkuType.INAPP)
        // SkuType.INAPP refers to 'managed products' or one time purchases.
        // To query for subscription products, you would use SkuType.SUBS.

        billingClient.querySkuDetailsAsync(
            params.build(),
            object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(
                    p0: BillingResult,
                    skuDetails: MutableList<SkuDetails>?
                ) {
                    Log.i("subs", "onSkuDetailsResponse ${p0?.responseCode}")
                    if (skuDetails != null) {
                        for ((i, skuDetail) in skuDetails.withIndex()) {
                            Log.i("subs", skuDetail.toString())
                            if (i == 0) {
                                mSkuDetailMonth = skuDetail
                            } else {
                                mSkuDetailYear = skuDetail
                            }
                        }
                    } else {
                        Log.i("subs", "No skus found from query")
                    }
                }

            })

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cardMonthlyPlan -> {
                if(activePlan ==1){
                    showToast("You have active plan")
                }else {
                    selectPlan = 1
                    skuList?.let {
                        val billingFlowParams = mSkuDetailMonth?.let { it1 ->
                            BillingFlowParams.newBuilder()
                                .setSkuDetails(it1)
                                .build()
                        }
                        billingFlowParams?.let { it1 ->
                            billingClient?.launchBillingFlow(
                                this,
                                it1
                            )?.responseCode
                        }
                    } ?: noSKUMessage()
                }
            }
            R.id.cardAnnualPlan -> {
                if(activePlan ==1){
                    showToast("You have active plan")
                }else {
                    selectPlan = 2
                    skuList?.let {
                        val billingFlowParams = mSkuDetailYear?.let { it1 ->
                            BillingFlowParams.newBuilder()
                                .setSkuDetails(it1)
                                .build()
                        }
                        billingFlowParams?.let { it1 ->
                            billingClient?.launchBillingFlow(
                                this,
                                it1
                            )?.responseCode
                        }
                    } ?: noSKUMessage()
                }
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.llTermAndCondition -> {
//                showErrorMessage("Term & condition")
            }

            R.id.btnChangeCard -> {

            }
            R.id.btnCancelSubScription -> {

            }
        }
    }

    private fun noSKUMessage() {

    }

}




