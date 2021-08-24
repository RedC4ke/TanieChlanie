package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.data.menu.DetailsShopAdapter
import com.redc4ke.taniechlanie.databinding.FragmentSheet2Binding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.AvailabilitySubmitFragment
import java.util.*
import kotlin.collections.ArrayList

class Sheet2Fragment : BaseFragment<FragmentSheet2Binding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSheet2Binding
        get() = FragmentSheet2Binding::inflate
    lateinit var shopViewModel: ShopViewModel
    lateinit var detailsFragment: DetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)

        detailsFragment = requireParentFragment().parentFragment as DetailsFragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recyclerView = binding.sheet2RV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        shopViewModel = requireActivity().run {
            ViewModelProvider(this).get(ShopViewModel::class.java)
        }

        shopViewModel.getData().observe(viewLifecycleOwner, {
            val adapter = DetailsShopAdapter(detailsFragment.alcoObject, it, detailsFragment)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            setupSearchBar(binding.sheet2ET, detailsFragment.alcoObject, it, adapter)
        })


        binding.sheet2ReturnBT.setOnClickListener {
            findNavController().navigate(R.id.action_sheet2_dest_to_sheet1_dest)
        }

        binding.sheet2AddBT.setOnClickListener {
            AvailabilitySubmitFragment(detailsFragment, null)
                    .show(parentFragmentManager, "av_submit")
        }
    }

    private fun setupSearchBar(
            editText: EditText,
            alcoObject: AlcoObject,
            shopList: Map<Int, Shop>,
            adapter: DetailsShopAdapter) {
        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = arrayListOf<Int>()
                val available = mutableMapOf<Int, Shop>()
                alcoObject.shopToPrice.keys.forEach {
                    if (shopList[it] != null) {
                        available[it] = shopList[it] as Shop
                    }
                }

                if (s.toString() != "") {
                    for (item in available) {
                        if (item.value.name.lowercase(Locale.ROOT)
                                        .contains(s.toString().lowercase(Locale.ROOT))) {
                            filtered.add(item.key)
                        }
                    }
                    adapter.update(filtered)
                } else {
                    adapter.update(alcoObject.shopToPrice.keys.toList() as ArrayList<Int>)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}