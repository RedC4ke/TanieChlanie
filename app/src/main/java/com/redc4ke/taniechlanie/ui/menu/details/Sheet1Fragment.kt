package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.ui.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_sheet1.view.*

class Sheet1Fragment : BaseFragment() {

    private lateinit var alcoObject: AlcoObject
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_left, R.transition.slide_to_left)

        alcoObject = (requireParentFragment().parentFragment as DetailsFragment).alcoObject
        mainActivity = requireActivity() as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_sheet1, container, false)

        rootView.details_priceTV.text = getString(R.string.suff_price,
            String.format("%.2f", alcoObject.price))

        rootView.details_valueTV.text = valueString(alcoObject, this)
        rootView.details_volumeTV.text = volumeString(alcoObject, this)
        rootView.details_voltageTV.text = voltageString(alcoObject, this)

        mainActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.catlist_fragment, CatListFragment(alcoObject))
            .commit()

        rootView.sheet1_shoplistBT.setOnClickListener {
            findNavController().navigate(R.id.action_sheet1_dest_to_sheet2_dest)
        }

        return rootView
    }

}