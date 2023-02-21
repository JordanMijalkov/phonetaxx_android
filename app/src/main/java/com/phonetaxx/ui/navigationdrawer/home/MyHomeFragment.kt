package com.phonetaxx.ui.navigationdrawer.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.FirebaseDatabasePaginationListener
import com.phonetaxx.firebase.helper.FireBaseCallLogHelper
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.listener.BaseRecyclerListener
import com.phonetaxx.listener.DrawerMenuChangeListener
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.model.HeaderDataModel
import com.phonetaxx.ui.BaseFragment
import com.phonetaxx.ui.DialerPadActivity
import com.phonetaxx.ui.navigationdrawer.uncategorized.DefineCategoryDialogFragment
import com.phonetaxx.ui.navigationdrawer.uncategorized.HomeCallsAdapter
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.navigation_drawer_menu.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyHomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MyHomeFragment : BaseFragment(), View.OnClickListener {
    var selectedFilterType: String = Const.TODAY
    var selectedTabPosition: Int = 0

    companion object {
        @JvmStatic
        fun newInstance() =
            MyHomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            initView()
        }

        getAllRecentCall()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    lateinit var drawerMenuChangeListener: DrawerMenuChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (getContext() is DrawerMenuChangeListener) {
            drawerMenuChangeListener = context as DrawerMenuChangeListener
        }
    }


    lateinit var adapter: HomeCallsAdapter
    suspend private fun initView() {
        adapter = HomeCallsAdapter(object : BaseRecyclerListener<CallLogsDbModel> {
            override fun showEmptyDataView(resId: Int) {
//                recyclerView.showEmptyDataView(getString(resId))
            }

            override fun onRecyclerItemClick(view: View, position: Int, item: CallLogsDbModel?) {
                if (item!!.callCategory.equals(Const.uncategorized)) {
                    var defineCategoryDialogFragment =
                        DefineCategoryDialogFragment.newInstance(item)
                    defineCategoryDialogFragment.registerCallbackListener(object :
                        FragmentCallBackListener<String> {
                        override fun onSuccess(data: String) {
//                            var listData: ArrayList<CallLogsDbModel?> = arrayListOf()
//                            for (i in 0..(adapter.allItems.size - 1)) {
//                                if (!adapter.allItems.get(i)!!.phoneNumber.equals(item!!.phoneNumber)) {
//                                    listData.add(adapter.allItems.get(i))
//                                }
//                            }
//                            adapter.removeAllItems()
//                            adapter.addItems(listData)
                            getAllRecentCall()
                        }
                    })
                    defineCategoryDialogFragment.show(
                        requireFragmentManager(),
                        "defineCategoryDialogFragment"
                    )
                }

            }

        })
        adapter.setFilterDataType(selectedFilterType)
        recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter

        viewAllCallLog?.setOnClickListener(this)
        tvAll?.setOnClickListener(this)
        llPersonal?.setOnClickListener(this)
        llBusiness?.setOnClickListener(this)
        fabAdd?.setOnClickListener(this)
        fabDialer?.setOnClickListener(this)
        cardScreenTime.setOnClickListener(this)

        if (PreferenceHelper.getInstance().totalScreenTimeInSecond!! > 0) {
            totalScreenTimeInMinute =
                (PreferenceHelper.getInstance().totalScreenTimeInSecond!!) / 60
        }

        if (PreferenceHelper.getInstance().getProfileData()!!.businessScreenTime != null) {
            businessScreenTime =
                PreferenceHelper.getInstance().getProfileData()!!.businessScreenTime
        }
        setAllSelected(true)
        getFilterData()

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.viewAllCallLog -> {
                if (drawerMenuChangeListener != null) {
                    drawerMenuChangeListener.setSelectedDrawerMenu(getString(R.string.menu_phone_usase))
                }
            }
            R.id.cardScreenTime -> {
                var screenTimeDialog = ScreenTimeDialog.newInstance(totalScreenTimeInMinute)
                screenTimeDialog.registerCallbackListener(object :
                    FragmentCallBackListener<String> {
                    override fun onSuccess(data: String) {
                        if (PreferenceHelper.getInstance()
                                .getProfileData()!!.businessScreenTime != null
                        ) {
                            businessScreenTime =
                                PreferenceHelper.getInstance().getProfileData()!!.businessScreenTime
                        }
                        if (selectedTabPosition == 0) {
                            setAllSelected(true)
                        } else if (selectedTabPosition == 1) {
                            setPersonalSelected(true)
                        } else {
                            setBusinessSelected(true)
                        }

                    }
                })

                screenTimeDialog.show(requireFragmentManager(), "screenTimeDialog")
            }
            R.id.tvAll -> {
                selectedTabPosition = 0
                setAllSelected(true)
                setPersonalSelected(false)
                setBusinessSelected(false)
                setData()
            }
            R.id.llPersonal -> {
                selectedTabPosition = 1
                setAllSelected(false)
                setPersonalSelected(true)
                setBusinessSelected(false)
                setData()
            }
            R.id.llBusiness -> {
                selectedTabPosition = 2
                setAllSelected(false)
                setPersonalSelected(false)
                setBusinessSelected(true)
                setData()
            }
            R.id.fabAdd -> {
                var addNumberDialogFragment = AddNumberDialogFragment.newInstance("", "")
                addNumberDialogFragment.registerCallbackListener(object :
                    FragmentCallBackListener<String> {
                    override fun onSuccess(data: String) {
                    }
                })
                addNumberDialogFragment.show(requireFragmentManager(), "addNumberDialogFragment")

            }
            R.id.fabDialer -> {

                if (PreferenceHelper.getInstance().getProfileData()!!.Subscription.equals(Const.MONTHLY_PLAN)) {
                    startActivity(DialerPadActivity.getIntent(requireActivity()))
                } else if (PreferenceHelper.getInstance().getProfileData()!!.Subscription.equals(Const.ANNUAL_PLAN)) {
                    startActivity(DialerPadActivity.getIntent(requireActivity()))
                }else{
                    if(PreferenceHelper.getInstance().callRead!! > 15) {
                        startActivity(DialerPadActivity.getIntent(requireActivity()))
                    }else{
                        Toast.makeText(context, "Please subscribe first", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun setAllSelected(b: Boolean) {
        tvAll?.isSelected = b
        if (b) {

            tvCallTitle.setText(getString(R.string.recent_call))
            cardBusinessUses.visibility = View.VISIBLE
            cardPersonalUses.visibility = View.VISIBLE
            cardUncategorizedCall.visibility = View.VISIBLE

            cardTakeTime.visibility = View.GONE
            cardBusinessCall.visibility = View.GONE
            cardPersonalCall.visibility = View.GONE
            cardPercentage.visibility = View.GONE

            tvScreenTime.setText(
                String.format(
                    "%.0f",
                    (totalScreenTimeInMinute.toDouble())
                ) + " mins"
            )
        }
    }

    var businessScreenTime = 0
    fun setPersonalSelected(b: Boolean) {
        if (b) {
            tvPersonal?.visibility = View.VISIBLE
            tvCallTitle.setText(getString(R.string.recent_personal_call))
            cardBusinessUses.visibility = View.GONE
            cardPersonalUses.visibility = View.GONE
            cardUncategorizedCall.visibility = View.GONE

            cardTakeTime.visibility = View.VISIBLE
            cardBusinessCall.visibility = View.GONE
            cardPersonalCall.visibility = View.VISIBLE
            cardPercentage.visibility = View.VISIBLE


            tvScreenTime.setText(
                String.format(
                    "%.0f",
                    (totalScreenTimeInMinute - businessScreenTime).toDouble()
                ) + " mins"
            )

        } else {
            tvPersonal?.visibility = View.GONE
        }

        llPersonal?.isSelected = b
        ivPersonal.isSelected = b
    }

    var totalScreenTimeInMinute: Long = 0
    fun setBusinessSelected(b: Boolean) {
        if (b) {
            tvBusiness?.visibility = View.VISIBLE
            tvCallTitle.setText(getString(R.string.recent_business_call))
            cardBusinessUses.visibility = View.GONE
            cardPersonalUses.visibility = View.GONE
            cardUncategorizedCall.visibility = View.GONE

            cardTakeTime.visibility = View.VISIBLE
            cardBusinessCall.visibility = View.VISIBLE
            cardPersonalCall.visibility = View.GONE
            cardPercentage.visibility = View.VISIBLE


            tvScreenTime.setText(
                String.format(
                    "%.0f",
                    (businessScreenTime).toDouble()
                ) + " mins"
            )


        } else {
            tvBusiness?.visibility = View.GONE
        }

        llBusiness?.isSelected = b
        ivBusiness.isSelected = b
    }

    public fun setFilterData(filter: String) {
//        showToast("Home : " + filter)
        if (!filter.toString().isEmpty()) {
            selectedFilterType = filter
        } else {
            getAllRecentCall()
        }
        showProgressDialog()
        getFilterData()
    }


    var recentAllcallLogList: ArrayList<CallLogsDbModel?> = ArrayList()
    var recentPersonalCallLogList: ArrayList<CallLogsDbModel?> = ArrayList()
    var recentBusinessCallLogList: ArrayList<CallLogsDbModel?> = ArrayList()
    var lastDocumentPage: DocumentSnapshot? = null
    private fun getFilterData() {

        FireBaseCallLogHelper.getInstance().getAllCallLogByFilterType(selectedFilterType,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {
                    if (isAdded && !isDetached) {
                        hideProgressDialog()
                        setOverViewData(data)
                    }

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                    showToast(errorMessage)

                }
            })
    }

    val TAG: String = "HomeFragment"
    var totalCallDurationBusinessCallInMinute = 0.0
    var totalCallDurationPersonalCallInMinute = 0.0
    var filterCallLogList: ArrayList<CallLogsDbModel?> = ArrayList()

    fun getCurrentFilterCallLog(): ArrayList<CallLogsDbModel?> {
        return filterCallLogList
    }

    private fun setOverViewData(callLogList: ArrayList<CallLogsDbModel?>) {
        filterCallLogList.clear()
        filterCallLogList.addAll(callLogList)

        var businessCall = 0.0
        var personCall = 0.0
        var uncategorizedCall = 0.0

        var businessUses = 0.0
        var personalUses = 0.0
        var totalCallDurationInSecond = 0.0
        var totalCallDurationBusinessCallInSecond = 0.0
        var totalCallDurationPersonalCallInSecond = 0.0
        for (i in 0..callLogList.size - 1) {
            if (callLogList.get(i)?.callCategory!!.equals(Const.business)) {
                businessCall++
                totalCallDurationBusinessCallInSecond =
                    totalCallDurationBusinessCallInSecond + callLogList.get(i)?.callDurationInSecond.toString()
                        .toInt()

            } else if (callLogList.get(i)?.callCategory!!.equals(Const.personal)) {
                personCall++
                totalCallDurationPersonalCallInSecond =
                    totalCallDurationPersonalCallInSecond + callLogList.get(i)?.callDurationInSecond.toString()
                        .toInt()

            } else if (callLogList.get(i)?.callCategory!!.equals(Const.uncategorized)) {
                uncategorizedCall++
            }

            totalCallDurationInSecond =
                totalCallDurationInSecond + callLogList.get(i)?.callDurationInSecond.toString()
                    .toInt()
        }
        if (businessCall > 0) {
//            businessUses = ((100 * businessCall) / (businessCall + personCall))
//            businessUses =
//                (totalCallDurationBusinessCallInSecond / 60 + PreferenceHelper.getInstance()
//                    .getProfileData()!!.businessScreenTime) / (totalCallDurationInSecond / 60 + totalScreenTimeInMinute) * 100

            businessUses =
                (totalCallDurationBusinessCallInSecond / 60) / (totalCallDurationInSecond / 60) * 100
        }
        if (personCall > 0) {

//            personalUses =
//                (totalCallDurationPersonalCallInMinute / 60 + (totalScreenTimeInMinute - PreferenceHelper.getInstance()
//                    .getProfileData()!!.businessScreenTime)) / (totalCallDurationInSecond / 60 + totalScreenTimeInMinute) * 100

            personalUses =
                (totalCallDurationPersonalCallInMinute / 60) / (totalCallDurationInSecond / 60) * 100

//            personalUses = ((100 * personCall) / (businessCall + personCall))
        }

        tvBusinessUses.setText(String.format("%.0f", businessUses) + "%")
        tvPersonalUses.setText(String.format("%.0f", personalUses) + "%")


        tvPersonalCall.setText(String.format("%.0f", personCall))
        tvBusinessCall.setText(String.format("%.0f", businessCall))
        tvUncategorizedCall.setText(String.format("%.0f", uncategorizedCall))

//        tvCalls.setText(callLogList.size.toString())

        if (totalCallDurationPersonalCallInSecond > 0) {
            totalCallDurationPersonalCallInMinute = totalCallDurationPersonalCallInSecond / 60
        } else {
            totalCallDurationPersonalCallInMinute = 0.0
        }

        if (totalCallDurationBusinessCallInSecond > 0) {
            totalCallDurationBusinessCallInMinute = totalCallDurationBusinessCallInSecond / 60
        } else {
            totalCallDurationBusinessCallInMinute = 0.0
        }

        Log.v(TAG, "setOverViewData : Calls : " + callLogList.size)
        Log.v(TAG, "setOverViewData : Minutes Talked : " + (totalCallDurationInSecond / 60))
        /*   if (totalCallDurationInSecond > 0) {
               tvMinuteTalk.setText((totalCallDurationInSecond / 60).toString())
           } else {
               tvMinuteTalk.setText("0")
           }
       */

        if (selectedTabPosition == 1) {
            tvPercentage.setText(tvPersonalUses.text.toString())
            tvTakeTime.setText(
                String.format(
                    "%.0f",
                    totalCallDurationPersonalCallInMinute
                ) + " mins"
            )

        } else if (selectedTabPosition == 2) {
            tvPercentage.setText(tvBusinessUses.text.toString())
            tvTakeTime.setText(
                String.format(
                    "%.0f",
                    totalCallDurationBusinessCallInMinute
                ) + " mins"
            )
        }

        var dataModel = HeaderDataModel()
        dataModel.calls = String.format("%.0f", businessCall + personCall + uncategorizedCall)
        if (totalCallDurationInSecond > 0) {
            dataModel.minuteTalked = String.format("%.0f", totalCallDurationInSecond / 60)
        } else {
            dataModel.minuteTalked = "0"
        }
        if (totalCallDurationInSecond > 0 && totalCallDurationBusinessCallInSecond > 0) {
            dataModel.expense =
                getString(R.string.currency_sign) + calculateBusinessExpanse(
                    totalCallDurationInSecond,
                    totalCallDurationBusinessCallInSecond
                )
        } else {
            dataModel.expense =
                getString(R.string.currency_sign) + "0"
        }
        if (listener != null) {
            listener.onSuccess(dataModel)
        }

    }

    private fun calculateBusinessExpanse(
        totalCallDurationInSecond: Double,
        totalCallDurationBusinessCallInSecond: Double
    ): String {
        var businessExpanse = 0.0
        var monthlyBilling = PreferenceHelper.getInstance().getProfileData()!!.mothlyBillAmount
        businessExpanse =
            ((totalCallDurationBusinessCallInSecond / 60) / (totalCallDurationInSecond / 60)) * monthlyBilling
//        Business Expenses ($) = Business Usage * Monthly Phone Bill (from Acct Settings)
        businessExpanse =
            (totalCallDurationBusinessCallInSecond / 60 + PreferenceHelper.getInstance()
                .getProfileData()!!.businessScreenTime) / (totalCallDurationInSecond / 60 + totalScreenTimeInMinute) * monthlyBilling
        return String.format("%.2f", businessExpanse)
    }

    private fun setData() {
        adapter.removeAllItems()
        adapter.notifyDataSetChanged()

        adapter.setFilterDataType(selectedFilterType)
        if (selectedTabPosition == 0) {
            if (recentAllcallLogList.size > 0) {
                cardCallListWithViewAll?.visibility = View.VISIBLE
                tvCallTitle?.visibility = View.VISIBLE

                if (recentAllcallLogList.size > 4) {
                    for (i in 0..3) {
                        adapter.addItem(recentAllcallLogList.get(i))
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.addItems(recentAllcallLogList)
                }
            } else {
                cardCallListWithViewAll?.visibility = View.GONE
                tvCallTitle?.visibility = View.GONE
            }

        } else if (selectedTabPosition == 1) {
            if (recentPersonalCallLogList.size > 0) {
                cardCallListWithViewAll?.visibility = View.VISIBLE
                tvCallTitle.visibility = View.VISIBLE

                if (recentPersonalCallLogList.size > 4) {
                    for (i in 0..3) {
                        adapter.addItem(recentPersonalCallLogList.get(i))
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.addItems(recentPersonalCallLogList)
                }
            } else {
                cardCallListWithViewAll?.visibility = View.GONE
                tvCallTitle?.visibility = View.GONE
            }
            tvPercentage.setText(tvPersonalUses.text.toString())
            tvTakeTime.setText(
                String.format(
                    "%.0f",
                    totalCallDurationPersonalCallInMinute
                ) + " mins"
            )
        } else if (selectedTabPosition == 2) {
            if (recentBusinessCallLogList.size > 0) {
                cardCallListWithViewAll?.visibility = View.VISIBLE
                tvCallTitle?.visibility = View.VISIBLE

                if (recentBusinessCallLogList.size > 4) {
                    for (i in 0..3) {
                        adapter.addItem(recentBusinessCallLogList.get(i))
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.addItems(recentBusinessCallLogList)
                }
            } else {
                cardCallListWithViewAll?.visibility = View.GONE
                tvCallTitle?.visibility = View.GONE
            }
            tvPercentage.setText(tvBusinessUses.text.toString())
            tvTakeTime.setText(
                String.format(
                    "%.0f",
                    totalCallDurationBusinessCallInMinute
                ) + " mins"
            )
        }


    }

    fun getAllRecentCall() {

        FireBaseCallLogHelper.getInstance()
            .getRecentCallLog(
                lastDocumentPage,
                object : FirebaseDatabasePaginationListener<ArrayList<CallLogsDbModel?>> {

                    override fun onSuccess(
                        lastDocument: DocumentSnapshot,
                        data: ArrayList<CallLogsDbModel?>
                    ) {
                        if (isAdded && !isDetached) {
                            hideProgressDialog()
                            recentAllcallLogList.clear()
                            recentAllcallLogList = data
                            getRecentBusinessCall()
                        }
                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        hideProgressDialog()
                        getRecentBusinessCall()
                    }

                    override fun onEmpty() {
                        hideProgressDialog()
                        getRecentBusinessCall()
                    }

                })

    }

    fun getRecentBusinessCall() {

        FireBaseCallLogHelper.getInstance()
            .getCallLogByCategory(
                Const.business, lastDocumentPage,
                object : FirebaseDatabasePaginationListener<ArrayList<CallLogsDbModel?>> {

                    override fun onSuccess(
                        lastDocument: DocumentSnapshot,
                        data: ArrayList<CallLogsDbModel?>
                    ) {
                        if (isAdded && !isDetached) {
                            recentBusinessCallLogList.clear()
                            recentBusinessCallLogList.addAll(data)
                            getRecentPersonalCall()
                        }
                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        hideProgressDialog()
                        getRecentPersonalCall()
                    }

                    override fun onEmpty() {
                        hideProgressDialog()
                        getRecentPersonalCall()
                    }

                })
    }

    fun getRecentPersonalCall() {

        FireBaseCallLogHelper.getInstance()
            .getCallLogByCategory(
                Const.personal, lastDocumentPage,
                object : FirebaseDatabasePaginationListener<ArrayList<CallLogsDbModel?>> {

                    override fun onSuccess(
                        lastDocument: DocumentSnapshot,
                        data: ArrayList<CallLogsDbModel?>
                    ) {
                        if (isAdded && !isDetached) {
                            recentPersonalCallLogList.clear()
                            recentPersonalCallLogList.addAll(data)
                            setData()
                        }
                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        hideProgressDialog()
                        setData()
                    }

                    override fun onEmpty() {
                        hideProgressDialog()
                        setData()
                    }

                })
    }

    lateinit var listener: FragmentCallBackListener<HeaderDataModel>

    fun registerCallbackListener(fragmentCallBackListener: FragmentCallBackListener<HeaderDataModel>) {
        listener = fragmentCallBackListener
    }
}
