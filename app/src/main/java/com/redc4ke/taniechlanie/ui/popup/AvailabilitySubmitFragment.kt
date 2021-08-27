package com.redc4ke.taniechlanie.ui.popup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ConnectionCheck
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.viewmodels.AvailabilityRequest
import com.redc4ke.taniechlanie.data.viewmodels.Request
import com.redc4ke.taniechlanie.data.viewmodels.RequestViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentAvailabilitySubmitBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment
import com.redc4ke.taniechlanie.ui.menu.details.DetailsFragment


class AvailabilitySubmitFragment(detailsFragment: DetailsFragment, private val shopId: Int?) :
    BaseDialogFragment<FragmentAvailabilitySubmitBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) ->
    FragmentAvailabilitySubmitBinding
        get() = FragmentAvailabilitySubmitBinding::inflate
    private lateinit var selectedShop: Shop
    private lateinit var shopViewModel: ShopViewModel
    private val alcoObject = detailsFragment.alcoObject

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = false

        shopViewModel = ViewModelProvider(requireActivity()).get(ShopViewModel::class.java)
        shopViewModel.getData().observe(viewLifecycleOwner, { map ->
            val mutableMap = map.toMutableMap()
            alcoObject.shopToPrice.keys.forEach {
                mutableMap.remove(it)
            }
            setSpinner(mutableMap)
        })

        var pickFromList = true
        val requestViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[RequestViewModel::class.java]

        with(binding) {
            if (shopId != null) {
                avSubmitHeaderTV.text = getString(R.string.avsubmit_edit)
                val shops = shopViewModel.getData().value
                pickFromList = false
                avSubmitSpinnerCV.visibility = View.GONE
                avSubmitShopCV.visibility = View.VISIBLE
                avSubmitMissingShopBT.visibility = View.GONE
                avSubmitShopET.isEnabled = false
                avSubmitShopET.setText(shops?.get(shopId)?.name ?: "Błąd")
            }

            avSubmitMissingShopBT.setOnClickListener {
                if (pickFromList) {
                    avSubmitMissingShopBT.text = getString(R.string.avsubmit_shoppresent)
                    avSubmitSpinnerCV.visibility = View.GONE
                    avSubmitShopCV.visibility = View.VISIBLE
                    pickFromList = !pickFromList
                } else {
                    avSubmitMissingShopBT.text = getString(R.string.avsubmit_shopmissing)
                    avSubmitShopCV.visibility = View.GONE
                    avSubmitSpinnerCV.visibility = View.VISIBLE
                    pickFromList = !pickFromList
                }
            }
            avSubmitShopET.addTextChangedListener {
                avSubmitErrorTV.visibility = View.GONE
                var correct = true
                it?.forEach { char ->
                    if (!getString(R.string.polishalphabet).contains(char)) {
                        correct = false
                    }
                }
                if (!correct) {
                    avSubmitShopETL.apply {
                        isErrorEnabled = true
                    }
                } else {
                    avSubmitShopETL.isErrorEnabled = false
                }
            }
            avSubmitPriceET.addTextChangedListener {
                avSubmitErrorTV.visibility = View.GONE
                val decimalcount = if (it?.contains(".") == true) {
                    it.length.minus((it.indexOf("."))).minus(1)
                } else {
                    0
                }

                if (it.isNullOrEmpty() ||
                    decimalcount > 2 ||
                    it.indexOf(".") == 0 ||
                    it.toString().toFloat() <= 0f
                ) {
                    avSubmitPriceETL.apply {
                        isErrorEnabled = true
                    }
                } else {
                    avSubmitPriceETL.apply {
                        isErrorEnabled = false
                    }
                }
            }
            avSubmitApplyBT.setOnClickListener { button ->
                val inputToCheck = mutableListOf(avSubmitPriceETL)
                val user = FirebaseAuth.getInstance().currentUser

                if (!pickFromList) inputToCheck.add(avSubmitShopETL)
                var correctInput = true
                inputToCheck.forEach { til ->
                    if (til.isErrorEnabled || til.editText?.text.isNullOrBlank()) {
                        correctInput = false
                        avSubmitErrorTV.visibility = View.VISIBLE
                    }
                }

                if (user == null) {
                    Toast.makeText(
                        requireContext(),
                        R.string.err_notloggedin, Toast.LENGTH_LONG
                    ).show()
                } else if (!correctInput) {
                    Toast.makeText(
                        requireContext(),
                        R.string.err_shopnameWrongInput, Toast.LENGTH_LONG
                    ).show()
                } else {
                    avSubmitApplyBT.text = ""
                    avSubmitPB.visibility = View.VISIBLE
                    button.isEnabled = false

                    //Perform connection check and upload if connected
                    ConnectionCheck.perform(object : RequestListener {
                        override fun onComplete(resultCode: Int) {
                            if (resultCode == RequestListener.NETWORK_ERR) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.err_no_connection),
                                    Toast.LENGTH_LONG
                                ).show()
                                avSubmitApplyBT.text = getString(R.string.submit)
                                avSubmitPB.visibility = View.GONE
                                button.isEnabled = true
                            } else {
                                val id = if (pickFromList) {
                                    selectedShop.id
                                } else shopId
                                val name = if (pickFromList) {
                                    selectedShop.name
                                } else {
                                    avSubmitShopET.text.toString()
                                }

                                val request = AvailabilityRequest(
                                    user.uid,
                                    alcoObject.id.toLong(),
                                    id,
                                    name,
                                    id == null,
                                    shopId != null,
                                    avSubmitPriceET.text.toString().toDouble(),
                                    Timestamp.now(),
                                    Request.RequestState.PENDING,
                                    null,
                                    null,
                                    null
                                )

                                requestViewModel.uploadAvailability(
                                    request,
                                    object : RequestListener {
                                        override fun onComplete(resultCode: Int) {
                                            avSubmitApplyBT.text = getString(R.string.submit)
                                            avSubmitPB.visibility = View.GONE
                                            if (resultCode == RequestListener.SUCCESS) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.request_success),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                dismiss()
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.toast_error),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                button.isEnabled = true
                                            }
                                        }
                                    })
                            }
                        }
                    })
                }
            }
            avSubmitCancelBT.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setSpinner(list: Map<Int, Shop>) {
        val shopList = arrayListOf<Shop>()
        val namesList = arrayListOf<String>()
        val spinner = binding.avSubmitNameSPINNER
        list.forEach {
            shopList.add(it.value)
            namesList.add(it.value.name)
        }

        spinner.adapter = ArrayAdapter(
            requireContext(), R.layout.spinner1, namesList
        )
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedShop = shopList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

}