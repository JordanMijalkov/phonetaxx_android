package com.phonetaxx.firebase

interface FirebaseDatabaseListener<T> {
    fun onSuccess(data: T)
    fun onFail(errorMessage: String, exception: Exception?)
}