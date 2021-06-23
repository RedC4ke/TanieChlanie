package com.redc4ke.taniechlanie.ui.popup

import android.view.LayoutInflater
import android.view.ViewGroup
import com.redc4ke.taniechlanie.databinding.FragmentInformationalBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class InformationalFragment(
    private val header: String,
    private val content: String
) : BaseDialogFragment<FragmentInformationalBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInformationalBinding
        get() = FragmentInformationalBinding::inflate

    override fun onStart() {
        super.onStart()

        binding.informationalHeaderTV.text = header
        binding.informationalContentTV.text = content
        binding.informationalConfirmBT.setOnClickListener {
            dismiss()
        }
    }


}