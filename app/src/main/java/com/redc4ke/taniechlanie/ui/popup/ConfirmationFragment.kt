package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.databinding.FragmentConfirmationBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class ConfirmationFragment(private val code: Int) :
    BaseDialogFragment<FragmentConfirmationBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentConfirmationBinding
        get() = FragmentConfirmationBinding::inflate
    private lateinit var header: String
    private lateinit var bt1: String
    private lateinit var bt2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bt1 = getString(R.string.dialog_bt1)
        bt2 = getString(R.string.dialog_bt2)

        when (code) {
            1 -> header = getString(R.string.dialog_logout_h)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            confirmationHeaderTV.text = header
            confirmationCancelBT.text = bt1
            confirmationConfirmBT.text = bt2
        }

        binding.confirmationCancelBT.setOnClickListener {
            result(false)
        }

        binding.confirmationConfirmBT.setOnClickListener {
            result(true)
        }
    }


    private fun result(value: Boolean) {
        setFragmentResult("confirmation", bundleOf("value" to value))
        this.dismiss()
    }
}