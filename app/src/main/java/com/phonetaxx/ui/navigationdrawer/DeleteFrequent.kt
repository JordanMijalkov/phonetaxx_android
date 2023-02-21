package com.phonetaxx.ui.navigationdrawer

import com.phonetaxx.firebase.model.CallCategoryDbModel

interface DeleteFrequent {

    fun onDeleteContact(callCategoryDbModel: CallCategoryDbModel)
}