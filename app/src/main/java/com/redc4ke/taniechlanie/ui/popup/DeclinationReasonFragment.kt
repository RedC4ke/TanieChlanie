package com.redc4ke.taniechlanie.ui.popup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.AvailabilityRequest
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.NewBoozeRequest
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
            ViewModelProvider(requireActivity() as MainActivity)[ModpanelViewModel::class.java]

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
            declinationSaveBT.setOnClickListener {
                if (selectedReason != null && request.requestId != null)
                    modpanelViewModel.declineRequest(request, selectedReason!!)
                dismiss()
                requireActivity().onBackPressed()
            }
        }
    }
}