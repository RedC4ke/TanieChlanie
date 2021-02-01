package com.redc4ke.taniechlanie.ui.request

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.data.request.SelectedShopsViewModel
import com.redc4ke.taniechlanie.data.request.ShopListAdapter
import com.redc4ke.taniechlanie.databinding.FragmentShopBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ShopFragment : BaseFragment<FragmentShopBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentShopBinding
        get() = FragmentShopBinding::inflate
    lateinit var selectedShopsViewModel: SelectedShopsViewModel
    private var hasImage = false
    private lateinit var alcoObject: AlcoObject
    private lateinit var shopViewModel: ShopViewModel
    private lateinit var selectedShops: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alcoObject = arguments?.getSerializable("AlcoObject") as AlcoObject
        hasImage = arguments?.getBoolean("hasImage")!!

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.acceptbt_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_forward) {

            alcoObject.shop = selectedShops
            val directions = ShopFragmentDirections.toSummaryFragment(alcoObject, hasImage)
            setTransitions(R.transition.slide_from_left, R.transition.slide_to_left)
            findNavController().navigate(directions)
        } else {
            setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        selectedShopsViewModel = ViewModelProvider(this)
                .get(SelectedShopsViewModel::class.java)
        selectedShopsViewModel.get().observe(viewLifecycleOwner, Observer {
            selectedShops = it
        })


        shopViewModel = requireActivity().run {
            ViewModelProvider(this).get(ShopViewModel::class.java)
        }
        val recyclerView = binding.shopListRV

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        shopViewModel.getData().observe(viewLifecycleOwner, Observer {
            val adapter = ShopListAdapter(it, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        })
    }

}