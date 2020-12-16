package com.redc4ke.taniechlanie.ui.menu

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialContainerTransform
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details.view.*
import kotlinx.android.synthetic.main.row_alcohol.view.*
import kotlinx.android.synthetic.main.sheet_details.view.*
import java.io.File

class DetailsFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var parentFrag: MenuFragment
    lateinit var alcoObject: AlcoObject

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

        rootView.name_details.text = alcoObject.name

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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