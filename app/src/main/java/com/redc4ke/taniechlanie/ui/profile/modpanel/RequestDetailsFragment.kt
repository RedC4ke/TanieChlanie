package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectRequest
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentRequestDetailsBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.DeclinationReasonFragment
import java.math.BigDecimal

class RequestDetailsFragment :
    BaseFragment<FragmentRequestDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestDetailsBinding
        get() = FragmentRequestDetailsBinding::inflate
    private lateinit var request: AlcoObjectRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        request = arguments?.getSerializable("request") as AlcoObjectRequest
    }

    override fun onStart() {
        super.onStart()

        val viewModelProvider = ViewModelProvider(requireActivity() as MainActivity)
        val categoryViewModel = viewModelProvider[CategoryViewModel::class.java]
        val modpanelViewModel = viewModelProvider[ModpanelViewModel::class.java]
        val shopViewModel = viewModelProvider[ShopViewModel::class.java]
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
            reqDetailsShopTV.text = request.shop?.second ?: ""
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

            reqDetailsAcceptBT.setOnClickListener {
                reqDetailsPB.visibility = View.VISIBLE
                reqDetailsAcceptBT.text = ""
                modpanelViewModel.acceptNewBooze(
                    request, shopViewModel, object: FirebaseListener {
                        override fun onComplete(resultCode: Int) {
                            val toastText = when (resultCode) {
                                FirebaseListener.SUCCESS -> getString(R.string.request_accepted).also {
                                    requireActivity().onBackPressed()
                                }
                                FirebaseListener.NOT_LOGGED_IN -> getString(R.string.err_notloggedin)
                                else -> getString(R.string.toast_error)
                            }
                            Toast.makeText(requireContext(), toastText, Toast.LENGTH_LONG).show()
                            reqDetailsAcceptBT.text = getString(R.string.submit)
                            reqDetailsPB.visibility = View.GONE
                        }
                    }
                )
            }
        }
    }

}