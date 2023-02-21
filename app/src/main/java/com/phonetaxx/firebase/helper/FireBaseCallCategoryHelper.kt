package com.phonetaxx.firebase.helper

import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.FirebaseDatabasePaginationListener
import com.phonetaxx.firebase.model.CallCategoryDbModel
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.utils.PreferenceHelper

class FireBaseCallCategoryHelper {
    var database: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {

        val tblCallCategory = "CALL_CATEGORY"

        private var instance: FireBaseCallCategoryHelper? = null

        fun getInstance(): FireBaseCallCategoryHelper {
            if (instance == null) {
                instance =
                    FireBaseCallCategoryHelper()
            }
            return instance as FireBaseCallCategoryHelper
        }
    }

    fun getUniqueUUID(phoneNumber: String): String {

        val uuId = database.collection(tblCallCategory).document().id

        var keyAdd = ""
        if (!phoneNumber.isNullOrEmpty() && phoneNumber.length > 8) {
            keyAdd = phoneNumber.substring(phoneNumber.length - 8)
        }

        val newUUID = uuId + keyAdd;
        return newUUID;
    }

    fun addCallCategory(callCategoryDbModel: CallCategoryDbModel, @NonNull listener: FirebaseDatabaseListener<String>) {

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .set(callCategoryDbModel)
            .addOnSuccessListener(OnSuccessListener<Void> {
                listener.onSuccess("")
            })
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })

    }

    fun deleteCallCategory(
        callCategoryDbModel: CallCategoryDbModel,
        @NonNull listener: FirebaseDatabaseListener<String>
    ) {

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .delete()
            .addOnSuccessListener(OnSuccessListener<Void> {
                listener.onSuccess("")
            })
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })

    }

    fun getCategorizedCall(@NonNull listener: FirebaseDatabasePaginationListener<ArrayList<CallCategoryDbModel?>>) {

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .get()
            .addOnSuccessListener { documents ->

                var arrayList: ArrayList<CallCategoryDbModel?> = arrayListOf()
                if (documents.size() > 0) {
                    for (document in documents) {
                        val callLog = document.toObject(CallCategoryDbModel::class.java)
                        arrayList.add(callLog)
                    }
                    listener.onSuccess(documents.last(), arrayList)
                } else {
                    listener.onEmpty()
                }


            }
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })

    }

    fun getFrequentNumber(@NonNull listener: FirebaseDatabasePaginationListener<ArrayList<CallCategoryDbModel?>>) {

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .whereEqualTo("numberType", 1)
            .get()
            .addOnSuccessListener { documents ->

                var arrayList: ArrayList<CallCategoryDbModel?> = arrayListOf()
                if (documents.size() > 0) {
                    for (document in documents) {
                        val callLog = document.toObject(CallCategoryDbModel::class.java)
                        arrayList.add(callLog)
                    }
                    listener.onSuccess(documents.last(), arrayList)
                } else {
                    listener.onEmpty()
                }


            }
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })

    }

    fun updateNumber(
        callCategoryDbModel: CallCategoryDbModel,
        listener: FirebaseDatabaseListener<CallCategoryDbModel>
    ) {

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .update("phoneNumber", callCategoryDbModel.phoneNumber)
            .addOnSuccessListener {

                listener.onSuccess(callCategoryDbModel)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .update("phoneName", callCategoryDbModel.phoneName)
            .addOnSuccessListener {


            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .update("callCategory", callCategoryDbModel.callCategory)
            .addOnSuccessListener {


            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .update("businessName", callCategoryDbModel.businessName)
            .addOnSuccessListener {


            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .update("location", callCategoryDbModel.location)
            .addOnSuccessListener {


            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

        database.collection(FireBaseUserHelper.tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .collection(tblCallCategory)
            .document(callCategoryDbModel.uuId)
            .update("naicsCode", callCategoryDbModel.naicsCode)
            .addOnSuccessListener {


            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

    }


}