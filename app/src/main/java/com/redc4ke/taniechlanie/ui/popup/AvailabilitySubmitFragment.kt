package com.redc4ke.taniechlanie.ui.popup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.ShopViewModel
import com.redc4ke.taniechlanie.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_availability_submit.*

class AvailabilitySubmitFragment : DialogFragment() {

    private lateinit var selectedShop: Shop

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
        return inflater.inflate(R.layout.fragment_availability_submit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shopVM = ViewModelProvider(requireActivity()).get(ShopViewModel::class.java)
        shopVM.getData().observe(viewLifecycleOwner, {
            setSpinner(it)
        })

    }

    fun setSpinner(list: Map<Int, Shop>) {
        val shopList = arrayListOf<Shop>()
        list.forEach {
            shopList.add(it.value)
        }
        av_submit_name_SPINNER.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, shopList)
    }

}