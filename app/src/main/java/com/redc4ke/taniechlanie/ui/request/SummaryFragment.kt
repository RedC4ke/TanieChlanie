package com.redc4ke.taniechlanie.ui.request

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.setTransitions
import kotlinx.android.synthetic.main.fragment_summary.*
import java.io.File
import java.util.*

class SummaryFragment : Fragment() {

    private var hasImage = false
    private lateinit var mainActivity: MainActivity
    private lateinit var alcoObject: AlcoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(this, R.transition.slide_from_right, R.transition.slide_to_right)

        hasImage = arguments?.getBoolean("hasImage")!!
        mainActivity = requireActivity() as MainActivity
        alcoObject = arguments?.getSerializable("AlcoObject") as AlcoObject
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        summary_name_TV.text = alcoObject.name
        summary_price_TV.text = getString(R.string.suff_price,
                String.format("%.2f", alcoObject.minPrice))
        summary_volume_TV.text = getString(R.string.suff_volume,
                alcoObject.volume!!.toString())
        summary_voltage_TV.text = getString(R.string.suff_voltage,
                alcoObject.voltage!!.toBigDecimal().stripTrailingZeros().toPlainString())
        alcoObject.categories!!.forEach {
            val catName = mainActivity.categories.get(it)?.name
            if (it != alcoObject.categories!!.last()) summary_categories_TV.append("$catName, ")
            else summary_categories_TV.append(catName)
        }
        alcoObject.shop?.forEach {
            val shopName = mainActivity.shopList[it-1].name
            if (it != alcoObject.shop?.last()) summary_shops_TV.append("$shopName, ")
            else summary_shops_TV.append(shopName)
        }

        req_upload_BT.setOnClickListener {
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
                "price" to mapOf("min" to alcoObject.minPrice!!),
                "volume" to alcoObject.volume,
                "voltage" to alcoObject.voltage,
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
        setTransitions(this, R.transition.slide_from_right, R.transition.slide_to_left)
        findNavController().navigate(SummaryFragmentDirections.toListDest())
        Toast.makeText(mainActivity.applicationContext,
            getString(R.string.success_summary), Toast.LENGTH_LONG).show()
    }

    private  fun onFailure() {
        Toast.makeText(requireContext(),
            getString(R.string.failure_summary), Toast.LENGTH_LONG).show()
    }

}