package com.redc4ke.taniechlanie.ui.menu

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialContainerTransform
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.menu.DetailsCategoryAdapter
import com.redc4ke.taniechlanie.data.menu.DetailsShopAdapter
import com.redc4ke.taniechlanie.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.sheet_details.*
import java.io.File

class DetailsFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var parentFrag: MenuFragment
    private lateinit var alcoObject: AlcoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        parentFrag = arguments?.getSerializable("MenuFragment") as MenuFragment
        alcoObject = arguments?.getSerializable("alcoObject") as AlcoObject

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHostFragment
            duration = 500
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHostFragment
            duration = 200
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_details, container, false)
        val recyclerViewShops = rootView.findViewById<RecyclerView>(R.id.shop_details)
        val recyclerViewCategory = rootView.findViewById<RecyclerView>(R.id.category_details)

        val shopAdapter = DetailsShopAdapter(mainActivity.shopList, alcoObject.shop)
        recyclerViewShops.adapter = shopAdapter
        recyclerViewShops.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false)

        val categoryAdapter = DetailsCategoryAdapter(this, alcoObject.categories)
        recyclerViewCategory.adapter = categoryAdapter
        val spanCount = when (alcoObject.categories?.size) {
            1 -> 1
            else -> 2
        }
        recyclerViewCategory.layoutManager = GridLayoutManager(
                context,
                spanCount,
                GridLayoutManager.HORIZONTAL,
                false)

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name_details.text = alcoObject.name
        if (alcoObject.minPrice != null)
            price_details.text = getString(R.string.suff_price,
            String.format("%.2f", alcoObject.minPrice))
        if (alcoObject.volume != null)
            volume_details.text = (alcoObject.volume.toString() + "ml")
        if (alcoObject.voltage != null)
            voltage_details.text = (alcoObject.voltage?.toInt().toString() + "%")
        if (alcoObject.volume != null
            && alcoObject.voltage != null
            && alcoObject.minPrice != null) {
            mr_details.text = (
                    alcoObject.voltage!!.toInt()
                            * alcoObject.volume!!.toInt()
                            / alcoObject.minPrice!!.toInt()
                    ).toString()
        }

        if (alcoObject.photo != null){
            val imageFile = File(requireContext().filesDir, alcoObject.name + ".jpg")

            if (File(requireContext().filesDir, alcoObject.name + ".jpg").exists()) {
                image_details.setImageBitmap(BitmapFactory.decodeFile(imageFile.path))
            } else {
                val imageRef = mainActivity.storage.getReferenceFromUrl(alcoObject.photo!!)
                imageRef.getFile(imageFile).addOnSuccessListener {
                    image_details.setImageBitmap(BitmapFactory.decodeFile(imageFile.path))
                    Log.d("image","success ${imageFile.path}")
                }.addOnFailureListener {
                    Log.d("image","$it")
                }
            }
        }

    }



}