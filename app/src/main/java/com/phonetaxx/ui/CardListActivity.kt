package com.phonetaxx.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jamangle.webapi.RemoteCallback
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.*
import com.phonetaxx.firebase.model.card.Card
import com.phonetaxx.firebase.model.card.CardResponse
import com.phonetaxx.firebase.model.createsub.CreateSubResponse
import com.phonetaxx.firebase.model.plan.PlanResponse
import com.phonetaxx.ui.navigationdrawer.PaymentActivity
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.PreferenceHelper
import com.sixmmedicine.webapi.WebAPIManager
import kotlinx.android.synthetic.main.activity_card_list.*
import kotlinx.android.synthetic.main.toolbar_layout.*


class CardListActivity : AppCompatActivity(), View.OnClickListener,
    MyRecyclerViewAdapter.ItemClickListener {

    var adapter: MyRecyclerViewAdapter? = null
    val cards: ArrayList<Card> = ArrayList()
    var isSubScriptionAvailable = false
    var price_id = ""
    var purchaseType = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)
        initView()
    }

    private fun initView() {
        isSubScriptionAvailable = intent.getBooleanExtra(Const.isSubScriptionAvailable, false)
        if (intent.getStringExtra(Const.SUB_PRICE_ID) != null) {
            price_id = intent.getStringExtra(Const.SUB_PRICE_ID).toString()
            purchaseType = intent.getStringExtra(Const.SUB_PRICE_TYPE).toString()
        }

        cardviewToolbar.setBackgroundResource(R.drawable.toolbar_card_background)
        tvToolbarTitle.setText(getString(R.string.payment))
        ivBackToolbar.setOnClickListener(this);
        btnAddNewCard.setOnClickListener(this);

    }

    override fun onResume() {
        super.onResume()
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
            getAllCards()


        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackToolbar -> {
                onBackPressed()
            }
            R.id.btnAddNewCard -> {
                var intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)

            }

        }
    }

    private fun getAllCards() {
        showProgressDialog()
        var request = SetDefaultCardRequest()
        request.customer_id = PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId

        WebAPIManager.instance.getAllCards(request)
            .enqueue(object : RemoteCallback<CardResponse>() {
                override fun onSuccess(response: CardResponse?) {
                    hideProgressDialog()
                    // data to populate the RecyclerView with
                    cards.clear()
                    if (response != null && response.result.cards.size > 0) {
                        tvNoCards.visibility = View.GONE
                        for (card in response.result.cards) {
                            cards.add(card)
                        }
                    } else {
                        tvNoCards.visibility = View.VISIBLE
                    }
                    // set up the RecyclerView
                    rvCards.layoutManager = LinearLayoutManager(this@CardListActivity)
                    adapter =
                        MyRecyclerViewAdapter(this@CardListActivity, cards, isSubScriptionAvailable)
                    adapter!!.setClickListener(this@CardListActivity)
                    rvCards.adapter = adapter!!
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
                    getAllCards()

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }

            })
    }

    override fun onItemClick(view: View?, position: Int) {
        if (isSubScriptionAvailable) {
            if (cards.get(position).default) {
                showToast("Card is already set as default card")
            } else {
                AlertDialog.Builder(this@CardListActivity)
                    .setTitle("Set Default Card")
                    .setMessage(
                        "Are you sure you want to select ${cards.get(position).card.brand} - **** **** **** ${
                            cards.get(
                                position
                            ).card.last4
                        } as your default card? make sure your all payment transaction will be done from the selected(default) card."
                    )
                    .setPositiveButton("Set as a default") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        setCardAsDeafult(cards.get(position).id, false)
                    }
                    .setNegativeButton(
                        getString(R.string.cancel)
                    ) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }.create().show()
            }


        } else {
            AlertDialog.Builder(this@CardListActivity)
                .setTitle("Turn On Subscription")
                .setMessage(
                    "Are you sure you want to select ${cards.get(position).card.brand} - **** **** **** ${
                        cards.get(
                            position
                        ).card.last4
                    } as your default card? make sure your all payment transaction will be done from the selected(default) card."
                )
                .setPositiveButton("Pay Now") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    if (cards.get(position).default) {
                        createSubSciption(price_id)

                    } else {
                        setCardAsDeafult(cards.get(position).id, true)
                    }
                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.create().show()
        }

    }

    private fun setCardAsDeafult(cardid: String, turnOn: Boolean) {
        showProgressDialog()
        var request = SetDefaultCardRequest()
        request.customer_id = PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId
        request.card_id = cardid


        WebAPIManager.instance.setCardAsDefaultlKey(request)
            .enqueue(object : RemoteCallback<PlanResponse>() {
                override fun onSuccess(response: PlanResponse?) {
                    hideProgressDialog()
                    if (turnOn) {
                        createSubSciption(price_id)
                    } else {
                        getAllCards()

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

    private fun createSubSciption(priceId: String) {
        showProgressDialog()
        var request = CreateSubRequest()
        request.customer_id = PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId
        request.price_id = priceId


        WebAPIManager.instance.createSubSciption(request)
            .enqueue(object : RemoteCallback<CreateSubResponse>() {
                override fun onSuccess(response: CreateSubResponse?) {
                    hideProgressDialog()
                    isSubScriptionAvailable = true
                    showToast(response!!.message)
                    var subscriptionModel = SubscriptionModel()
                    subscriptionModel.priceId = priceId
                    subscriptionModel.planType = purchaseType
                    subscriptionModel.subscriptionId = response.result.subscriptionId
                    subscriptionModel.invoiceId = response.result.invoiceId

                    updateUserSubscription(subscriptionModel)
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

    private fun updateUserSubscription(subscriptionModel: SubscriptionModel) {

        FireBaseUserHelper.getInstance().updateUserSubscription("Free",
            object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    var intent = Intent();
                    setResult(RESULT_OK, intent)
                    finish()

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                }

            })
    }

}