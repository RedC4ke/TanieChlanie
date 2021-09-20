package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.databinding.FragmentSpinnerBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class SpinnerFragment(
    private val options: List<String>
) : BaseDialogFragment<FragmentSpinnerBinding>() {

    private var selectedOption = 0

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSpinnerBinding
        get() = FragmentSpinnerBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        setFragmentResultListener(
            "spinnerParent"
        ) { _, bundle ->
            val result = bundle.getInt("listenerResult")
            if (result == com.redc4ke.taniechlanie.data.RequestListener.SUCCESS) {
                dismiss()
            }
        }

        with(binding) {
            spinnerSPINNER.adapter = ArrayAdapter(requireContext(), R.layout.spinner1, options)
            spinnerSPINNER.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedOption = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            spinnerCancelBT.setOnClickListener {
                dismiss()
            }

            spinnerSaveBT.setOnClickListener {
                spinnerSaveBT.text = ""
                spinnerPB.visibility = View.VISIBLE

                setFragmentResult(
                    "spinnerResult",
                    bundleOf("spinnerPosition" to selectedOption)
                )
            }
        }
    }
}