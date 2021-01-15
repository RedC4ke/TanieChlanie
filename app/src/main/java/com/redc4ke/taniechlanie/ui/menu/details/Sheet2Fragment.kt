package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.ShopViewModel
import com.redc4ke.taniechlanie.data.menu.DetailsShopAdapter
import com.redc4ke.taniechlanie.ui.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.AvailabilitySubmitFragment
import kotlinx.android.synthetic.main.fragment_sheet2.*
import java.util.*

class Sheet2Fragment : BaseFragment() {

    lateinit var shopViewModel: ShopViewModel
    lateinit var detailsFragment: DetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)

        detailsFragment = requireParentFragment().parentFragment as DetailsFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sheet2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recyclerView = sheet2_RV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        shopViewModel = requireActivity().run {
            ViewModelProvider(this).get(ShopViewModel::class.java)
        }

        shopViewModel.getData().observe(viewLifecycleOwner, {
            val adapter = DetailsShopAdapter(detailsFragment.alcoObject, it)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            setupSearchBar(sheet2_ET, detailsFragment.alcoObject, it, adapter)
        })


        sheet2_returnBT.setOnClickListener {
            findNavController().navigate(R.id.action_sheet2_dest_to_sheet1_dest)
        }

        sheet2_add_BT.setOnClickListener {
            AvailabilitySubmitFragment(detailsFragment)
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
                alcoObject.shop.forEach {
                    if (shopList[it] != null) {
                        available[it] = shopList[it] as Shop
                    }
                }

                if (s.toString() != "") {
                    for (item in available) {
                        if (item.value.name.toLowerCase(Locale.ROOT)
                                        .contains(s.toString().toLowerCase(Locale.ROOT))) {
                            filtered.add(item.key)
                        }
                    }
                    adapter.update(filtered)
                } else {
                    adapter.update(alcoObject.shop)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}