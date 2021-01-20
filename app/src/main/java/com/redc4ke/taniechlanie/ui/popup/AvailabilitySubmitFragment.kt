package com.redc4ke.taniechlanie.ui.popup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.ShopViewModel
import com.redc4ke.taniechlanie.ui.BaseFragment
import com.redc4ke.taniechlanie.ui.menu.details.DetailsFragment
import com.redc4ke.taniechlanie.ui.menu.details.Sheet2Fragment
import kotlinx.android.synthetic.main.fragment_availability_submit.*

class AvailabilitySubmitFragment(detailsFragment: DetailsFragment) : DialogFragment() {

    private lateinit var selectedShop: Shop
    private val alcoObject = detailsFragment.alcoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
                R.layout.fragment_availability_submit, container, false)
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
        arrayListOf(av_submit_apply_BT, av_submit_missingShop_BT).forEach {
            it.setOnClickListener {
                Toast.makeText(requireContext(), "Funkcja w budowie!", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun setSpinner(list: Map<Int, Shop>) {
        val shopList = arrayListOf<Shop>()
        val namesList = arrayListOf<String>()
        val spinner = av_submit_name_SPINNER
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