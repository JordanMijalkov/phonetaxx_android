package com.phonetaxx.firebase.helper

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.phonetaxx.AppClass
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.FirebaseDatabasePaginationListener
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.roomdb.DatabaseHelper
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.DateTimeHelper
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class FireBaseCallLogHelper {
    var database: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {

        val tblCallLogs = "CALL_LOGS"

        private var instance: FireBaseCallLogHelper? = null

        fun getInstance(): FireBaseCallLogHelper {
            if (instance == null) {
                instance =
                    FireBaseCallLogHelper()
            }
            return instance as FireBaseCallLogHelper
        }
    }

    fun getUniqueUUID(phoneNumber: String): String {

        val uuId = database.collection(tblCallLogs).document().id

        var keyAdd = ""
        if (!phoneNumber.isNullOrEmpty() && phoneNumber.length > 8) {
            keyAdd = phoneNumber.substring(phoneNumber.length - 8)
        }

        val newUUID = uuId + keyAdd;
        return newUUID;
    }

    fun addCallLog(callLogsDbModel: ArrayList<CallLogsDbModel>, listener: FirebaseDatabaseListener<String>) {

        GlobalScope.launch {
            async { updateLastSyncTime() }
        }

        val callCategoryDbModelList: List<CallCategoryDbModel?> =
            DatabaseHelper.getInstance(AppClass.getInstance()).appDataBase.getCallCategoryDao().getAllData()

        var chunkList = callLogsDbModel.chunked(400)

        runBlocking {
            updateCallLog(chunkList, callCategoryDbModelList, listener)
        }
    }

    suspend private fun updateCallLog(
        chunkList: List<List<CallLogsDbModel>>,
        callCategoryDbModelList: List<CallCategoryDbModel?>,
        listener: FirebaseDatabaseListener<String>
    ) {
        val hashMap: HashMap<String, CallCategoryDbModel>? = HashMap()
        for (j in 0..callCategoryDbModelList.size - 1) {
            hashMap?.put(callCategoryDbModelList.get(j)!!.phoneNumber, callCategoryDbModelList.get(j)!!)
        }

        for (i in 0..chunkList.size - 1) {

            Log.v(TAG, ":addCallLog chunkList" + i);
            var callLogsDbModel = chunkList.get(i)

            val batch = database.batch()


            for (i in 0..callLogsDbModel.size - 1) {

                val newUUID = getUniqueUUID(callLogsDbModel.get(i).phoneNumber)

                val nycRef = database.collection(FireBaseUserHelper.tblUsers)
                    .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                    .collection(tblCallLogs)
                    .document(newUUID)

                callLogsDbModel.get(i).userUuid = PreferenceHelper.getInstance().getProfileData()?.uuId!!
                callLogsDbModel.get(i).uuId = newUUID
                if (hashMap?.containsKey(callLogsDbModel.get(i).phoneNumber)!!) {
                    callLogsDbModel.get(i).callCategory = hashMap.get(callLogsDbModel.get(i).phoneNumber)!!.callCategory
                }

                batch.set(nycRef, callLogsDbModel.get(i))
            }


            batch.commit().addOnSuccessListener {
                Log.v(
                    TAG,
                    "addCallLog updateCallLog success : " + chunkList.size + " current pposition : " + i
                )
                if ((chunkList.size - 1).equals(i)) {
                    listener.onSuccess("")
                }
            }.addOnFailureListener {
                Log.v(TAG, ": " + it.message.toString())
                listener.onFail(it.message.toString(), it)
            }
        }
    }

    private fun updateLastSyncTime() {
        FireBaseUserHelper.getInstance().updateLastSyncTime(DateTimeHelper.getInstance().currentTimeStamp)
    }

    fun getCallLogByCategory(
        callCategory: String,
        lastDocument: DocumentSnapshot?,
        listener: FirebaseDatabasePaginationListener<ArrayList<CallLogsDbModel?>>
    ) {
        var query: Query
        if (lastDocument != null) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo("callCategory", callCategory)
                .orderBy("callDateTimeUTC", Query.Direction.DESCENDING)
                .limit(Const.PAGINATION_ITEM_COUNT.toLong()).startAfter(lastDocument)
        } else {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo("callCategory", callCategory)
                .orderBy("callDateTimeUTC", Query.Direction.DESCENDING)
                .limit(Const.PAGINATION_ITEM_COUNT.toLong())
        }

        query.get().addOnSuccessListener { documents ->
            Log.v(TAG, "getCallLogByCategory : " + callCategory + ":" + documents.size())
            var arrayList: ArrayList<CallLogsDbModel?> = arrayListOf()

            if (documents.size() > 0) {
                for (document in documents) {
                    val callLog = document.toObject(CallLogsDbModel::class.java)
                    arrayList.add(callLog)
                }

                listener.onSuccess(documents.last(), arrayList)
            } else {
                listener.onEmpty()
            }
        }

    }

    fun getRecentCallLog(
        lastDocument: DocumentSnapshot?,
        listener: FirebaseDatabasePaginationListener<ArrayList<CallLogsDbModel?>>
    ) {

        var query: Query

        if (lastDocument != null) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .orderBy("callDateTimeUTC", Query.Direction.DESCENDING)
                .limit(Const.PAGINATION_ITEM_COUNT.toLong()).startAfter(lastDocument)
        } else {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .orderBy("callDateTimeUTC", Query.Direction.DESCENDING)
                .limit(Const.PAGINATION_ITEM_COUNT.toLong())
        }

        query.get().addOnSuccessListener { documents ->
            var arrayList: ArrayList<CallLogsDbModel?> = arrayListOf()

            if (documents.size() > 0) {
                for (document in documents) {
                    val callLog = document.toObject(CallLogsDbModel::class.java)
                    arrayList.add(callLog)
                }

                listener.onSuccess(documents.last(), arrayList)
            } else {
                listener.onEmpty()
            }
        }

    }

    val TAG: String = "FireBaseCallLogHelper"

    fun getCallLogByPhoneNumber(phoneNumber: String, listener: FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>>) {

        var query = database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallLogs)
            .whereEqualTo("phoneNumber", phoneNumber)


        query.get().addOnSuccessListener { documents ->

            var arrayList: ArrayList<CallLogsDbModel?> = arrayListOf()
            if (documents.size() > 0) {
                for (document in documents) {
                    val callLog = document.toObject(CallLogsDbModel::class.java)
                    Log.v(TAG, ": " + callLog.phoneNumber)
                    arrayList.add(callLog)
                }
                listener.onSuccess(arrayList)

            }
        }.addOnFailureListener {
            Log.v(TAG, "getCallLogByPhoneNumber : " + it)
            listener.onFail(it.message.toString(), it)
        }
    }

    fun updateCallLogWithCategory(
        dataList: ArrayList<CallLogsDbModel?>,
        callCategory: String,
        listener: FirebaseDatabaseListener<String>
    ) {

        var chunkList = dataList.chunked(400)
        Log.v(TAG, "updateCallLogWithCategory : " + chunkList.size)
        for (k in 0..chunkList.size - 1) {
            var arrayList = chunkList.get(k)
            val batch = database.batch()

            for (i in 0..arrayList.size - 1) {

                Log.v(TAG, "updateCallLogWithCategory : " + arrayList.get(i)?.uuId!!)
                val nycRef = database.collection(FireBaseUserHelper.tblUsers)
                    .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                    .collection(tblCallLogs)
                    .document(arrayList.get(i)?.uuId!!)

                batch.update(nycRef, "callCategory", callCategory)
            }

            batch.commit().addOnSuccessListener {
                Log.v(TAG, ": " + chunkList.size + " position : " + k);
                if ((chunkList.size - 1).equals(k)) {
                    listener.onSuccess("")
                }
            }.addOnFailureListener {
                Log.v(TAG, "getCallLogByPhoneNumber : " + it)
                listener.onFail(it.message.toString(), it)
            }
        }

    }

    fun getAllCallLogByCategoryAndFilterType(
        callCategory: String, filterType: String,
        listener: FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>>
    ) {
        var query: Query? = null
        if (filterType.equals(Const.TODAY)) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo("callCategory", callCategory)
                .whereEqualTo(
                    "callDate",
                    DateTimeHelper.getInstance().getDisplayDateFormat(DateTimeHelper.getInstance().currentTimeStamp)
                )
        } else if (filterType.equals(Const.WEEKLY)) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo("callCategory", callCategory)
                .whereEqualTo(
                    "callWeek",
                    DateTimeHelper.getInstance().getWeekDays(DateTimeHelper.getInstance().currentTimeStamp)
                )
        } else if (filterType.equals(Const.MONTHLY)) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo("callCategory", callCategory)
                .whereEqualTo(
                    "callMonth",
                    DateTimeHelper.getInstance().getDisplayMonthFormat(DateTimeHelper.getInstance().currentTimeStamp)
                )
        }

        query?.get()?.addOnSuccessListener { documents ->

            var arrayList: ArrayList<CallLogsDbModel?> = arrayListOf()
            if (documents.size() > 0) {
                for (document in documents) {
                    val callLog = document.toObject(CallLogsDbModel::class.java)
                    Log.v(TAG, ": " + callLog.phoneNumber)
                    arrayList.add(callLog)
                }
            }
            listener.onSuccess(arrayList)
        }?.addOnFailureListener {
            Log.v(TAG, "getCallLogByPhoneNumber : " + it)
            listener.onFail(it.message.toString(), it)
        }

    }

    fun getAllCallLogByFilterType(
        filterType: String,
        listener: FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>>
    ) {
        var query: Query? = null
        if (filterType.equals(Const.TODAY)) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo(
                    "callDate",
                    DateTimeHelper.getInstance().getDisplayDateFormat(DateTimeHelper.getInstance().currentTimeStamp)
                )
                .orderBy("callDateTimeUTC", Query.Direction.DESCENDING)
        } else if (filterType.equals(Const.WEEKLY)) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo(
                    "callWeek",
                    DateTimeHelper.getInstance().getWeekDays(DateTimeHelper.getInstance().currentTimeStamp)
                )
                .orderBy("callDateTimeUTC", Query.Direction.DESCENDING)
        } else if (filterType.equals(Const.MONTHLY)) {
            query = database.collection(FireBaseUserHelper.tblUsers)
                .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
                .collection(tblCallLogs)
                .whereEqualTo(
                    "callMonth",
                    DateTimeHelper.getInstance().getDisplayMonthFormat(DateTimeHelper.getInstance().currentTimeStamp)
                )
                .orderBy("callDateTimeUTC", Query.Direction.DESCENDING)
        }

        query?.get()?.addOnSuccessListener { documents ->

            var arrayList: ArrayList<CallLogsDbModel?> = arrayListOf()
            if (documents.size() > 0) {
                for (document in documents) {
                    val callLog = document.toObject(CallLogsDbModel::class.java)
                    Log.v(TAG, ": " + callLog.phoneNumber)
                    arrayList.add(callLog)
                }
            }
            listener.onSuccess(arrayList)
        }?.addOnFailureListener {
            Log.v(TAG, "getCallLogByPhoneNumber : " + it)
            listener.onFail(it.message.toString(), it)
        }

    }

    fun getAllCallLogByMonthYear(
        monthYear: String,
        listener: FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>>
    ) {

        var query = database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallLogs)
            .whereEqualTo(
                "callMonth",
                monthYear
            )

        query.get().addOnSuccessListener { documents ->

            var arrayList: ArrayList<CallLogsDbModel?> = arrayListOf()
            if (documents.size() > 0) {
                for (document in documents) {
                    val callLog = document.toObject(CallLogsDbModel::class.java)
                    Log.v(TAG, ": " + callLog.phoneNumber)
                    arrayList.add(callLog)
                }
            }
            listener.onSuccess(arrayList)
        }.addOnFailureListener {
            Log.v(TAG, "getCallLogByPhoneNumber : " + it)
            listener.onFail(it.message.toString(), it)
        }

    }

}