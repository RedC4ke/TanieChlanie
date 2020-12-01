package com.redc4ke.taniechlanie.ui.request

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.request.ShopListAdapter
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.setTransitions

class ShopFragment : Fragment() {

    var hasImage = false
    val selectedShops = mutableListOf<Int>()
    lateinit var alcoObject: AlcoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alcoObject = arguments?.getSerializable("AlcoObject") as AlcoObject
        hasImage = arguments?.getBoolean("hasImage")!!

        setTransitions(this, R.transition.slide_from_right, R.transition.slide_to_right)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_shop, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.shopList_RV)

        setHasOptionsMenu(true)

        val activity = requireActivity() as MainActivity

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ShopListAdapter(activity.shopList, this)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.acceptbt_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_forward) {
            alcoObject.shop = selectedShops as ArrayList<Int>
            val directions = ShopFragmentDirections.toSummaryFragment(alcoObject, hasImage)
            setTransitions(this, R.transition.slide_from_left, R.transition.slide_to_left)
            findNavController().navigate(directions)
        } else {
            setTransitions(this, R.transition.slide_from_right, R.transition.slide_to_right)
        }
        return super.onOptionsItemSelected(item)
    }
}