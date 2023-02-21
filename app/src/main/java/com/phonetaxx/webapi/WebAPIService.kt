package com.sixmmedicine.webapi

import com.phonetaxx.firebase.model.*
import com.phonetaxx.firebase.model.card.CardResponse
import com.phonetaxx.firebase.model.createsub.CreateSubResponse
import com.phonetaxx.firebase.model.plan.PlanResponse
import com.phonetaxx.firebase.model.subscription.CheckSubscriptionResponse
import com.phonetaxx.model.RegisterRequest
import com.phonetaxx.model.RegisterResponse
import com.squareup.okhttp.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface WebAPIService {


    @POST("create_customer")
    fun createStripeCustomer(@Body request: CreateStripeCustomerRequest): Call<CreateStripeCustomerResponse>

//    @POST("create_ephemeral_key")
//    fun getEphemeralKey(@Body request: EmperalKeyRequest): Call<EmpheralKeyResponse>

    @POST("set_card_as_default")
    fun setCardAsDefault(@Body request: SetDefaultCardRequest): Call<PlanResponse>

    @POST("list_cards")
    fun getAllCards(@Body request: SetDefaultCardRequest): Call<CardResponse>

    @GET("list_plans")
    fun getPlans(): Call<PlanResponse>


    @POST("create_card")
    fun createCard(@Body request: CreateCardRequest): Call<CreateCardResponse>

    @POST("check_subscription_status")
    fun checkSubscription(@Body request: SetDefaultCardRequest): Call<CheckSubscriptionResponse>

    @POST("create_subscription")
    fun createSubSciption(@Body createSubRequest: CreateSubRequest): Call<CreateSubResponse>

    @POST("cancel_subscription")
    fun cancelSubScription(@Body createSubRequest: CancelSubRequest): Call<PlanResponse>

    @POST("check_invoice_status")
    fun getInvoice(@Body createSubRequest: InvoiceRequest): Call<ResponseBody>

    @POST("https://dev.theappkit.co.uk/phonetax/public/api/register")
    fun register(@Body createSubRequest: RegisterRequest): Call<RegisterResponse>

}