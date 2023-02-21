package com.jamangle.webapi

import android.text.TextUtils
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

/**
 * Generic class to handle web service response
 *
 * @author jaimin.shah on 19/12/16.
 */

abstract class RemoteCallback<T> : Callback<T> {

    /**
     * Overrides onReponse method and handles response of servers and reacts accordingly.
     *
     * @param call
     * @param response
     */
    override fun onResponse(call: Call<T>, response: Response<T>) {
        when (response.code()) {
            HttpsURLConnection.HTTP_OK, HttpsURLConnection.HTTP_CREATED, HttpsURLConnection.HTTP_ACCEPTED,
            HttpsURLConnection.HTTP_NOT_AUTHORITATIVE, HttpsURLConnection.HTTP_NO_CONTENT -> if (response.body() != null) {
                onSuccess(response.body())
            } else {
                onEmptyResponse(response.message())
            }
            423 -> onEmptyResponse(getErrorMessage(response))
            HttpURLConnection.HTTP_NOT_FOUND -> onFailed(Throwable(getErrorMessage(response)))
            HttpURLConnection.HTTP_UNAUTHORIZED -> onUnauthorized(Throwable(getErrorMessage(response)))
            else -> onFailed(Throwable(getErrorMessage(response)))
        }
    }

    private fun getErrorMessage(response: Response<T>?): String {
        if (response?.errorBody() == null) {
            return DEFAULT_ERROR_MSG
        }

        val jObjError: JSONObject
        try {
            var responseBody = response?.errorBody()!!.string()
            Log.d(TAG, "1 : " + responseBody)
            jObjError = JSONObject(responseBody)
        } catch (e: JSONException) {
            return DEFAULT_ERROR_MSG
        } catch (e: IOException) {
            return DEFAULT_ERROR_MSG
        }

        //gets message value which is returned by server
        var errorMessage = jObjError.optString("message", "")
        val errorMsg = jObjError.optString("error", "")
        if (!TextUtils.isEmpty(errorMessage)) {
            return errorMessage
        } else if (!TextUtils.isEmpty(errorMsg)) {
            return errorMsg
        } else {
            return DEFAULT_ERROR_MSG
        }
        /*
        Log.d(TAG, "2 :: "+ errorMessage);
        errorMessage = when {
            TextUtils.isEmpty(errorMessage) && !TextUtils.isEmpty(errorMsg) -> {
                errorMsg
            }
            else -> {
                DEFAULT_ERROR_MSG
            }
        }

        return errorMessage*/
    }

    /**
     * Overriding default onFailure method
     * this method will trigger onInternetFailed()
     *
     * @param call
     * @param t
     */
    override fun onFailure(call: Call<T>, t: Throwable) {
        if (t is NoConnectivityException) {
            //Add your code for displaying no network connection error
            onInternetFailed()
        } else {
            onFailed(t)
        }
    }

    /**
     * onSuccess will be called when response contains body
     *
     * @param response
     */
    abstract fun onSuccess(response: T?)

    /**
     * onUnauthorized will be called when token miss matches with server
     */
    abstract fun onUnauthorized(throwable: Throwable)

    /**
     * onFailed will be called when error generated from server
     *
     * @param throwable message value will be dependend on servers error message
     * if message is not available from server than default error message will
     * be displayed.
     */
    abstract fun onFailed(throwable: Throwable)

    /**
     * onInternetFailed() method will be called when
     * network connection is not available in device.
     */
    abstract fun onInternetFailed()

    /**
     * onEmptyResponse() method will be called when response from server is blank or
     * error code is 404 generated.
     *
     * @param message
     */
    abstract fun onEmptyResponse(message: String)

    companion object {
        private val TAG = "RemoteCallback"

        // Default error message
        private val DEFAULT_ERROR_MSG = "Sorry we are unable to reach server at this time."
    }

}
