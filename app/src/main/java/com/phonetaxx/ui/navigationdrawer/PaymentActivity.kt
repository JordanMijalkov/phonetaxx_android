package com.phonetaxx.ui.navigationdrawer

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jamangle.webapi.RemoteCallback
import com.phonetaxx.BuildConfig
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.*
import com.phonetaxx.firebase.model.plan.PlanResponse
import com.phonetaxx.utils.ExampleEphemeralKeyProvider
import com.phonetaxx.utils.PreferenceHelper
import com.sixmmedicine.webapi.WebAPIManager
import com.stripe.android.ApiResultCallback
import com.stripe.android.CustomerSession
import com.stripe.android.Stripe
import com.stripe.android.model.Source
import com.stripe.android.model.SourceParams
import com.stripe.android.view.PaymentMethodsActivityStarter
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.runBlocking

class PaymentActivity : AppCompatActivity(), View.OnClickListener {
    var stripe: Stripe? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        initView()
    }

    private fun initView() {
        cardviewToolbar.setBackgroundResource(R.drawable.toolbar_card_background)
        tvToolbarTitle.setText(getString(R.string.payment))
        ivBackToolbar.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        stripe = Stripe(applicationContext, BuildConfig.STRIPE_PUBLISHABLE_KEY)

    }

    val TAG: String = "PaymentActivity"
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackToolbar -> {
                onBackPressed()
            }
            R.id.btnPay -> {

                if (TextUtils.isEmpty(
                        PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId
                    )
                ) {
                    //Call API to create Customer
                    createStripeCustomer()

                } else {

                    //Get Customner Id which is already there.
                    Log.d(
                        "STRIPE_CUSTOMER_ID : ",
                        PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId!!
                    )
                    runBlocking {
                        initPaymentMethod()
                    }
                    startPayment()


                }

            }
        }
    }

    private fun createStripeCustomer() {

        showProgressDialog()
        var createStripeCustomerRequest = CreateStripeCustomerRequest()
        createStripeCustomerRequest.name =
            PreferenceHelper.getInstance().getProfileData()!!.fullName
        createStripeCustomerRequest.email = PreferenceHelper.getInstance().getProfileData()!!.email

        WebAPIManager.instance.createStripeCustomer(createStripeCustomerRequest)
            .enqueue(object : RemoteCallback<CreateStripeCustomerResponse>() {
                override fun onSuccess(response: CreateStripeCustomerResponse?) {
                    hideProgressDialog()
                    if (response != null) {
                        if (!TextUtils.isEmpty(response.getResult()!!.customer_id)) {
                            updateStripeCustomerId(response.getResult()!!.customer_id!!)
                        }
                    }

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

    private fun updateStripeCustomerId(stripeCustomerId: String) {

        FireBaseUserHelper.getInstance().updateStripeCustomerId(stripeCustomerId,
            object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()
                    Log.d(
                        "STRIPE_CUSTOMER_ID : ",
                        PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId!!
                    )
                    runBlocking {
                        initPaymentMethod()
                    }
                    startPayment()

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }

            })
    }

    private fun initPaymentMethod() {
        CustomerSession.initCustomerSession(this, ExampleEphemeralKeyProvider())
    }

    private fun startPayment() {
//        paymentSession = PaymentSession(
//            this,
//            PaymentSessionConfig.Builder()
//                .setShippingMethodsRequired(false)
//                .build()
//        )
//
//
//        setupPaymentSession()

        try {
            val card = cardInputWidget.card
            val cardSourceParams = SourceParams.createCardParams(card!!)
// The asynchronous way to do it. Call this method on the main thread.
            stripe!!.createSource(
                cardSourceParams,
                callback = object : ApiResultCallback<Source> {
                    override fun onSuccess(source: Source) {
                        // Store the source somewhere, use it, etc
                        Log.d("createSource", source.id!!)
                        addCard(source.id!!)

                    }

                    override fun onError(error: Exception) {
                        // Tell the user that something went wrong
                        Log.d("createSource", error.message!!)

                    }
                }
            )
        } catch (e: Exception) {
            showToast(getString(R.string.valide_card_details))
        }


// The synchronous way to do it (DON'T DO BOTH)
//        val source = stripe.createSourceSynchronous(cardSourceParams)

    }


    private fun addCard(sourceId: String) {
        showProgressDialog()
        var request = CreateCardRequest()
        request.customer_id = PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId!!
        request.source_id = sourceId

        WebAPIManager.instance.createCard(request)
            .enqueue(object : RemoteCallback<CreateCardResponse>() {
                override fun onSuccess(response: CreateCardResponse?) {
                    hideProgressDialog()
                    Log.d("createCard", "onSuccess")
                    finish()
                }

                override fun onUnauthorized(throwable: Throwable) {
                    hideProgressDialog()

                    Log.d("createCard", "onUnauthorized")

                }

                override fun onFailed(throwable: Throwable) {
                    hideProgressDialog()

                    Log.d("createCard", "onFailed")

                }

                override fun onInternetFailed() {
                    hideProgressDialog()

                    Log.d("createCard", "onInternetFailed")

                }

                override fun onEmptyResponse(message: String) {
                    hideProgressDialog()

                    Log.d("createCard", "onEmptyResponse")
                }

            })
    }


    private fun launchPaymentMethodsActivity() {

        var argument = PaymentMethodsActivityStarter.Args.Builder()
            .setShouldShowGooglePay(false)
            .build()
        PaymentMethodsActivityStarter(this).startForResult(argument);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(
            TAG,
            " : " + requestCode + " resultCode : " + resultCode + "- data :" + data.toString()
        );
        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE && data != null) {
            val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
            val paymentMethod = result?.paymentMethod
            Log.v(TAG, " paymentMethod : " + (paymentMethod?.card));
            if (!TextUtils.isEmpty(paymentMethod?.id)) {
                setCardAsDeafult(paymentMethod?.id!!)

            }
//            callPaymentAPI(paymentMethod?.id)

            // use paymentMethod

        }

    }

    private fun setCardAsDeafult(cardid: String) {

        var request = SetDefaultCardRequest()
        request.customer_id = PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId
        request.card_id = cardid


        WebAPIManager.instance.setCardAsDefaultlKey(request)
            .enqueue(object : RemoteCallback<PlanResponse>() {
                override fun onSuccess(response: PlanResponse?) {

                }

                override fun onUnauthorized(throwable: Throwable) {
                }

                override fun onFailed(throwable: Throwable) {

                }

                override fun onInternetFailed() {
                }

                override fun onEmptyResponse(message: String) {
                }

            })
    }

}
