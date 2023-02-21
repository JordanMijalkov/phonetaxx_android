package com.phonetaxx.ui.navigationdrawer.home

import android.app.DatePickerDialog
import android.os.Bundle
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
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.utils.DateTimeDialog
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_screen_time_dialog.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TOTAL_SCREEN_TIME = "param1"
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
class ScreenTimeDialog : DialogFragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var totalScreenTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            totalScreenTime = it.getLong(TOTAL_SCREEN_TIME)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        ivClose.setOnClickListener(this)
        btnSave.setOnClickListener(this)

        cardBusiness?.setOnClickListener(this)
        cardPersonal?.setOnClickListener(this)

        etDate?.setOnClickListener(this)
        setInitSelection()

        etScreenTime.setText(PreferenceHelper.getInstance().getProfileData()!!.businessScreenTime.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_time_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.getWindow()?.setBackgroundDrawableResource(R.color.dialog_transparent_bg);

    }


    companion object {
        @JvmStatic
        fun newInstance(totalScreenTime: Long) =
            ScreenTimeDialog().apply {
                arguments = Bundle().apply {
                    putLong(TOTAL_SCREEN_TIME, totalScreenTime)
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
                    updateScreenTime()
                }
            }
            R.id.etDate -> {
                openDateDialog()
            }
            R.id.cardPersonal -> {
                setInitSelection()

            }
            R.id.cardBusiness -> {
                businessSelected(true)
                personalSelected(false)
            }
        }
    }

    private fun updateScreenTime() {


        showProgressDialog()

        FireBaseUserHelper.getInstance().updateScreenTime(etScreenTime.text.toString().toInt(),
            object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()
                    if (listener != null) {
                        listener.onSuccess("")
                    }
                    dialog?.dismiss()
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }

            })

    }

    fun isValid(): Boolean {
        /*     if (etDate.text.isNullOrEmpty()) {
                 showToast(getString(R.string.err_date))
                 return false
             }
        */     if (etScreenTime.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_screen_time))
            return false
        }
        if (etScreenTime.text.toString().toLong() > totalScreenTime) {
            showToast(getString(R.string.err_invalid_screen_time))
            return false
        }
        return true
    }

    private fun setInitSelection() {
        businessSelected(false)
        personalSelected(true)

    }

    fun businessSelected(b: Boolean) {
        ivBusiness?.isSelected = b;
        tvBusinessText?.isSelected = b;
        cardBusiness?.isSelected = b;

    }

    fun personalSelected(b: Boolean) {
        ivPersonal?.isSelected = b;
        tvPersonalText?.isSelected = b;
        cardPersonal?.isSelected = b

    }

    private var datePickerDialog: DatePickerDialog? = null
    private fun openDateDialog() {
        if (datePickerDialog == null) {
            datePickerDialog =
                DateTimeDialog.showDatePickerDialog(context, object : DateTimeDialog.OnAppDateChangeListener {
                    override fun setAppOnDateChangeListener(formattedDate: String, serverDateFormat: String) {
                        etDate.setText(formattedDate)
                    }
                })
            //            datePickerDialog.getDatePicker().setMinDate(DateTimeUtil.getInstance().getCurrentTimeStamp());
        }

        datePickerDialog?.show()
    }

    lateinit var listener: FragmentCallBackListener<String>

    public fun registerCallbackListener(fragmentCallBackListener: FragmentCallBackListener<String>) {
        listener = fragmentCallBackListener
    }

}
