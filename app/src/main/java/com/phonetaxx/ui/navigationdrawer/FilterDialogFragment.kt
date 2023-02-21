package com.phonetaxx.ui.navigationdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.phonetaxx.R
import com.phonetaxx.listener.FragmentCallBackListener
import com.phonetaxx.utils.Const
import kotlinx.android.synthetic.main.filter_dialog.*
import kotlinx.android.synthetic.main.fragment_add_number_dialog.ivClose


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddNumberDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddNumberDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FilterDialogFragment : DialogFragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        ivClose.setOnClickListener(this)

        tvToday?.setOnClickListener(this)
        tvWeekly?.setOnClickListener(this)
        tvMonthly?.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.filter_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.getWindow()?.setBackgroundDrawableResource(R.color.dialog_transparent_bg);

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilterDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivClose -> {
                dialog?.dismiss()
            }
            R.id.tvToday -> {
                listener.onSuccess(Const.TODAY)
                dialog?.dismiss()

            }
            R.id.tvWeekly -> {
                listener.onSuccess(Const.WEEKLY)
                dialog?.dismiss()
            }
            R.id.tvMonthly -> {
                listener.onSuccess(Const.MONTHLY)
                dialog?.dismiss()
            }
        }
    }

    lateinit var listener: FragmentCallBackListener<String>

    public fun registerCallbackListener(fragmentCallBackListener: FragmentCallBackListener<String>) {
        listener = fragmentCallBackListener
    }
}
