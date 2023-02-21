package com.phonetaxx.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.phonetaxx.R

class ProgressDialogUtils() {

    private var progressDialog: Dialog? = null

    companion object {
        private var instance: ProgressDialogUtils? = null

        fun getInstance(): ProgressDialogUtils {
            if (instance == null) {
                instance = ProgressDialogUtils()
            }
            return instance as ProgressDialogUtils
        }
    }

    fun create(context: Context): ProgressDialogUtils {
//        progressDialog = ProgressDialog(context)

        progressDialog = Dialog(context)
        progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog!!.setContentView(R.layout.progress_dialog)
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return this
    }

    fun withTitle(title: String): ProgressDialogUtils {
        if (progressDialog != null) {
//            progressDialog?.setTitle(title)
        }
        return this
    }

    fun withMessage(message: String): ProgressDialogUtils {
        if (progressDialog != null) {
//            progressDialog?.setMessage(message)
        }
        return this
    }

    fun cancelable(isCancelable: Boolean): ProgressDialogUtils {
        if (progressDialog != null) {
            progressDialog?.setCancelable(isCancelable)
        }
        return this
    }

    fun show() {
        if (progressDialog != null && !progressDialog?.isShowing!!) {
            progressDialog?.show()
        }
    }

    fun hide() {
        if (progressDialog != null && progressDialog?.isShowing!!) {
            progressDialog?.dismiss()
        }
    }
}