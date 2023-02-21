package com.phonetaxx.firebase.model

class CreateStripeCustomerResponse {

    private var message: String? = null
    private var result: Result? = null

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getResult(): Result? {
        return result
    }

    fun setResult(result: Result?) {
        this.result = result
    }


}

class Result {
    var customer_id: String? = null

}