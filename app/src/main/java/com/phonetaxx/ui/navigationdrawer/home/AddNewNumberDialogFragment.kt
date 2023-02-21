package com.phonetaxx.ui.navigationdrawer.home

import RuntimePermissionHelper
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseCallCategoryHelper
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.roomdb.DatabaseHelper
import com.phonetaxx.ui.NaicsCodeView
import com.phonetaxx.ui.contactui.ContactActivity
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_add_number_dialog.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddNumberDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddNumberDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddNumberDialogFragment : DialogFragment(), View.OnClickListener,
    RuntimePermissionHelper.PermissionCallbacks {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var userImage = ""
    private var REQUEST_CODE = 103
    var PERMISSION_READ_CONTACT_CODE = 104

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        ivClose.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        ivContact.setOnClickListener(this)
        cardBusiness?.setOnClickListener(this)
        cardPersonal?.setOnClickListener(this)
        etrSearch?.setOnClickListener(this)

        setInitSelection()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_number_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.getWindow()?.setBackgroundDrawableResource(R.color.dialog_transparent_bg);

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNumberDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivClose -> {
                dialog?.dismiss()
            }
            R.id.btnSave -> {
                if (isValid()) {
                    btnSave.isClickable = false
                    btnSave.isEnabled = false
                    checkNumberForCategory()

                }
            }
            R.id.cardUncategorized -> {
                setInitSelection()
            }
            R.id.cardPersonal -> {
                businessSelected(false)
                personalSelected(true)


            }
            R.id.cardBusiness -> {
                businessSelected(true)
                personalSelected(false)
            }
            R.id.ivContact -> {
                requestPermissionForCall()

            }
            R.id.etrSearch -> {
                val intent = Intent(requireContext(), NaicsCodeView::class.java)
                startActivity(intent)
            }

        }
    }

    private fun requestPermissionForCall() {
        Log.v(TAG, "requestPermissionForCall: ");
        val permissions = arrayOf<String?>(
            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val runtimePermission = RuntimePermissionHelper.newInstance(
            permissions,
            PERMISSION_READ_CONTACT_CODE,
            "Application need contact permission."
        )
        runtimePermission.setAppPermissionListener(this)
        runtimePermission.show(childFragmentManager)
    }


    val TAG: String = "AddNumberDialogFragment"
    private fun checkNumberForCategory() {

        var appDatabase = DatabaseHelper.getInstance(requireContext()).appDataBase
        var phoneNumber = etPhoneNumber.text.toString()


        val callCategoryDbModelList: List<CallCategoryDbModel?> =
            appDatabase.getCallCategoryDao()
                .getCallCategoryData(phoneNumber.substring(phoneNumber.length - 9))

        Log.v(TAG, "checkNumberForCategory : " + callCategoryDbModelList.size)

        if (callCategoryDbModelList.size == 0) {
            saveCallCategoryToFirebase()
        } else {
            var category = ""
            if (callCategoryDbModelList.get(0)!!.callCategory.equals(Const.business)) {
                category = getString(R.string.business)
            } else {
                category = getString(R.string.personal)
            }
            showToast("Number is already categorized as " + category)
            btnSave.isClickable = true
            btnSave.isEnabled = true
        }

    }

    private fun saveCallCategoryToFirebase() {
        showProgressDialog()
        var callCategoryDbModel = CallCategoryDbModel()
        callCategoryDbModel.uuId =
            FireBaseCallCategoryHelper.getInstance().getUniqueUUID(etPhoneNumber.text.toString())
        callCategoryDbModel.userUuid =
            PreferenceHelper.getInstance().getProfileData()?.uuId.toString()
        callCategoryDbModel.phoneNumber = etPhoneNumber.text.toString()
        callCategoryDbModel.numberType = 1
        callCategoryDbModel.phoneName = etName.text.toString()
        callCategoryDbModel.callCategory = selectedCategory.toString()
        callCategoryDbModel.phoneImage = userImage
        callCategoryDbModel.businessName = etrBusinessName.text.toString().trim()
        callCategoryDbModel.naicsCode = etrNaicsCode.text.toString().trim()
        callCategoryDbModel.location = etrLocation.text.toString().trim()

        FireBaseCallCategoryHelper.getInstance()
            .addCallCategory(callCategoryDbModel, object : FirebaseDatabaseListener<String> {
                override fun onSuccess(data: String) {
                    hideProgressDialog()
                    insertIntoLocalDb(callCategoryDbModel)
//                    getSameMobileNumberFromFirebase(callCategoryDbModel)

                    if (listener != null) {

                        listener.onSuccess("")
                    }
                    btnSave.isClickable = true
                    btnSave.isEnabled = true
                    dialog?.dismiss()

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showToast(errorMessage)
                    btnSave.isClickable = true
                    btnSave.isEnabled = true
                }

            })

    }

    private fun insertIntoLocalDb(callCategoryDbModel: CallCategoryDbModel) {
        var appDatabase = DatabaseHelper.getInstance(requireContext()).appDataBase
        appDatabase.getCallCategoryDao().insertSingleData(callCategoryDbModel)
    }

    private fun setInitSelection() {
        businessSelected(false)
        personalSelected(true)

    }

    var selectedCategory: String? = "0"
    fun businessSelected(b: Boolean) {
        if (b) {
            selectedCategory = Const.business
        }

        ivBusiness?.isSelected = b;
        tvBusinessText?.isSelected = b;
        cardBusiness?.isSelected = b;

    }

    fun personalSelected(b: Boolean) {
        if (b) {
            selectedCategory = Const.personal
        }

        ivPersonal?.isSelected = b;
        tvPersonalText?.isSelected = b;
        cardPersonal?.isSelected = b

    }

    fun isValid(): Boolean {
        if (etPhoneNumber.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_phone_number))
            return false
        }
        if (etPhoneNumber.text.toString().length < 9) {
            showToast(getString(R.string.err_phone_number_invalid))
            return false
        }
        if (etName.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_name))
            return false
        }
        if (etrBusinessName.text.isNullOrEmpty()) {
            showToast("Please enter business name")
            return false
        }
//        if (etrNaicsCode.text.isNullOrEmpty()) {
//            showToast("Please NAICS code")
//            return false
//        }
//        if (etrLocation.text.isNullOrEmpty()) {
//            showToast("Please enter location")
//            return false
//        }
        return true
    }

    lateinit var listener: FragmentCallBackListener<String>

    public fun registerCallbackListener(fragmentCallBackListener: FragmentCallBackListener<String>) {
        listener = fragmentCallBackListener
    }

    override fun onPermissionAllow(permissionCode: Int) {
        var intent = Intent(context, ContactActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onPermissionDeny(permissionCode: Int) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    etPhoneNumber.setText(data.getStringExtra(Const.PHONE).toString())
                    etName.setText(data.getStringExtra(Const.NAME).toString())
                    userImage = data.getStringExtra(Const.IMAGE).toString()
                }
            }
        }
    }

}
