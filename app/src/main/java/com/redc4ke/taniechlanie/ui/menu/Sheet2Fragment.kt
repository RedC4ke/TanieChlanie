package com.redc4ke.taniechlanie.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ShopViewModel
import com.redc4ke.taniechlanie.data.menu.DetailsShopAdapter
import com.redc4ke.taniechlanie.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_sheet2.*

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
        })


        sheet2_returnBT.setOnClickListener {
            findNavController().navigate(R.id.action_sheet2_dest_to_sheet1_dest)
        }
    }
}