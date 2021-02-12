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
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentAvailabilitySubmitBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment
import com.redc4ke.taniechlanie.ui.menu.details.DetailsFragment

class AvailabilitySubmitFragment(detailsFragment: DetailsFragment) :
    BaseDialogFragment<FragmentAvailabilitySubmitBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) ->
        FragmentAvailabilitySubmitBinding
            get() = FragmentAvailabilitySubmitBinding::inflate
    private lateinit var selectedShop: Shop
    private val alcoObject = detailsFragment.alcoObject

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shopVM = ViewModelProvider(requireActivity()).get(ShopViewModel::class.java)
        shopVM.getData().observe(viewLifecycleOwner, {map ->
            val mutableMap = map.toMutableMap()
            alcoObject.shop.forEach {
                mutableMap.remove(it)
            }
            setSpinner(mutableMap)
        })

        //TEMPORARY
        arrayListOf(binding.avSubmitApplyBT, binding.avSubmitMissingShopBT).forEach {
            it.setOnClickListener {
                Toast.makeText(requireContext(), "Funkcja w budowie!", Toast.LENGTH_SHORT)
                        .show()
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
                requireContext(), R.layout.spinner1, namesList)
        spinner.onItemSelectedListener = object:
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedShop = shopList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

}