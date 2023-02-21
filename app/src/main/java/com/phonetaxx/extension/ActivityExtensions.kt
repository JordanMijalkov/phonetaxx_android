package com.phonetaxx.extension

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.phonetaxx.R
import com.phonetaxx.utils.ProgressDialogUtils


/*---------- Toast ----------*/

fun AppCompatActivity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.noInternetAlert() {
    showToast(R.string.no_internet_available)
}

fun AppCompatActivity.showSnackBar(message: String) {
    val snackbar = Snackbar.make(
        findViewById<View>(android.R.id.content),
        message, Snackbar.LENGTH_LONG
    )
    val sbView = snackbar.getView()
    val textView = sbView
        .findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))


    val lp = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    } else {
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    lp.gravity = Gravity.TOP

    sbView.setLayoutParams(lp)
    snackbar.addCallback(object : Snackbar.Callback() {
        override fun onShown(snackbar: Snackbar?) {
            super.onShown(snackbar)
            snackbar!!.view.visibility = View.VISIBLE
            Handler().postDelayed({
                snackbar.view.visibility = View.INVISIBLE
            }, 1000)
        }
    })
    if (message.isNotBlank()) {
        snackbar.show()
    }
}

/*---------- Progress Dialog ----------*/

fun AppCompatActivity.showProgressDialog() {
    showProgressDialog("Loading...")
}

fun AppCompatActivity.showProgressDialog(message: String) {
    ProgressDialogUtils.getInstance()
        .create(this)
        .withMessage(message)
        .cancelable(false)
        .show()
}

fun AppCompatActivity.hideProgressDialog() {
    ProgressDialogUtils.getInstance()
        .hide()
}

var actionDialog: Dialog? = null
fun AppCompatActivity.showActionDialog(
    context: Context
    , message: String
    , subtext: String
    , positiveButtonName: String
    , nagativeButtonName: String
    , acctionListener: ActionDialogInterface
) {

    actionDialog = Dialog(context)
    actionDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
    actionDialog!!.setCancelable(false)
    actionDialog!!.setContentView(R.layout.conformation_dialog)
    var tvMessage = actionDialog!!.findViewById(R.id.tvNoData) as TextView
    var tvSubText = actionDialog!!.findViewById(R.id.tvSubText) as TextView

    tvMessage.text = message

    if (!TextUtils.isEmpty(subtext)) {
        tvSubText.text = subtext
        tvSubText.visibility = View.VISIBLE
    } else {
        tvSubText.visibility = View.GONE
    }

    var tvNo = actionDialog!!.findViewById(R.id.tvNo) as TextView
    if (nagativeButtonName.isEmpty()) {
        tvNo.visibility = View.GONE
    }
    tvNo.setText(nagativeButtonName)
    var tvYes = actionDialog!!.findViewById(R.id.tvYes) as TextView
    if (positiveButtonName.isEmpty()) {
        tvYes.visibility = View.GONE
    }
    tvYes.setText(positiveButtonName)

    tvYes.setOnClickListener {
        acctionListener.onPositiveButtonClick()
    }
    tvNo.setOnClickListener { acctionListener.onNagativeButtonClick() }

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(actionDialog!!.getWindow()!!.getAttributes())
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    actionDialog!!.getWindow()!!.setAttributes(lp);
    actionDialog!!.show()

}

interface ActionDialogInterface {
    fun onPositiveButtonClick()
    fun onNagativeButtonClick()
}




