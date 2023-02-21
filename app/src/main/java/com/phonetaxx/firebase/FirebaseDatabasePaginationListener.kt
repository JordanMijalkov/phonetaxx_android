package com.phonetaxx.firebase

import com.google.firebase.firestore.DocumentSnapshot

interface FirebaseDatabasePaginationListener<T> {
    fun onSuccess(lastDocument: DocumentSnapshot, data: T)
    fun onFail(errorMessage: String, exception: Exception?)
    fun onEmpty()
}