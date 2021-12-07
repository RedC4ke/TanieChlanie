package com.redc4ke.taniechlanie.ui.popup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ConnectionCheck
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.Request
import com.redc4ke.taniechlanie.databinding.FragmentDeclinationReasonBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class DeclinationReasonFragment(
    private val request: Request
) : BaseDialogFragment<FragmentDeclinationReasonBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDeclinationReasonBinding
        get() = FragmentDeclinationReasonBinding::inflate

    override fun onStart() {
        super.onStart()

        val reasonList = resources.getStringArray(R.array.decliantion_reasons)
        var selectedReason: String? = null
        val modpanelViewModel =
            ViewModelProvider(requireActivity())[ModpanelViewModel::class.java]

        with(binding) {
            declinationSPINNER.adapter =
                ArrayAdapter(requireContext(), R.layout.spinner1, reasonList)
            declinationSPINNER.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedReason = reasonList[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }

            declinationCancelBT.setOnClickListener {
                dismiss()
            }
            declinationSaveBT.setOnClickListener { button ->
                button.isEnabled = false

                if (selectedReason != null && request.requestId != null)
                    ConnectionCheck.perform(object : RequestListener {
                        override fun onComplete(resultCode: Int) {
                            if (resultCode == RequestListener.NETWORK_ERR) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.err_no_connection),
                                    Toast.LENGTH_LONG
                                ).show()
                                button.isEnabled = true
                            } else {
                                modpanelViewModel.declineRequest(
                                    request,
                                    selectedReason!!,
                                    object : RequestListener {
                                        override fun onComplete(resultCode: Int) {
                                            if (resultCode != RequestListener.SUCCESS) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.toast_error),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    })
                                dismiss()
                                requireActivity().onBackPressed()
                            }
                        }
                    })
            }
        }
    }
}