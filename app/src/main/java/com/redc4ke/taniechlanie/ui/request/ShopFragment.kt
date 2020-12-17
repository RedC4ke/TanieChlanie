package com.redc4ke.taniechlanie.ui.request

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.ShopViewModel
import com.redc4ke.taniechlanie.data.request.SelectedShopsViewModel
import com.redc4ke.taniechlanie.data.request.ShopListAdapter
import com.redc4ke.taniechlanie.ui.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_shop.*

class ShopFragment : BaseFragment() {

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
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_shop, container, false)

        setHasOptionsMenu(true)

        return root
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
        val recyclerView = shopList_RV

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        shopViewModel.getData().observe(viewLifecycleOwner, Observer {
            val adapter = ShopListAdapter(it, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        })
    }

}