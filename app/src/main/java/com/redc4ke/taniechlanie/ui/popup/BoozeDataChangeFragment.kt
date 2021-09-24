package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.databinding.FragmentBoozeDataChangeBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class BoozeDataChangeFragment(itemId: Long, listener: RequestListener) :
    BaseDialogFragment<FragmentBoozeDataChangeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBoozeDataChangeBinding
        get() = FragmentBoozeDataChangeBinding::inflate

    override fun onStart() {
        super.onStart()

        this.isCancelable = false

        //This avoids the weird bug where parent layout params get removed
        dialog?.window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alcoObjectViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[AlcoObjectViewModel::class.java]

        with(binding) {
            bdcNameET.addTextChangedListener {
                bdcNameTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 20 -> getString(R.string.err_tooLong, "20")
                            it.length < 2 -> getString(R.string.err_tooShort, "2")
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }
            bdcVolumeET.addTextChangedListener {
                bdcVolumeTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 6 -> getString(R.string.err_tooLongShortened, "6")
                            it.toString().toInt() == 0 -> getString(R.string.err_zeroValue)
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }
            bdcVoltageET.addTextChangedListener {
                bdcVoltageTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 5 -> getString(R.string.err_tooLongShortened, "5")
                            it[0] == '.' -> getString(R.string.err_wrongInput)
                            it.last() == '.' -> getString(R.string.err_wrongInput)
                            it.toString().toFloat() == 0f -> getString(R.string.err_zeroValue)
                            it.toString().toFloat() > 100f -> getString(R.string.err_over100p)
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }

            bdcCancelBT.setOnClickListener {
                dismiss()
            }
            bdcSendBT.setOnClickListener {
                alcoObjectViewModel
            }
        }

    }

}