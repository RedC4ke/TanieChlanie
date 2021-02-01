package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.databinding.FragmentSheet1Binding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity

class Sheet1Fragment : BaseFragment<FragmentSheet1Binding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSheet1Binding
        get() = FragmentSheet1Binding::inflate
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

        val root = super.onCreateView(inflater, container, savedInstanceState)

        binding.detailsPriceTV.text = getString(R.string.suff_price,
            String.format("%.2f", alcoObject.price))
        binding.detailsValueTV.text = valueString(alcoObject, this)
        binding.detailsVolumeTV.text = volumeString(alcoObject, this)
        binding.detailsVoltageTV.text = voltageString(alcoObject, this)

        mainActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.catlist_fragment, CatListFragment(alcoObject))
            .commit()

        binding.sheet1ShoplistBT.setOnClickListener {
            findNavController().navigate(R.id.action_sheet1_dest_to_sheet2_dest)
        }

        return root
    }

}