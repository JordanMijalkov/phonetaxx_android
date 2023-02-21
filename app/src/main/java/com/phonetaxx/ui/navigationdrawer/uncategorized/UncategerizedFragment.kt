package com.phonetaxx.ui.navigationdrawer.uncategorized

import android.content.ContentValues.TAG
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
import com.phonetaxx.firebase.FirebaseDatabasePaginationListener
import com.phonetaxx.firebase.helper.FireBaseCallLogHelper
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.listener.BaseRecyclerListener
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.ui.BaseFragment
import com.phonetaxx.utils.Const
import kotlinx.android.synthetic.main.fragment_phone_uses.*
import kotlinx.android.synthetic.main.no_content_layout.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UncateGerizedFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UncateGerizedFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UncateGerizedFragment : BaseFragment(), BaseRecyclerListener<CallLogsDbModel> {
    private lateinit var adapter: UnCategorizedCallsAdapterParent

    var isLoading: Boolean = false
    var pageNo: Int = 0
    var lastDocumentPage: DocumentSnapshot? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            UncateGerizedFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val view = inflater.inflate(R.layout.fragment_uncate_gerized, container, false)
        return view;
    }

    private fun initView() {
        adapter = UnCategorizedCallsAdapterParent(context, this)
        if (tvNoData != null) {
            recyclerView?.setEmptyMsgHolder(tvNoData)
        }
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
                        Log.v(TAG, ": Load more");

                    }
                }
            }
        })


        getDataFromFirebase()
    }


    private fun getDataFromFirebase() {
        if (pageNo == 0) {
            adapter.removeAllItems()
            showProgressDialog()
        }

        FireBaseCallLogHelper.getInstance()
            .getCallLogByCategory(
                Const.uncategorized, lastDocumentPage,
                object : FirebaseDatabasePaginationListener<ArrayList<CallLogsDbModel?>> {

                    override fun onSuccess(lastDocument: DocumentSnapshot, data: ArrayList<CallLogsDbModel?>) {
                        hideProgressDialog()

                        lastDocumentPage = lastDocument
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
                        isLoading = false
                        pageNo = -1
                    }

                })

    }


    override fun showEmptyDataView(resId: Int) {
//        showToast("showEmptyDataView")
        try {
            recyclerView.showEmptyDataView(getString(resId))

        } catch (e: Exception) {

        }
    }

    override fun onRecyclerItemClick(view: View, position: Int, item: CallLogsDbModel?) {
        var defineCategoryDialogFragment = DefineCategoryDialogFragment.newInstance(item)
        defineCategoryDialogFragment.registerCallbackListener(object : FragmentCallBackListener<String> {
            override fun onSuccess(data: String) {
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


    public fun setFilterData(filter: String) {
        adapter.setFilterDataType(filter)
    }

}
