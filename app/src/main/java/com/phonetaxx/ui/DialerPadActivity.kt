package com.phonetaxx.ui

import RuntimePermissionHelper
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.phonetaxx.R
import kotlinx.android.synthetic.main.activity_dialer_pad.*


class DialerPadActivity : AppCompatActivity(), View.OnClickListener,
    RuntimePermissionHelper.PermissionCallbacks {

    private var edtPhoneNo: AppCompatTextView? = null
    private var lableInfo: AppCompatTextView? = null
    private val PERMISSION_CALL_DIAL_CODE = 122

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, DialerPadActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer_pad)

        val permissions = arrayOf<String?>(
            Manifest.permission.CALL_PHONE
        )
        val runtimePermission = RuntimePermissionHelper.newInstance(
            permissions,
            PERMISSION_CALL_DIAL_CODE,
            "Application need call log permission."
        )
        runtimePermission.setAppPermissionListener(this)
        runtimePermission.show(supportFragmentManager)

        edtPhoneNo = findViewById(R.id.tvPhoneNumber)
        lableInfo = findViewById(R.id.lblInfo)
        btnAsterisk.setOnClickListener(this)
        btnHash.setOnClickListener(this)
        btnZero.setOnClickListener(this)
        btnOne.setOnClickListener(this)
        btnTwo.setOnClickListener(this)
        btnThree.setOnClickListener(this)
        btnFour.setOnClickListener(this)
        btnFive.setOnClickListener(this)
        btnSix.setOnClickListener(this)
        btnSeven.setOnClickListener(this)
        btnEight.setOnClickListener(this)
        btnNine.setOnClickListener(this)
        ivDelete.setOnClickListener(this)
        btnMakeCall.setOnClickListener(this)
        btnClearAll.setOnClickListener(this)
    }

    override fun onPermissionAllow(permissionCode: Int) {
        when (permissionCode) {
            PERMISSION_CALL_DIAL_CODE -> {

            }
        }
    }

    override fun onPermissionDeny(permissionCode: Int) {
    }

    override fun onClick(v: View?) {
        var phoneNo = edtPhoneNo!!.text.toString()
        when (v!!.id) {
            R.id.ivBackToolbar -> {
                onBackPressed()
            }
            R.id.btnAsterisk -> {
                lableInfo!!.text = ""
                phoneNo += "*"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnHash -> {
                lableInfo!!.text = ""
                phoneNo += "#"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnZero -> {
                lableInfo!!.text = ""
                phoneNo += "0"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnOne -> {
                lableInfo!!.text = ""
                phoneNo += "1"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnTwo -> {
                lableInfo!!.text = ""
                phoneNo += "2"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnThree -> {
                lableInfo!!.text = ""
                phoneNo += "3"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnFour -> {
                lableInfo!!.text = ""
                phoneNo += "4"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnFive -> {
                lableInfo!!.text = ""
                phoneNo += "5"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnSix -> {
                lableInfo!!.text = ""
                phoneNo += "6"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnSeven -> {
                lableInfo!!.text = ""
                phoneNo += "7"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnEight -> {
                lableInfo!!.text = ""
                phoneNo += "8"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnNine -> {
                lableInfo!!.text = ""
                phoneNo += "9"
                edtPhoneNo!!.text = phoneNo
            }
            R.id.ivDelete -> {
                lableInfo!!.text = ""
                if (phoneNo != null && phoneNo.length > 0) {
                    phoneNo = phoneNo.substring(0, phoneNo.length - 1)
                }
                edtPhoneNo!!.text = phoneNo
            }
            R.id.btnClearAll -> {
                lableInfo!!.text = ""
                edtPhoneNo!!.text = ""
            }
            R.id.btnMakeCall -> {
                if (phoneNo.trim().equals("")) {
                    lableInfo!!.text = getString(R.string.please_enter_number)
                } else {
                    if (phoneNo.subSequence(phoneNo.length - 1, phoneNo.length).equals("#")) {
                        phoneNo = phoneNo.substring(0, phoneNo.length - 1)
                        val callInfo = "tel:" + phoneNo + Uri.encode("#")
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse(callInfo)
                        startActivity(callIntent)
                    } else {
                        val callInfo = "tel:$phoneNo"
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse(callInfo)
                        startActivity(callIntent)
                    }
                }
            }
        }
    }
}