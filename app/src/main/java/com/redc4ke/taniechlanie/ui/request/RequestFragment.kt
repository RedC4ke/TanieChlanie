package com.redc4ke.taniechlanie.ui.request

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.RequestViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentRequestBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.RequestShopFragment

private const val PICK_IMAGE = 1

class RequestFragment : BaseFragment<FragmentRequestBinding>(), DialogInterface.OnDismissListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestBinding
        get() = FragmentRequestBinding::inflate
    private lateinit var requestViewModel: RequestViewModel

    override fun onStart() {
        super.onStart()

        val shopViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[ShopViewModel::class.java]
        requestViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[RequestViewModel::class.java]

        with(binding) {
            requestNameET.addTextChangedListener {
                requestNameTIL.error =
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
            requestVolumeET.addTextChangedListener {
                requestVolumeTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 6 -> getString(R.string.err_tooLongShortened, "6")
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }
            requestVoltageET.addTextChangedListener {
                requestVoltageTIL.error =
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
            requestPriceET.addTextChangedListener {
                requestPriceTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 10 -> getString(R.string.err_tooLongShortened, "10")
                            it[0] == '.' -> getString(R.string.err_wrongInput)
                            it.last() == '.' -> getString(R.string.err_wrongInput)
                            it.toString().toFloat() == 0f -> getString(R.string.err_zeroValue)
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }

            requestShopBT.setOnClickListener {
                RequestShopFragment(shopViewModel, requestViewModel)
                    .show(parentFragmentManager, "requestShopFragment")
            }
            requestPhotoBT.setOnClickListener {
                addPhoto()
            }

            requestViewModel.getShop().observe(viewLifecycleOwner, {
                Log.d("huj", it)
                requestShopBT.text = "${getString(R.string.request_shopbt)} $it"
            })

            requestSendBT.setOnClickListener {
                listOf(
                    requestNameTIL,
                    requestVoltageTIL,
                    requestVolumeTIL,
                    requestPriceTIL
                ).forEach {
                    if (it.error != null) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_overallcheck),
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }
                }

                if (FirebaseAuth.getInstance().currentUser == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_notloggedin),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                val input = mutableListOf<String>()
                listOf(requestNameET, requestVolumeET, requestVoltageET, requestPriceET).forEach {
                    input.add(it.text.toString())
                }

                requestViewModel.fillRequest(
                    input[0],
                    input[1].toInt(),
                    input[2].toBigDecimal(),
                    input[3].toBigDecimal()
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                requestViewModel.setImage(data.data!!)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        addPhoto()
    }

    private fun addPhoto() {
        val intent = Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.chose_photo)),
            PICK_IMAGE
        )
    }

}