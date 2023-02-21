package com.sixmmedicine.webapi

import com.phonetaxx.AppClass
import com.phonetaxx.BuildConfig
import com.phonetaxx.utils.PreferenceHelper
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WebAPIServiceFactory {

    fun makeServiceFactory(): WebAPIService {
        return makeServiceFactory(makeOkHttpClient())
    }

    private fun makeServiceFactory(okHttpClient: OkHttpClient): WebAPIService {

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(WebAPIService::class.java)
    }

    private fun makeOkHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient().newBuilder()
        httpClientBuilder.connectTimeout(HTTP_CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        httpClientBuilder.writeTimeout(HTTP_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)


        httpClientBuilder.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {

                val original = chain.request()
                // Customize the request
                var requestBuilder: Request.Builder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept-app-version", BuildConfig.VERSION_NAME)
                    .header("Accept-type", "1")
                    .method(original.method, original.body)


                val request = requestBuilder.build()

                var response = chain.proceed(request)


                return response

            }
        })

        httpClientBuilder.addInterceptor(loggingInterceptor())
     //   httpClientBuilder.addInterceptor(ChuckInterceptor(AppClass.getInstance()))
        return httpClientBuilder.build()
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.apply {
            logging.level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    companion object {
        private const val HTTP_READ_TIMEOUT = 20000
        private const val HTTP_WRITE_TIMEOUT = 20000
        private const val HTTP_CONNECT_TIMEOUT = 60000

        private var INSTANCE: WebAPIServiceFactory? = null

        fun newInstance(): WebAPIServiceFactory {
            if (INSTANCE == null) {
                INSTANCE = WebAPIServiceFactory()
            }
            return INSTANCE as WebAPIServiceFactory
        }
    }

}