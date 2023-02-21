package com.phonetaxx.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.bumptech.glide.Glide
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseCallCategoryHelper
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.PreferenceHelper
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_edit_frequent_contact.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_add_number_dialog.*

class EditFrequentContact : AppCompatActivity(), View.OnClickListener {

    private var number: EditText? = null
    private var name: EditText? = null
    private var businessName: EditText? = null
    private var naicsCode: EditText? = null
    private var location: EditText? = null
    private var malout: EditText? = null
    private var personal: CardView? = null
    private var business: CardView? = null
    private var back: ImageView? = null
    private var save: TextView? = null
    private var personalImage: ImageView? = null
    private var personalText: TextView? = null
    private var businessImage: ImageView? = null
    private var businessText: TextView? = null
    private var isPersonal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_frequent_contact)

        inItView()
        setOnClickListner()

    }

    private fun inItView() {
        number = findViewById(R.id.efc_number)
        name = findViewById(R.id.efc_name)
        businessName = findViewById(R.id.efc_business_name)
        naicsCode = findViewById(R.id.efcNaicsCode)
        location = findViewById(R.id.efcLocation)
        personal = findViewById(R.id.efc_cardPersonal)
        personalImage = findViewById(R.id.efc_personal_image)
        personalText = findViewById(R.id.efc_personal_text)
        business = findViewById(R.id.efc_cardBusiness)
        businessImage = findViewById(R.id.efc_business_image)
        businessText = findViewById(R.id.efc_business_text)
        back = findViewById(R.id.efc_back)
        save = findViewById(R.id.efc_save)

        number?.setText(intent.getStringExtra("phone_number"))
        name?.setText(intent.getStringExtra("phone_name"))
        businessName?.setText(intent.getStringExtra("business_name"))
        naicsCode?.setText(intent.getStringExtra("naics_code"))
        location?.setText(intent.getStringExtra("location"))

        if (intent.getStringExtra("call_category").equals(Const.personal)) {
            setPersonalCard()
        } else {
            setBusinessCard()
        }

    }

    private fun setOnClickListner() {
        back?.setOnClickListener(this)
        save?.setOnClickListener(this)
        business?.setOnClickListener(this)
        personal?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.efc_back -> {
                super.onBackPressed()
            }
            R.id.efc_cardBusiness -> {
                setBusinessCard()
            }
            R.id.efc_cardPersonal -> {
                setPersonalCard()
            }
            R.id.efc_save -> {
                if (valid())
                    updateOnFirebase()
            }
        }
    }

    private fun valid(): Boolean {
        if (efc_number.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_phone_number))
            return false
        }
        if (efc_number.text.toString().length < 9) {
            showToast(getString(R.string.err_phone_number_invalid))
            return false
        }
        if (efc_name.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_name))
            return false
        }
        if (efc_business_name.text.isNullOrEmpty()) {
            showToast("Please enter business name")
            return false
        }
//        if (efcNaicsCode.text.isNullOrEmpty()) {
//            showToast("Please NAICS code")
//            return false
//        }
//        if (efcLocation.text.isNullOrEmpty()) {
//            showToast("Please enter location")
//            return false
//        }
        return true
    }

    private fun updateOnFirebase() {

        showProgressDialog()
        var callCategoryDbModel = CallCategoryDbModel()
        callCategoryDbModel.uuId = intent.getStringExtra("uuid")!!
        callCategoryDbModel.userUuid = intent.getStringExtra("user_uuid")!!

        callCategoryDbModel.phoneNumber = number?.text.toString()
        callCategoryDbModel.numberType = 1
        callCategoryDbModel.phoneName = name?.text.toString()

        callCategoryDbModel.businessName = businessName?.text.toString()
        callCategoryDbModel.naicsCode = naicsCode?.text.toString()
        callCategoryDbModel.location = location?.text.toString()

        if (isPersonal) {
            callCategoryDbModel.callCategory = Const.personal
        } else {
            callCategoryDbModel.callCategory = Const.business
        }
        callCategoryDbModel.phoneImage = intent.getStringExtra("phone_image")!!

        FireBaseCallCategoryHelper.getInstance().updateNumber(
            callCategoryDbModel,
            object : FirebaseDatabaseListener<CallCategoryDbModel> {
                override fun onSuccess(data: CallCategoryDbModel) {
                    showToast("Update successfully")
                    hideProgressDialog()
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    showToast("Something wrong, please try again")
                    hideProgressDialog()
                }
            })

    }

    private fun setPersonalCard() {
        personal?.setCardBackgroundColor(this@EditFrequentContact.resources.getColor(R.color.colorPrimary))
        Glide.with(this@EditFrequentContact).load(R.drawable.ic_persnal_selected)
            .into(personalImage!!)
        personalText?.setTextColor(this@EditFrequentContact.resources.getColor(R.color.colorWhite))

        business?.setCardBackgroundColor(this@EditFrequentContact.resources.getColor(R.color.add_number_category_selector))
        Glide.with(this@EditFrequentContact).load(R.drawable.phone_uses_bussiness_image_selector)
            .into(businessImage!!)
        businessText?.setTextColor(this@EditFrequentContact.resources.getColor(R.color.colorPrimary))

        isPersonal = true
    }

    private fun setBusinessCard() {
        business?.setCardBackgroundColor(this@EditFrequentContact.resources.getColor(R.color.colorPrimary))
        Glide.with(this@EditFrequentContact).load(R.drawable.ic_business_selected)
            .into(businessImage!!)
        businessText?.setTextColor(this@EditFrequentContact.resources.getColor(R.color.colorWhite))

        personal?.setCardBackgroundColor(this@EditFrequentContact.resources.getColor(R.color.add_number_category_selector))
        Glide.with(this@EditFrequentContact).load(R.drawable.ic_persnal).into(personalImage!!)
        personalText?.setTextColor(this@EditFrequentContact.resources.getColor(R.color.colorPrimary))

        isPersonal = false
    }


}