package com.phonetaxx.ui.navigationdrawer.accountsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.phonetaxx.R
import com.phonetaxx.adapter.FilterRecyclerView
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.FirebaseDatabasePaginationListener
import com.phonetaxx.firebase.helper.FireBaseCallCategoryHelper
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.listener.BaseRecyclerListener
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.roomdb.DatabaseHelper
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.ui.navigationdrawer.DeleteFrequent
import com.phonetaxx.ui.navigationdrawer.home.AddNumberDialogFragment
import kotlinx.android.synthetic.main.activity_frequent_number.*
import kotlinx.android.synthetic.main.no_content_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

class FrequentNumberActivity : BaseActivity(), View.OnClickListener,
    BaseRecyclerListener<CallCategoryDbModel>,
    DeleteFrequent {

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, FrequentNumberActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frequent_number)
        initView()

    }

    lateinit var adapter: FrequentNumberAdapter
    private fun initView() {
        cardviewToolbar.setBackgroundResource(R.drawable.toolbar_card_background)
        tvToolbarTitle.setText(getString(R.string.frequent_numbers))
        ivBackToolbar.setOnClickListener(this);
        llAddNew.setOnClickListener(this);

        var recyclerView = findViewById<FilterRecyclerView>(R.id.recyclerView)
        adapter = FrequentNumberAdapter(this, this, this)
        recyclerView?.setEmptyMsgHolder(tvNoData!!)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter

    }

    private fun getCategorizedData() {
        showProgressDialog()
        FireBaseCallCategoryHelper.getInstance()
            .getFrequentNumber(object :
                FirebaseDatabasePaginationListener<ArrayList<CallCategoryDbModel?>> {
                override fun onSuccess(
                    lastDocument: DocumentSnapshot,
                    data: ArrayList<CallCategoryDbModel?>
                ) {
                    hideProgressDialog()
                    adapter.removeAllItems()
                    adapter.addItems(data)
                    if (data.size > 0) {
//                        showToast("Size = " + data.size.toString())
                    }

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }

                override fun onEmpty() {
                    hideProgressDialog()
                    adapter.removeAllItems()

                }
            })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackToolbar -> {
                onBackPressed()
            }
            R.id.llAddNew -> {
                var addNumberDialogFragment = AddNumberDialogFragment.newInstance("", "")
                addNumberDialogFragment.registerCallbackListener(object :
                    FragmentCallBackListener<String> {
                    override fun onSuccess(data: String) {
                        getCategorizedData()
                    }
                })
                addNumberDialogFragment.show(supportFragmentManager, "AddNumberDialogFragment")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun showEmptyDataView(resId: Int) {
//        showToast("showEmptyDataView")
        recyclerView.showEmptyDataView(getString(resId))
    }

    override fun onRecyclerItemClick(view: View, position: Int, item: CallCategoryDbModel?) {

    }

    override fun onDeleteContact(callCategoryDbModel: CallCategoryDbModel) {

        AlertDialog.Builder(this@FrequentNumberActivity)
            .setTitle(getString(R.string.declined))
            .setMessage("Are you sure want to delete this number from frequent list?")
            .setPositiveButton(getString(R.string.delete)) { dialogInterface, _ ->
                showProgressDialog()
                FireBaseCallCategoryHelper.getInstance()
                    .deleteCallCategory(
                        callCategoryDbModel,
                        object : FirebaseDatabaseListener<String> {
                            override fun onSuccess(data: String) {
                                hideProgressDialog()
                                deleteIntoLocalDb(callCategoryDbModel)
                                getCategorizedData()
                            }

                            override fun onFail(errorMessage: String, exception: Exception?) {
                                hideProgressDialog()
                                showToast(errorMessage)
                            }

                        })
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ ->

            }.create().show()

    }

    private fun deleteIntoLocalDb(callCategoryDbModel: CallCategoryDbModel) {
        var appDatabase = DatabaseHelper.getInstance(this).appDataBase
        appDatabase.getCallCategoryDao().deleteCategory(callCategoryDbModel)
    }

    override fun onResume() {
        getCategorizedData()
        super.onResume()
    }

}
