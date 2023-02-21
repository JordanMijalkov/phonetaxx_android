package com.phonetaxx.ui.navigationdrawer.accountsetting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.phonetaxx.AppClass
import com.phonetaxx.R
import com.phonetaxx.extension.*
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseCallLogHelper
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.roomdb.DatabaseHelper
import com.phonetaxx.ui.BaseFragment
import com.phonetaxx.ui.navigationdrawer.SubscriptionActivity
import com.phonetaxx.ui.signin.SignInActivity
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.PreferenceHelper
import com.phonetaxx.utils.ReadCallLogHelper
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.fragment_account_setting.*
import kotlinx.android.synthetic.main.fragment_account_setting.tvEmail
import kotlinx.android.synthetic.main.navigation_drawer_menu.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AccountSettingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AccountSettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AccountSettingFragment : BaseFragment(), View.OnClickListener {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_setting, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {
        if (PreferenceHelper.getInstance()
                .getProfileData()!!.Subscription != null && !TextUtils.isEmpty(
                PreferenceHelper.getInstance()
                    .getProfileData()!!.Subscription
            )
        ) {
            if (PreferenceHelper.getInstance()
                    .getProfileData()!!.Subscription.equals(Const.MONTHLY_PLAN)
            ) {
                tvPlanName.text =
                    "Monthly Plan"
            } else if (PreferenceHelper.getInstance()
                    .getProfileData()!!.Subscription.equals(Const.ANNUAL_PLAN)
            ){
                tvPlanName.text =
                    "Annual Plan"
            }else{
                tvPlanName.text =
                    "Free plan"
            }


        } else {
            var callleft =
                (Const.CALL_LIMIT + 1) - PreferenceHelper.getInstance().callRead!!
            if (callleft > 1) {
                tvPlanPrice.text =
                    "" + callleft + " Calls Remaining"
            } else {
                if (callleft < 0) {
                    tvPlanPrice.text =
                        "" + 0 + " Call Remaining"
                } else {
                    tvPlanPrice.text =
                        "" + callleft + " Call Remaining"
                }

            }

        }



        llProfile.setOnClickListener(this)
        llCallDetection.setOnClickListener(this)
        llNotification.setOnClickListener(this)
        llPrivacy?.setOnClickListener(this)
        llMonthlyPhoneBill.setOnClickListener(this)
        llFrequentNumber.setOnClickListener(this)
        llGetUnlimitedCalls.setOnClickListener(this)
        llLogout.setOnClickListener(this)
        llDeleteAccount.setOnClickListener(this)
        tvEmail.setText(PreferenceHelper.getInstance().getProfileData()?.email)
        tvMonthlyAmount.setText(
            getString(R.string.currency_sign) + PreferenceHelper.getInstance()
                .getProfileData()?.mothlyBillAmount.toString()
        )

        if (PreferenceHelper.getInstance().getProfileData()!!.callDetection == 1) {
            swCallDetection.isChecked = true
        } else {
            swCallDetection.isChecked = false
        }

        swCallDetection.setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener {
            override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
                var callDetection = 0
                if (isChecked) {
                    callDetection = 1
                } else {
                    callDetection = 0
                }
                if (PreferenceHelper.getInstance()
                        .getProfileData()!!.callDetection != callDetection
                ) {
                    updateCallDetection(callDetection)
                }
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountSettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.llProfile -> {
//                showErrorMessage("llProfile")
            }
            R.id.llNotification -> {
                startActivity(activity?.let { NotificationActivity.getIntent(it) })
            }
            R.id.llPrivacy -> {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.terms_of_services_url))
                )
                startActivity(browserIntent)
            }
            R.id.llMonthlyPhoneBill -> {
                var monthlyPhoneBillDialogFragment =
                    MonthlyPhoneBillDialogFragment.newInstance("", "")
                monthlyPhoneBillDialogFragment.registerCallbackListener(object :
                    FragmentCallBackListener<String> {
                    override fun onSuccess(data: String) {
                        tvMonthlyAmount.setText(
                            getString(R.string.currency_sign) + PreferenceHelper.getInstance()
                                .getProfileData()?.mothlyBillAmount.toString()
                        )
                    }
                })
                monthlyPhoneBillDialogFragment.show(
                    requireFragmentManager(),
                    "monthlyPhoneBillDialogFragment"
                )

            }
            R.id.llFrequentNumber -> {
                startActivity(activity?.let { FrequentNumberActivity.getIntent(it) })
            }
            R.id.llGetUnlimitedCalls -> {
                startActivity(SubscriptionActivity.getIntent(requireActivity()))
            }
            R.id.llLogout -> {
                showActionDialog(
                    requireActivity(),
                    getString(R.string.logout_message),
                    "",
                    getString(R.string.yes),
                    getString(R.string.no),
                    object :
                        ActionDialogInterface {
                        override fun onPositiveButtonClick() {
                            redirectToLogin()
                        }

                        override fun onNagativeButtonClick() {

                        }

                    })
            }
            R.id.llDeleteAccount -> {
                showActionDialog(
                    requireActivity(),
                    getString(R.string.delete_account_message),
                    "",
                    getString(R.string.yes),
                    getString(R.string.no),
                    object :
                        ActionDialogInterface {
                        override fun onPositiveButtonClick() {
                            deleteAccountFromFirebase()
                        }

                        override fun onNagativeButtonClick() {

                        }

                    })


            }
        }
    }

    private fun updateCallDetection(callDetection: Int) {

        showProgressDialog()

        FireBaseUserHelper.getInstance().updateCallDeletection(callDetection,
            object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                    if (callDetection == 1) {
                        getCallDetail()
                    } else {
                        hideProgressDialog()
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }

            })


    }

    private fun getCallDetail() {
        ReadCallLogHelper.getInstance(requireActivity())
            .getCallDetailNew(PreferenceHelper.getInstance().lastSyncCallTimeStamp,
                object : ReadCallLogHelper.ReadCallLogListener {
                    override fun onCompleted(result: ArrayList<CallLogsDbModel>?) {
                        var list: ArrayList<CallLogsDbModel>? = ArrayList()
                        if (result?.size!! > 20 && PreferenceHelper.getInstance().lastSyncCallTimeStamp?.equals(
                                0
                            )!!
                        ) {
                            for (i in 0..20) {
                                list?.add(result?.get(i))
                            }
                        } else {
                            list = result
                        }
                        if (list!!.size > 0 && list.size < 500) {

                            showToast("Reading call logs")

                            Log.v(
                                "lastSyncCallTimeStamp",
                                ": " + PreferenceHelper.getInstance().lastSyncCallTimeStamp
                            )
                            PreferenceHelper.getInstance().lastSyncCallTimeStamp =
                                System.currentTimeMillis()
                            FireBaseCallLogHelper.getInstance()
                                .addCallLog(list, object : FirebaseDatabaseListener<String> {
                                    override fun onSuccess(data: String) {

                                        hideProgressDialog()
                                    }

                                    override fun onFail(
                                        errorMessage: String,
                                        exception: Exception?
                                    ) {
                                        hideProgressDialog()
                                    }

                                })
                        } else {
                            if (list.size > 500) {
                                showErrorMessage("Unable to update more then 500 call to firestore")
                            }
                            hideProgressDialog()
                        }

                    }

                    override fun onStart() {
//                showToast("start")
                    }

                    override fun onFail() {
//                showToast("fail")
                        hideProgressDialog()
                    }

                })
    }

    private fun redirectToLogin() {

        var appDatabase = DatabaseHelper.getInstance(AppClass.getInstance()).appDataBase
        appDatabase.getCallCategoryDao().deleteAll()

        PreferenceHelper.getInstance().clearPreference()
        startActivity(activity?.let { SignInActivity.getIntent(it) })

    }

    private fun deleteAccountFromFirebase() {
        showProgressDialog()

        FireBaseUserHelper.getInstance().deleteAccountFromFirebase(
            PreferenceHelper.getInstance().getProfileData()?.uuId!!,
            object : FirebaseDatabaseListener<String> {
                override fun onSuccess(data: String) {
                    hideProgressDialog()
                    PreferenceHelper.getInstance().deleteAccountClearPreference()
                    redirectToLogin()
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showErrorMessage(getString(R.string.not_able_to_delete_account))
                }

            })
    }

}
