package com.phonetaxx.extension

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.phonetaxx.R
import com.phonetaxx.utils.ProgressDialogUtils

fun Fragment.showToast(msg: String) {
    try {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {

    }
}

fun Fragment.showToast(@StringRes resId: Int) {
    Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.noInternetAlert() {
    showToast(R.string.no_internet_available)
}

/*---------- Progress Dialog ----------*/

fun Fragment.showProgressDialog() {
    showProgressDialog("Loading...")
}

fun Fragment.showProgressDialog(message: String) {
    ProgressDialogUtils.getInstance()
        .create(context!!)
        .withMessage(message)
        .cancelable(false)
        .show()
}

fun Fragment.hideProgressDialog() {
    ProgressDialogUtils.getInstance()
        .hide()
}

var dialog: Dialog? = null

fun Fragment.showActionDialog(
    context: Context
    , message: String
    , subtext: String
    , positiveButtonName: String
    , nagativeButtonName: String
    , acctionListener: ActionDialogInterface
) {

    dialog = Dialog(context)
    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog!!.setCancelable(false)
    dialog!!.setContentView(R.layout.conformation_dialog)
    var tvMessage = dialog!!.findViewById(R.id.tvNoData) as TextView
    var tvSubText = dialog!!.findViewById(R.id.tvSubText) as TextView

    tvMessage.text = message

    if (!TextUtils.isEmpty(subtext)) {
        tvSubText.text = subtext
        tvSubText.visibility = View.VISIBLE
    } else {
        tvSubText.visibility = View.GONE
    }

    var tvNo = dialog!!.findViewById(R.id.tvNo) as TextView
    if (nagativeButtonName.isEmpty()) {
        tvNo.visibility = View.GONE
    }
    tvNo.setText(nagativeButtonName)
    var tvYes = dialog!!.findViewById(R.id.tvYes) as TextView
    if (positiveButtonName.isEmpty()) {
        tvYes.visibility = View.GONE
    }
    tvYes.setText(positiveButtonName)

    tvYes.setOnClickListener {
        dialog!!.dismiss()
        acctionListener.onPositiveButtonClick()
    }
    tvNo.setOnClickListener {
        dialog!!.dismiss()
        acctionListener.onNagativeButtonClick()
    }

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog!!.getWindow()!!.getAttributes())
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    dialog!!.getWindow()!!.setAttributes(lp);
    dialog!!.show()

}

interface DialogInterface {
    fun onPositiveButtonClick()
    fun onNagativeButtonClick()
}


