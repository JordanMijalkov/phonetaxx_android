package com.phonetaxx.ui.navigationdrawer.uncategorized

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
import com.phonetaxx.firebase.helper.FireBaseCallLogHelper
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.roomdb.DatabaseHelper
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.DateTimeHelper
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_define_category_number_dialog.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddNumberDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddNumberDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DefineCategoryDialogFragment : DialogFragment(), View.OnClickListener {


    private var CallLogsDbModel: String? = null
    var callData: CallLogsDbModel? = null
    var selectedCategory: String? = "0"
    val TAG: String = "DefineCategory"

    companion object {
        @JvmStatic
        fun newInstance(item: CallLogsDbModel?) =
            DefineCategoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CallLogsDbModel, item)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            callData = it.getParcelable(CallLogsDbModel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setData()
        Log.v(TAG, ": ");
    }

    private fun setData() {

        tvName.setText(callData?.name)
        Log.v("viewHolder", ": " + callData?.callDurationInSecond);
        tvCallDuration.setText(DateTimeHelper.getInstance().getTimeFormateFromSeconds(callData?.callDurationInSecond))
        tvDateTime.setText(DateTimeHelper.getInstance().getWeekDayAndTimeFromTimeStamp(callData?.callDateTimeUTC!!))
        tvPhoneNumber.setText(callData?.phoneNumber)

    }

    private fun initView(view: View) {

        ivClose.setOnClickListener(this)
        btnSave.setOnClickListener(this)

        cardBusiness?.setOnClickListener(this)
        cardPersonal?.setOnClickListener(this)
        cardUncategorized?.setOnClickListener(this)
        setInitSelection()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_define_category_number_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.getWindow()?.setBackgroundDrawableResource(R.color.dialog_transparent_bg);

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivClose -> {
                dialog?.dismiss()
            }
            R.id.btnSave -> {
                if (!selectedCategory.toString().equals(callData?.callCategory)) {
                    saveCallCategoryToFirebase()
                } else {
                    showToast(getString(R.string.already_defined_in_this_category))
                }
            }
            R.id.cardUncategorized -> {
                setInitSelection()
            }
            R.id.cardPersonal -> {
                businessSelected(false)
                personalSelected(true)
                uncategorizedSelected(false)

            }
            R.id.cardBusiness -> {
                businessSelected(true)
                personalSelected(false)
                uncategorizedSelected(false)
            }
        }
    }

    private fun saveCallCategoryToFirebase() {
        showProgressDialog()
        var callCategoryDbModel = CallCategoryDbModel()
        callCategoryDbModel.uuId = FireBaseCallCategoryHelper.getInstance().getUniqueUUID(callData!!.phoneNumber)
        callCategoryDbModel.userUuid = PreferenceHelper.getInstance().getProfileData()?.uuId.toString()
        callCategoryDbModel.phoneNumber = callData!!.phoneNumber
        callCategoryDbModel.numberType = 0
        callCategoryDbModel.callCategory = selectedCategory.toString()

        FireBaseCallCategoryHelper.getInstance()
            .addCallCategory(callCategoryDbModel, object : FirebaseDatabaseListener<String> {
                override fun onSuccess(data: String) {
                    insertIntoLocalDb(callCategoryDbModel)
                    getSameMobileNumberFromFirebase(callCategoryDbModel)
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showToast(errorMessage)
                }

            })

    }

    private fun insertIntoLocalDb(callCategoryDbModel: CallCategoryDbModel) {
        var appDatabase = DatabaseHelper.getInstance(context!!).appDataBase
        appDatabase.getCallCategoryDao().insertSingleData(callCategoryDbModel)
    }

    private fun getSameMobileNumberFromFirebase(callCategoryDbModel: CallCategoryDbModel) {
        FireBaseCallLogHelper.getInstance().getCallLogByPhoneNumber(callCategoryDbModel.phoneNumber,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {
                    Log.v(TAG, "getSameMobileNumberFromFirebase: ");
                    updateAllPhoneNumberCategory(data, callCategoryDbModel)
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showToast(errorMessage)
                }
            })
    }

    private fun updateAllPhoneNumberCategory(
        data: ArrayList<CallLogsDbModel?>,
        callCategoryDbModel: CallCategoryDbModel
    ) {
        FireBaseCallLogHelper.getInstance().updateCallLogWithCategory(
            data, callCategoryDbModel.callCategory,
            object : FirebaseDatabaseListener<String> {
                override fun onSuccess(data: String) {
                    hideProgressDialog()
                    dialog?.dismiss()
                    listener.onSuccess("")
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showToast(errorMessage)
                }
            })
    }

    private fun setInitSelection() {

        businessSelected(false)
        personalSelected(false)
        uncategorizedSelected(true)

    }

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

    fun uncategorizedSelected(b: Boolean) {
        if (b) {
            selectedCategory = Const.uncategorized
        }
        tvUncategorizedText?.isSelected = b
        cardUncategorized?.isSelected = b
    }

    lateinit var listener: FragmentCallBackListener<String>

    public fun registerCallbackListener(fragmentCallBackListener: FragmentCallBackListener<String>) {
        listener = fragmentCallBackListener
    }
}
