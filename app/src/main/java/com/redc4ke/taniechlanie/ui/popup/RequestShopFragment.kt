package com.redc4ke.taniechlanie.ui.popup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.viewmodels.RequestViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentRequestShopBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class RequestShopFragment(
    private val shopViewModel: ShopViewModel,
    private val requestViewModel: RequestViewModel
) :
    BaseDialogFragment<FragmentRequestShopBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestShopBinding
        get() = FragmentRequestShopBinding::inflate
    private var selectedShop: String? = null
    private var shopNameList = mutableListOf<String>()

    override fun onStart() {
        super.onStart()

        var shopMap: Map<Int, Shop>
        shopViewModel.getData().observe(viewLifecycleOwner, {
            shopMap = it
            shopNameList.clear()
            shopMap.forEach { (_, u) ->
                shopNameList.add(u.name)
            }
            setSpinner(shopNameList)
        })

        with(binding) {
            requestShopCancelBT.setOnClickListener {
                dismiss()
            }
            requestShopChangeBT.setOnClickListener {
                if (requestShopSpinnerCV.isVisible) {
                    requestShopSpinnerCV.visibility = View.GONE
                    requestShopTIL.visibility = View.VISIBLE
                    requestShop1TV.visibility = View.GONE
                    requestShopChangeBT.setText(R.string.request_returntospinner)
                } else {
                    requestShopSpinnerCV.visibility = View.VISIBLE
                    requestShopTIL.visibility = View.GONE
                    requestShop1TV.visibility = View.VISIBLE
                    requestShopChangeBT.setText(R.string.request_shopmissingbt)
                }
            }
            requestShopET.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    requestShopTIL.error = getString(R.string.err_emptyField)
                } else {
                    requestShopTIL.error = when (true) {
                        it.length < 3 -> getString(R.string.err_tooShort, "3")
                        it.length > 20 -> getString(R.string.err_tooLongShortened, "20")
                        else -> null
                    }
                }
            }
            requestShopSaveBT.setOnClickListener {
                if (requestShopTIL.isVisible && requestShopTIL.error == null
                    && !requestShopET.text.isNullOrEmpty()) {
                    requestViewModel.setShop(requestShopET.text.toString())
                    dismiss()
                } else if (requestShopSpinnerCV.isVisible && selectedShop != null) {
                    requestViewModel.setShop(selectedShop!!)
                    dismiss()
                }
            }
        }
    }

    private fun setSpinner(shopNameList: List<String>) {

        binding.requestShopSPINNER.adapter =
            ArrayAdapter(requireContext(), R.layout.spinner1, shopNameList)

        binding.requestShopSPINNER.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedShop = shopNameList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
    }

}