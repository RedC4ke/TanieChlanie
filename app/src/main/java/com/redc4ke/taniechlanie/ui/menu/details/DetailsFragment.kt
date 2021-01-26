package com.redc4ke.taniechlanie.ui.menu.details

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
import com.redc4ke.taniechlanie.databinding.FragmentDetailsBinding
import com.redc4ke.taniechlanie.ui.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.menu.MenuFragment
import java.io.File

class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailsBinding
        get() = FragmentDetailsBinding::inflate
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

        val root = super.onCreateView(inflater, container, savedInstanceState)
        binding.nameDetails.text = alcoObject.name

        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (alcoObject.photo != null){
            val imageFile = File(requireContext().filesDir, alcoObject.name + ".jpg")

            if (File(requireContext().filesDir, alcoObject.name + ".jpg").exists()) {
                binding.imageDetails.setImageBitmap(BitmapFactory.decodeFile(imageFile.path))
            } else {
                val imageRef = mainActivity.storage.getReferenceFromUrl(alcoObject.photo!!)
                imageRef.getFile(imageFile).addOnSuccessListener {
                    binding.imageDetails.setImageBitmap(BitmapFactory.decodeFile(imageFile.path))
                    Log.d("image","success ${imageFile.path}")
                }.addOnFailureListener {
                    Log.d("image","$it")
                }
            }
        }

    }
}