package com.phonetaxx.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog

class NaicsCodeView : AppCompatActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_naics_code_view)

        webView = findViewById(R.id.cp_web_view)

        showProgressDialog()

        val webSettings = webView?.settings
        webSettings?.domStorageEnabled = true
        webSettings?.javaScriptEnabled = true
        webView?.settings?.allowFileAccess = true

        if (Build.VERSION.SDK_INT >= 21) {
            webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            webView?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webView?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE

        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                hideProgressDialog()
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                hideProgressDialog()
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                hideProgressDialog()
            }
        }

        webView?.loadUrl("https://www.census.gov/naics/")

    }

}