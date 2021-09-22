package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.databinding.FragmentSpinnerBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class CategoryAddRemoveFragment(
    private val categoryAdd: Boolean,
    private val listener: RequestListener
) : BaseDialogFragment<FragmentSpinnerBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSpinnerBinding
        get() = FragmentSpinnerBinding::inflate
    private var minorMap = mapOf<Int, Category>()
    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewModelProvider(requireActivity() as MainActivity)
        val categoryViewModel = provider[CategoryViewModel::class.java]
        val alcoObjectViewModel = provider[AlcoObjectViewModel::class.java]

        binding.spinnerHeaderTV.text = if (categoryAdd) {
            getString(R.string.add_category)
        } else {
            getString(R.string.remove_category)
        }

        categoryViewModel.getAll().observe(viewLifecycleOwner, { catMap ->
            minorMap = catMap.filter { !it.value.major }
            binding.spinnerSPINNER.adapter =
                ArrayAdapter(requireContext(), R.layout.spinner1, minorMap.values.map { it.name })
        })

        binding.spinnerSPINNER.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedCategory =
                    minorMap.values.filter { it.name == minorMap.values.toList()[position].name }[0]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

}