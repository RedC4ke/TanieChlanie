package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
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
    private val actionType: Int,
    private val itemId: Long,
    private val listener: RequestListener
) : BaseDialogFragment<FragmentSpinnerBinding>() {

    companion object ActionType {
        const val ADD = 0
        const val REMOVE = 1
        const val CHANGE_MAJOR = 2
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSpinnerBinding
        get() = FragmentSpinnerBinding::inflate
    private val existingCategories: MutableList<Int> = mutableListOf()
    private var spinnerMap = mapOf<Int, Category>()
    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //This avoids the weird bug where parent layout params get removed
        dialog?.window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        val provider = ViewModelProvider(requireActivity() as MainActivity)
        val categoryViewModel = provider[CategoryViewModel::class.java]
        val alcoObjectViewModel = provider[AlcoObjectViewModel::class.java]

        binding.spinnerHeaderTV.text = when (actionType) {
            ADD -> getString(R.string.add_category)
            REMOVE -> getString(R.string.remove_category)
            else -> getString(R.string.change_major_category)
        }
        binding.spinnerSPINNER.prompt = getString(R.string.chose_category)

        alcoObjectViewModel.getAll().observe(viewLifecycleOwner, { _ ->
            existingCategories.addAll(
                alcoObjectViewModel.get(itemId)?.categories ?: mutableListOf()
            )
        })

        categoryViewModel.getAll().observe(viewLifecycleOwner, { catMap ->
            val existingCategoriesFilter = mutableListOf<Category>()

            existingCategories.forEach {
                existingCategoriesFilter.add(catMap[it] ?: return@forEach)
            }

            spinnerMap = catMap.filter {
                when (actionType) {
                    ADD -> !(it.value.major) && (it.value !in existingCategoriesFilter)
                    REMOVE -> !(it.value.major) && (it.value in existingCategoriesFilter)
                    else -> (it.value.major) && (it.value !in existingCategoriesFilter)
                }
            }

            binding.spinnerSPINNER.adapter =
                ArrayAdapter(requireContext(), R.layout.spinner1, spinnerMap.values.map { it.name })

            binding.spinnerCancelBT.setOnClickListener {
                dismiss()
            }
        })

        binding.spinnerSPINNER.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedCategory =
                    spinnerMap.values.filter { it.name == spinnerMap.values.toList()[position].name }[0]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

}