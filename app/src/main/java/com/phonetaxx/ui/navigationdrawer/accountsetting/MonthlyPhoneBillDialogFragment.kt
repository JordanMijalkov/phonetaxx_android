package com.phonetaxx.ui.navigationdrawer.accountsetting

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
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_add_number_dialog.btnSave
import kotlinx.android.synthetic.main.fragment_add_number_dialog.ivClose
import kotlinx.android.synthetic.main.fragment_monthly_phone_bill_dialog.*


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
class MonthlyPhoneBillDialogFragment : DialogFragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        etAmount.setText(
            PreferenceHelper.getInstance().getProfileData()!!.mothlyBillAmount.toString()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly_phone_bill_dialog, container, false)
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
            MonthlyPhoneBillDialogFragment().apply {
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
                    updateAmountToFirebase()
                }
            }
        }
    }

    private fun updateAmountToFirebase() {

        showProgressDialog()

        FireBaseUserHelper.getInstance()
            .updateMonthlyBillAmount(etAmount.text.toString().toDouble(),
                object : FirebaseDatabaseListener<UsersDbModel> {
                    override fun onSuccess(data: UsersDbModel) {
                        hideProgressDialog()
                        listener.onSuccess("")
                        dialog!!.dismiss()
                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        hideProgressDialog()
                    }

                })
    }

    fun isValid(): Boolean {
        if (etAmount.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_amount))
            return false
        }
        if (etAmount.text.toString().toDouble() == 0.00) {
            showToast(getString(R.string.err_valid_amount))
            return false
        }
        return true
    }

    lateinit var listener: FragmentCallBackListener<String>

    public fun registerCallbackListener(fragmentCallBackListener: FragmentCallBackListener<String>) {
        listener = fragmentCallBackListener
    }
}
