package com.phonetaxx.ui.navigationdrawer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseCallLogHelper
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.listener.DrawerMenuChangeListener
import com.phonetaxx.ui.BaseFragment
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.CustomDatePicker
import com.phonetaxx.utils.DateTimeHelper
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_monthly_summary.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MonthlySummaryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MonthlySummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MonthlySummaryFragment : BaseFragment(), View.OnClickListener {

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonthlySummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var selectedMonth = 0
    var selectedYear = 0
    var personalCall: Int = 0
    var businessCall: Int = 0
    var uncategorizedCall: Int = 0
    var totalPhoneCalls: Int = 0
    var businessCallTimeInSecond: Int = 0
    var totalCallTimeInSecond: Int = 0


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_monthly_summary, container, false)
        return view
    }

    var calender = Calendar.getInstance()
    private fun initView() {

        tvAmount.setText(getString(R.string.currency_sign) + PreferenceHelper.getInstance().getProfileData()!!.mothlyBillAmount.toString())
        tvAmountMonth.setText(getString(R.string.currency_sign) + PreferenceHelper.getInstance().getProfileData()!!.mothlyBillAmount.toString())
        tvAmountPotential.setText(
            getString(R.string.currency_sign) + PreferenceHelper.getInstance().getProfileData()!!.mothlyBillAmount.toString() + " " + getString(
                R.string.potential
            )
        )
        btnPhoneUses.setOnClickListener(this)
        btnViewAllCall.setOnClickListener(this)
        rlCalender.setOnClickListener(this)


        if (selectedMonth == 0) {
            selectedMonth = calender.get(Calendar.MONTH)
        }
        if (selectedYear == 0) {
            selectedYear = calender.get(Calendar.YEAR)
        }

        getMonthDataFromFirebase()
    }

    private fun openMonthYearPicker() {
        CustomDatePicker.getInstance()
            .openMonthYearPicker(context, selectedYear, selectedMonth, object : CustomDatePicker.MonthPickerListener {
                override fun onMonthSelected(month: Int, year: Int) {
                    if (year == Calendar.getInstance().get(Calendar.YEAR) && month > Calendar.getInstance().get(Calendar.MONTH)) {
                        showToast(getString(R.string.invalid_future_month_message))
                    } else {
                        selectedMonth = month
                        selectedYear = year
                        calender.set(Calendar.MONTH, month)
                        calender.set(Calendar.YEAR, year)

                        getMonthDataFromFirebase()
                    }

                }
            })
    }

    private fun getMonthDataFromFirebase() {


        var monthYear = DateTimeHelper.getInstance().getDisplayMonthFormat(calender.timeInMillis);

        var parts = monthYear.split(" ")
        tvYear.setText(parts.get(1))
        tvMonth.setText(parts.get(0))
        tvSelectedDate.setText(parts.get(0) + "," + parts.get(1))

        personalCall = 0
        businessCall = 0
        uncategorizedCall = 0
        totalPhoneCalls = 0
        totalCallTimeInSecond = 0
        businessCallTimeInSecond = 0
        totalPhoneCalls = 0

        showProgressDialog()
        FireBaseCallLogHelper.getInstance().getAllCallLogByMonthYear(
            monthYear!!,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {
                    hideProgressDialog()
                    for (i in 0..data.size - 1) {
                        if (data.get(i)?.callCategory.equals(Const.personal)) {
                            personalCall++;
                        } else if (data.get(i)?.callCategory.equals(Const.uncategorized)) {
                            uncategorizedCall++;
                        } else if (data.get(i)?.callCategory.equals(Const.business)) {
                            businessCall++;
                            businessCallTimeInSecond =
                                businessCallTimeInSecond + data.get(i)?.callDurationInSecond.toString().toInt()
                        }
                        totalCallTimeInSecond =
                            totalCallTimeInSecond + data.get(i)?.callDurationInSecond.toString().toInt()
                    }
                    totalPhoneCalls = personalCall + uncategorizedCall + businessCall
                    setData()
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showToast(errorMessage)
                }
            })

    }

    private fun setData() {
        tvPersonalCall.setText(personalCall.toString() + " " + getString(R.string.calls))
        tvBusinessCall.setText(businessCall.toString() + " " + getString(R.string.calls))
        tvUncategorizedCall.setText(uncategorizedCall.toString() + " " + getString(R.string.calls))
        tvMonthPhoneCall.setText(totalPhoneCalls.toString() + " " + getString(R.string.phone_calls))
        var businessCallPercentage = 0
        if (businessCall > 0) {
            businessCallPercentage = (100 * businessCall) / totalPhoneCalls
        }
        var persnalCallPercentage = 0
        if (personalCall > 0) {
            persnalCallPercentage = (100 * personalCall) / totalPhoneCalls
        }
        var uncategorizedCallPercentage = 0
        if (uncategorizedCall > 0) {
            uncategorizedCallPercentage = (100 * uncategorizedCall) / totalPhoneCalls
        }

        var completePercentage: Int = 0
        var monthlyAmount = PreferenceHelper.getInstance().getProfileData()!!.mothlyBillAmount
        if (monthlyAmount > 0 && businessCallTimeInSecond > 0) {

            var businessExpense: Double =
                (businessCallTimeInSecond.toDouble() / totalCallTimeInSecond.toDouble()) * monthlyAmount

            completePercentage = ((businessExpense * 100) / monthlyAmount).toInt()

        }

        tvMonthyUsesPercentageAmount.setText(completePercentage.toString() + "% Complete")

        circularProgressView.setPercentage(
            businessCallPercentage.toFloat(),
            persnalCallPercentage.toFloat(),
            uncategorizedCallPercentage.toFloat()
        )

    }


    lateinit var drawerMenuChangeListener: DrawerMenuChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (getContext() is DrawerMenuChangeListener) {
            drawerMenuChangeListener = context as DrawerMenuChangeListener
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnPhoneUses -> {
                if (drawerMenuChangeListener != null) {
                    drawerMenuChangeListener.setSelectedDrawerMenu(getString(R.string.menu_phone_usase))
                }
            }
            R.id.btnViewAllCall -> {
                if (drawerMenuChangeListener != null) {
                    drawerMenuChangeListener.setSelectedDrawerMenu(getString(R.string.menu_home))
                }
            }
            R.id.rlCalender -> {
                openMonthYearPicker()
            }
        }
    }

}
