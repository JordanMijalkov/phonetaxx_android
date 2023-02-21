package com.phonetaxx.firebase.helper

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.phonetaxx.AppClass
import com.phonetaxx.R
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.model.SubscriptionModel
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.utils.PreferenceHelper

class FireBaseUserHelper {
    var database: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {

        val tblUsers = "USERS"


        private var instance: FireBaseUserHelper? = null

        fun getInstance(): FireBaseUserHelper {
            if (instance == null) {
                instance =
                    FireBaseUserHelper()
            }
            return instance as FireBaseUserHelper
        }

    }

    fun checkPhoneNumberExist(
        phoneNumber: String,
        countryCode: String,
        listener: FirebaseDatabaseListener<QuerySnapshot>
    ) {

        database.collection(tblUsers)
            .whereEqualTo("phoneNumber", phoneNumber)
            .whereEqualTo("countryCode", countryCode)
            .get()
            .addOnSuccessListener { documents ->
                listener.onSuccess(documents)
            }
            .addOnFailureListener { exception ->
                listener.onFail(exception.message.toString(), exception)
            }

    }


    fun updateStripeCustomerId(
        stripeCustomerId: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {
        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("stripeCustomerId", stripeCustomerId)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.stripeCustomerId = stripeCustomerId
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

    }

    fun checkEmailExist(email: String, listener: FirebaseDatabaseListener<QuerySnapshot>) {

        database.collection(tblUsers)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                listener.onSuccess(documents)
            }
            .addOnFailureListener { exception ->
                listener.onFail(exception.message.toString(), exception)
            }

    }

    fun createUser(userData: UsersDbModel, listener: FirebaseDatabaseListener<UsersDbModel>) {

/*
        val uniqueKey = database.collection(tblUsers).document().id
        userData.uuId = uniqueKey + userData.phoneNumber
*/

        database.collection(tblUsers)
            .document(userData.uuId)
            .set(userData)
            .addOnSuccessListener(OnSuccessListener<Void> {
                getUserDataFromUuid(userData.uuId, listener)
            })
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })

    }

    fun getUserDataFromUuid(
        uuId: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {
        database.collection(tblUsers).document(uuId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(UsersDbModel::class.java)
                listener.onSuccess(user!!)
            }
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })
    }

    fun deleteAccountFromFirebase(
        uuId: String,
        listener: FirebaseDatabaseListener<String>
    ) {
        database.collection(tblUsers).document(uuId)
            .delete()
            .addOnSuccessListener(OnSuccessListener<Void> {
                listener.onSuccess("")
            })
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })
    }

    fun loginWithEmailId(
        email: String,
        password: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    for (document in documents) {
                        var usersDbModel = document.toObject(UsersDbModel::class.java)
                        if (usersDbModel.password.equals(password)) {
                            listener.onSuccess(usersDbModel)
                        } else {
                            listener.onFail(
                                AppClass.getInstance().getString(R.string.invalid_password), null
                            )
                        }
                    }

                } else {
                    listener.onFail(AppClass.getInstance().getString(R.string.user_not_exist), null)
                }
            }
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })

    }

    fun loginWithPhoneNumber(
        phoneNumber: String,
        password: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    for (document in documents) {
                        var usersDbModel = document.toObject(UsersDbModel::class.java)
                        if (usersDbModel.password.equals(password)) {
                            listener.onSuccess(usersDbModel)
                        } else {
                            listener.onFail(
                                AppClass.getInstance().getString(R.string.invalid_password), null
                            )
                        }
                    }

                } else {
                    listener.onFail(AppClass.getInstance().getString(R.string.user_not_exist), null)
                }
            }
            .addOnFailureListener(OnFailureListener { e ->
                listener.onFail(e.message.toString(), e)
            })

    }

    fun updateLastSyncTime(timestamp: Long) {
        PreferenceHelper.getInstance().lastSyncCallTimeStamp = timestamp
        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("lastSyncTime", timestamp)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }

    }

    fun updateCallDeletection(
        callDetection: Int,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("callDetection", callDetection)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.callDetection = callDetection
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }


    }

    fun updatePushNotification(
        pushNotification: Int,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("pushNotification", pushNotification)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.pushNotification = pushNotification
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }


    }

    fun updateScreenTime(
        screenTimeInMinute: Int,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("businessScreenTime", screenTimeInMinute)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.businessScreenTime = screenTimeInMinute
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

    }


    fun updatePassword(
        uuid: String,
        password: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(uuid)
            .update("password", password)
            .addOnSuccessListener {

//                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

    }

    fun updateMonthlyBillAmount(amount: Double, listener: FirebaseDatabaseListener<UsersDbModel>) {
        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("mothlyBillAmount", amount)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.mothlyBillAmount = amount
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }

    }

    fun updateUserSubscription(
        subscriptionModel: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("subscription", subscriptionModel)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.Subscription = subscriptionModel
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateProfileImage(
        profileUrl: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("profileUrl", profileUrl)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.profileUrl = profileUrl
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }


    }

    fun updateName(
        fullName: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("fullName", fullName)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.fullName = fullName
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateCountryCode(
        conCode: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("countryCode", conCode)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.countryCode = conCode
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updatePhoneNumber(
        phone: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("phoneNumber", phone)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.phoneNumber = phone
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateBusinessName(
        bName: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("businessName", bName)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.businessName = bName
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateNaicsCode(
        naicsCode: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("naicscode", naicsCode)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.naicscode = naicsCode
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateLocation(
        location: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("location", location)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.location = location
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateSupervisorEmail(
        email: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("supervisoremail", email)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.supervisoremail = email
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateEincode(
        einCode: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("eincode", einCode)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.eincode = einCode
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateSubscription(
        subPlan: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("Subscription", subPlan)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.Subscription = subPlan
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }

    fun updateStartDate(
        startDate: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("PlanStartDate", startDate)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.PlanStartDate = startDate
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }


    fun updateEndDate(
        endDate: String,
        listener: FirebaseDatabaseListener<UsersDbModel>
    ) {

        database.collection(tblUsers)
            .document(PreferenceHelper.getInstance().getProfileData()?.uuId!!)
            .update("PlanExpiryDate", endDate)
            .addOnSuccessListener {
                var usersDbModel = PreferenceHelper.getInstance().getProfileData()
                usersDbModel?.PlanExpiryDate = endDate
                PreferenceHelper.getInstance().setProfileData(usersDbModel!!)
                listener.onSuccess(PreferenceHelper.getInstance().getProfileData()!!)
            }.addOnFailureListener {
                listener.onFail(it.message.toString(), it)
            }
    }


}