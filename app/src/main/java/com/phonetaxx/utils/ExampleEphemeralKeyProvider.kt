package com.phonetaxx.utils

import androidx.annotation.Size
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener

class ExampleEphemeralKeyProvider : EphemeralKeyProvider {

    override fun createEphemeralKey(
        @Size(min = 4) apiVersion: String,
        keyUpdateListener: EphemeralKeyUpdateListener
    ) {

//        var request = EmperalKeyRequest(
//            PreferenceHelper.getInstance().getProfileData()!!.stripeCustomerId.toString(),
//            apiVersion
//        );
//
//
//        WebAPIManager.instance.getEphemeralKey(request)
//            .enqueue(object : RemoteCallback<EmpheralKeyResponse>() {
//                override fun onSuccess(response: EmpheralKeyResponse?) {
//
//                    keyUpdateListener.onKeyUpdate(response!!.empheral_key!!.toString())
//                }
//
//                override fun onUnauthorized(throwable: Throwable) {
//                }
//
//                override fun onFailed(throwable: Throwable) {
//
//                }
//
//                override fun onInternetFailed() {
//                }
//
//                override fun onEmptyResponse(message: String) {
//                }
//
//            })

    }
}
