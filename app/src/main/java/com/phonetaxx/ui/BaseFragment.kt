package com.phonetaxx.ui

import androidx.fragment.app.Fragment
import com.phonetaxx.extension.showToast

abstract class BaseFragment : Fragment() {

    fun showErrorMessage(message: String) {
        showToast(message)
    }

}