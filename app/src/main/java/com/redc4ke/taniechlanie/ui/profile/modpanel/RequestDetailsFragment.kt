package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.data.priceString
import com.redc4ke.taniechlanie.data.setImage
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.Request
import com.redc4ke.taniechlanie.data.voltageString
import com.redc4ke.taniechlanie.data.volumeString
import com.redc4ke.taniechlanie.databinding.FragmentRequestDetailsBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.DeclinationReasonFragment
import java.math.BigDecimal

class RequestDetailsFragment() :
    BaseFragment<FragmentRequestDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestDetailsBinding
        get() = FragmentRequestDetailsBinding::inflate
    private lateinit var request: Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        request = arguments?.getSerializable("request") as Request
    }

    override fun onStart() {
        super.onStart()

        val categoryViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[CategoryViewModel::class.java]
        val categoryNames =
            categoryViewModel.getWithMajorFirst(request.categories ?: listOf()).map { it?.name }

        if (request.photo != null)
            setImage(
                requireContext(),
                request.id.toString(),
                binding.reqDetailsPhotoIV,
                Uri.parse(request.photo)
            )

        with(binding) {
            reqDetailsNameTV.text = request.name
            var catNamesText = ""
            categoryNames.forEach {
                catNamesText += it
                if (it != categoryNames.last())
                    catNamesText += ", "
            }
            reqDetailsCategoryTV.text = catNamesText
            reqDetailsVolumeTV.text = volumeString(request.volume ?: 0, requireContext())
            reqDetailsVoltageTV.text =
                voltageString(request.voltage ?: BigDecimal.ZERO, requireContext())
            reqDetailsShopTV.text = request.shop?.values?.toList()?.get(0) ?: ""
            if (request.shopIsNew == true)
                reqDetailsShopTV.setTextColor(Color.GREEN)
            reqDetailsPriceTV.text =
                priceString(request.price?.toBigDecimal() ?: BigDecimal.ZERO, requireContext())

            reqDetailsDeleteBT.setOnClickListener {
                DeclinationReasonFragment(request).show(
                    parentFragmentManager,
                    "declinationReason"
                )
            }
        }
    }

}