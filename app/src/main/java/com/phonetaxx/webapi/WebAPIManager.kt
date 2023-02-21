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


class WebAPIManager private constructor() {

    private val mApiService: WebAPIService = WebAPIServiceFactory.newInstance().makeServiceFactory()

    companion object {

        private var INSTANCE: WebAPIManager? = null

        val instance: WebAPIManager
            get() {
                if (INSTANCE == null) {
                    INSTANCE = WebAPIManager()
                }
                return INSTANCE as WebAPIManager
            }
    }

    fun createStripeCustomer(
        request: CreateStripeCustomerRequest
    ): Call<CreateStripeCustomerResponse> {
        return mApiService.createStripeCustomer(request)
    }

//    fun getEphemeralKey(apiVersion: EmperalKeyRequest): Call<EmpheralKeyResponse> {
//        return mApiService.getEphemeralKey(apiVersion)
//    }

    fun setCardAsDefaultlKey(apiVersion: SetDefaultCardRequest): Call<PlanResponse> {
        return mApiService.setCardAsDefault(apiVersion)
    }

    fun getAllCards(apiVersion: SetDefaultCardRequest): Call<CardResponse> {
        return mApiService.getAllCards(apiVersion)
    }

    fun getPlans(): Call<PlanResponse> {
        return mApiService.getPlans()
    }

    fun createCard(createCardRequest: CreateCardRequest): Call<CreateCardResponse> {
        return mApiService.createCard(createCardRequest)
    }

    fun checkSubscription(setDefaultCardRequest: SetDefaultCardRequest): Call<CheckSubscriptionResponse> {
        return mApiService.checkSubscription(setDefaultCardRequest)
    }

    fun createSubSciption(createSubRequest: CreateSubRequest): Call<CreateSubResponse> {
        return mApiService.createSubSciption(createSubRequest)
    }

    fun cancelSubScription(cancelSubRequest: CancelSubRequest): Call<PlanResponse> {
        return mApiService.cancelSubScription(cancelSubRequest)
    }

    fun getInvoice(cancelSubRequest: InvoiceRequest): Call<ResponseBody> {
        return mApiService.getInvoice(cancelSubRequest)
    }

    fun register(request: RegisterRequest): Call<RegisterResponse> {
        return mApiService.register(request)
    }
}
