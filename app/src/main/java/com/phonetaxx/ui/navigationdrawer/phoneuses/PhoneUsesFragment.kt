package com.phonetaxx.ui.navigationdrawer.phoneuses

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.ui.BaseFragment
import com.phonetaxx.ui.navigationdrawer.uncategorized.DefineCategoryDialogFragment
import com.phonetaxx.utils.Const
import kotlinx.android.synthetic.main.fragment_phone_uses.*
import kotlinx.android.synthetic.main.no_content_layout.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PhoneUsesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PhoneUsesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PhoneUsesFragment : BaseFragment(), BaseRecyclerListener<CallLogsDbModel>, View.OnClickListener {

    private lateinit var adapter: PhoneUsesAdapterParent

    var isLoading: Boolean = false
    var pageNo: Int = 0
    var lastDocumentPage: DocumentSnapshot? = null
    var selectedCategory = Const.business
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getCountByFilterType()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone_uses, container, false)
        return view;
    }

    private fun initView() {

        cardBusiness?.setOnClickListener(this)
        cardPersonal?.setOnClickListener(this)
        cardUncategorized?.setOnClickListener(this)

        adapter = PhoneUsesAdapterParent(context, this)
        recyclerView?.setEmptyMsgHolder(tvNoData!!)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter



        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoading && pageNo != (-1)) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.allItems.size - Const.LOADING_OFFSET) {
                        //bottom of list!
                        pageNo++
                        isLoading = true
                        adapter.addProgressView()
                        getDataFromFirebase()
                        Log.v(ContentValues.TAG, ": Load more");

                    }
                }
            }
        })

        setInitSelection()

    }

    private fun setInitSelection() {
        adapter.setSelectedTabPosition(0)
        businessSelected(true)
        personalSelected(false)
        uncategorizedSelected(false)


    }


    override fun showEmptyDataView(resId: Int) {
        recyclerView.showEmptyDataView(getString(resId))
    }

    override fun onRecyclerItemClick(view: View, position: Int, item: CallLogsDbModel?) {
        when (view.id) {
            R.id.tvAssign -> {
                openAssignDialog(item)
            }
        }
    }

    private fun openAssignDialog(item: CallLogsDbModel?) {
        var defineCategoryDialogFragment = DefineCategoryDialogFragment.newInstance(item)
        defineCategoryDialogFragment.registerCallbackListener(object : FragmentCallBackListener<String> {
            override fun onSuccess(data: String) {
                getCountByFilterType()
                var listData: ArrayList<CallLogsDbModel?> = arrayListOf()
                for (i in 0..(adapter.allItems.size - 1)) {
                    if (!adapter.allItems.get(i)!!.phoneNumber.equals(item!!.phoneNumber)) {
                        listData.add(adapter.allItems.get(i))
                    }
                }
                adapter.removeAllItems()
                adapter.addItems(listData)
            }
        })
        defineCategoryDialogFragment.show(requireFragmentManager(), "defineCategoryDialogFragment")

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PhoneUsesFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    fun businessSelected(b: Boolean) {
        ivBusiness?.isSelected = b;
        tvBusinesCount?.isSelected = b;
        tvBusinessText?.isSelected = b;
        cardBusiness?.isSelected = b;

        if (b) {
            selectedCategory = Const.business
            pageNo = 0
            lastDocumentPage = null
            getDataFromFirebase()
        }
    }

    fun personalSelected(b: Boolean) {
        ivPersonal?.isSelected = b;
        tvPersonalCount?.isSelected = b;
        tvPersonalText?.isSelected = b;
        cardPersonal?.isSelected = b

        if (b) {
            selectedCategory = Const.personal
            pageNo = 0
            lastDocumentPage = null
            getDataFromFirebase()
        }
    }

    fun uncategorizedSelected(b: Boolean) {
        ivUncategorized?.isSelected = b
        tvUncategorizedCount?.isSelected = b
        tvUncategorizedText?.isSelected = b
        cardUncategorized?.isSelected = b

        if (b) {
            selectedCategory = Const.uncategorized
            pageNo = 0
            lastDocumentPage = null
            getDataFromFirebase()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cardUncategorized -> {
                adapter.setSelectedTabPosition(2)

                businessSelected(false)
                personalSelected(false)
                uncategorizedSelected(true)


            }
            R.id.cardPersonal -> {
                adapter.setSelectedTabPosition(1)
                businessSelected(false)
                personalSelected(true)
                uncategorizedSelected(false)

            }
            R.id.cardBusiness -> {
                setInitSelection()
            }
        }
    }

    fun getCountByFilterType() {
        FireBaseCallLogHelper.getInstance().getAllCallLogByCategoryAndFilterType(Const.personal, selectedFilterType,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {

                    tvPersonalCount?.setText(data.size.toString())
                }

                override fun onFail(errorMessage: String, exception: Exception?) {

                    showToast(errorMessage)
                }
            })
        FireBaseCallLogHelper.getInstance().getAllCallLogByCategoryAndFilterType(Const.business, selectedFilterType,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {
                    tvBusinesCount?.setText(data.size.toString())
                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    showToast(errorMessage)
                }
            })
        FireBaseCallLogHelper.getInstance()
            .getAllCallLogByCategoryAndFilterType(Const.uncategorized, selectedFilterType,
                object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                    override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {
                        tvUncategorizedCount?.setText(data.size.toString())
                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        showToast(errorMessage)
                    }
                })
    }

    var selectedFilterType: String = Const.TODAY
    fun setFilterData(filter: String) {
        selectedFilterType = filter
        adapter.setFilterDataType(filter)
        getCountByFilterType()
    }

    private fun getDataFromFirebase() {
        if (pageNo == 0) {
            adapter.removeAllItems()
            showProgressDialog()
        }
        FireBaseCallLogHelper.getInstance()
            .getCallLogByCategory(
                selectedCategory, lastDocumentPage,
                object : FirebaseDatabasePaginationListener<ArrayList<CallLogsDbModel?>> {

                    override fun onSuccess(lastDocument: DocumentSnapshot, data: ArrayList<CallLogsDbModel?>) {
                        lastDocumentPage = lastDocument
                        hideProgressDialog()
                        if (pageNo > 0) {
                            adapter.removeProgressView()
                        }
                        adapter.addItems(data)
                        isLoading = false

                        if (data.size < Const.PAGINATION_ITEM_COUNT) {
                            pageNo = -1
                        }

                    }

                    override fun onFail(errorMessage: String, exception: Exception?) {
                        hideProgressDialog()
                    }

                    override fun onEmpty() {
                        hideProgressDialog()
                    }

                })

    }
}
