package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ConnectionCheck
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.databinding.FragmentBoozeDataChangeBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class BoozeDataChangeFragment(
    private val itemId: Long,
    private val listener: RequestListener
) : BaseDialogFragment<FragmentBoozeDataChangeBinding>() {

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
            ViewModelProvider(requireActivity())[AlcoObjectViewModel::class.java]

        with(binding) {
            val alcoObject = alcoObjectViewModel.get(itemId)
            bdcNameET.setText(alcoObject?.name)
            bdcVolumeET.setText(alcoObject?.volume.toString())
            bdcVoltageET.setText(alcoObject?.voltage.toString())

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
                bdcSendBT.text = ""
                bdcPB.visibility = View.VISIBLE

                ConnectionCheck.perform(object : RequestListener {
                    override fun onComplete(resultCode: Int) {
                        if (resultCode != RequestListener.SUCCESS) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.err_no_connection),
                                Toast.LENGTH_LONG
                            ).show()

                            bdcSendBT.text = getString(R.string.send)
                            bdcPB.visibility = View.GONE

                            return

                        } else {
                            val inputList = listOf(bdcNameTIL, bdcVolumeTIL, bdcVoltageTIL)
                            inputList.forEach {
                                if (it.error != null) {
                                    bdcSendBT.text = getString(R.string.send)
                                    bdcPB.visibility = View.GONE

                                    return
                                }
                            }

                            alcoObjectViewModel.changeData(
                                itemId,
                                bdcNameET.text.toString(),
                                bdcVolumeET.text.toString().toInt(),
                                bdcVoltageET.text.toString().toBigDecimal(),
                                object : RequestListener {
                                    override fun onComplete(resultCode: Int) {
                                        dismiss()
                                        listener.onComplete(resultCode)
                                    }
                                }
                            )
                        }
                    }
                })
            }
        }
    }
}