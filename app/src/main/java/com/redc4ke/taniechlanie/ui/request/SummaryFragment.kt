package com.redc4ke.taniechlanie.ui.request

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentSummaryBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import java.io.File
import java.util.*

class SummaryFragment : BaseFragment<FragmentSummaryBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSummaryBinding
        get() = FragmentSummaryBinding::inflate
    private var hasImage = false
    private lateinit var mainActivity: MainActivity
    private lateinit var alcoObject: AlcoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)

        mainActivity = requireActivity() as MainActivity
        hasImage = arguments?.getBoolean("hasImage")!!
        alcoObject = arguments?.getSerializable("AlcoObject") as AlcoObject

        Log.d("huj", alcoObject.voltage.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryViewModel: CategoryViewModel
        val shopViewModel: ShopViewModel
        requireActivity().run {
            categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
            shopViewModel = ViewModelProvider(this).get(ShopViewModel::class.java)
        }

        var categories: Map<Int, Category>
        categoryViewModel.get().observe(viewLifecycleOwner, {
            binding.summaryCategoriesTV.text = ""
            categories = it
            alcoObject.categories.forEach {position ->
                val catName = categories[position]?.name
                if (position != alcoObject.categories.last())
                    binding.summaryCategoriesTV.append("$catName, ")
                else
                    binding.summaryCategoriesTV.append(catName)
            }
        })

        var shops: Map<Int, Shop>
        shopViewModel.getData().observe(viewLifecycleOwner, {
            binding.summaryShopsTV.text = ""
            shops = it
            alcoObject.shop.forEach {position ->
                val shopName = shops[position]?.name
                if (position != alcoObject.shop.last()) binding.summaryShopsTV.append("$shopName, ")
                else binding.summaryShopsTV.append(shopName)
            }
        })

        binding.summaryNameTV.text = alcoObject.name
        binding.summaryPriceTV.text = priceString(alcoObject, this)
        binding.summaryVolumeTV.text = volumeString(alcoObject, this)
        binding.summaryVoltageTV.text = voltageString(alcoObject, this)

        binding.reqUploadBT.setOnClickListener {
            upload()
        }
    }

    private fun uploadImageTask(storageRef: StorageReference): UploadTask {
        val imageUri = Uri.fromFile(
            File(requireContext().cacheDir, "upload.jpg"))
        return storageRef.putFile(imageUri)
    }

    private fun upload() {
        var photoUrl: String?
        val storageRef = mainActivity.storage.reference
                .child("pendingPhotos")
                .child(UUID.randomUUID().toString())
        if (hasImage) {
            uploadImageTask(storageRef)
                .addOnSuccessListener {
                    it.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {uri ->
                                photoUrl = uri.toString()
                                uploadData(photoUrl, storageRef)
                            }
                            .addOnFailureListener {
                                storageRef.delete()
                                onFailure()
                            }
                }
                .addOnFailureListener {
                    storageRef.delete()
                    onFailure()
                }
        } else {
            uploadData(null, null)
        }

    }

    private fun uploadData(photoUrl: String?, storageRef: StorageReference?) {
        val pendingRef = mainActivity.database.collection("pending")
        val data = hashMapOf(
                "name" to alcoObject.name,
                "price" to mapOf("min" to alcoObject.price.toDouble()),
                "volume" to alcoObject.volume,
                "voltage" to alcoObject.voltage.toDouble(),
                "categories" to alcoObject.categories,
                "shop" to alcoObject.shop,
                "photo" to photoUrl
        )

        pendingRef.add(data)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    storageRef?.delete()
                    onFailure()
                }
    }

    private fun onSuccess() {
        setTransitions(R.transition.slide_from_right, R.transition.slide_to_left)
        findNavController().navigate(SummaryFragmentDirections.toListDest())
        Toast.makeText(mainActivity.applicationContext,
            getString(R.string.success_summary), Toast.LENGTH_LONG).show()
    }

    private  fun onFailure() {
        Toast.makeText(requireContext(),
            getString(R.string.failure_summary), Toast.LENGTH_LONG).show()
    }

}