package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ConnectionCheck
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.priceString
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.AvailabilityRequest
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentAvailabilityDetailsBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.DeclinationReasonFragment
import java.math.BigDecimal

class AvailabilityDetailsFragment : BaseFragment<FragmentAvailabilityDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAvailabilityDetailsBinding
        get() = FragmentAvailabilityDetailsBinding::inflate
    private lateinit var request: AvailabilityRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        request = arguments?.getSerializable("request") as AvailabilityRequest
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewModelProvider(requireActivity() as MainActivity)
        val alcoObjectViewModel = provider[AlcoObjectViewModel::class.java]
        val modpanelViewModel = provider[ModpanelViewModel::class.java]
        val shopViewModel = provider[ShopViewModel::class.java]
        val alcoObject = alcoObjectViewModel.get(request.alcoObjectId)

        with(binding) {
            avDetailsNameTV.text = alcoObject?.name
            avDetailsShopTV.text = request.shopName

            if (request.shopIsNew) {
                avDetailsShopTV.setTextColor(Color.GREEN)
            }

            if (alcoObject != null) {
                avDetailsOldpriceTV.text = priceString(
                    alcoObject.shopToPrice[request.shopId] ?: BigDecimal.ZERO,
                    requireContext()
                )
                if (alcoObject.photo != null) {
                    Glide.with(requireContext()).load(alcoObject.photo).into(avDetailsPhotoIV)
                }
            }

            avDetailsNewpriceTV.text = priceString(request.price.toBigDecimal(), requireContext())

            avDetailsDeleteBT.setOnClickListener {
                DeclinationReasonFragment(request).show(
                    parentFragmentManager,
                    "declinationReason"
                )
            }

            avDetailsAcceptBT.setOnClickListener { button ->
                button.isEnabled = false
                avDetailsPB.visibility = View.VISIBLE
                avDetailsAcceptBT.text = ""

                ConnectionCheck.perform(object : RequestListener {
                    override fun onComplete(resultCode: Int) {
                        if (resultCode != RequestListener.SUCCESS) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.err_no_connection),
                                Toast.LENGTH_LONG
                            ).show()
                            avDetailsAcceptBT.text = getString(R.string.submit)
                            avDetailsAcceptBT.visibility = View.GONE
                            button.isEnabled = true
                        } else {
                            modpanelViewModel.acceptAvailability(
                                request, shopViewModel, object : RequestListener {
                                    override fun onComplete(resultCode: Int) {
                                        val toastText = when (resultCode) {
                                            RequestListener.SUCCESS -> getString(R.string.request_accepted).also {
                                                requireActivity().onBackPressed()
                                            }
                                            RequestListener.NOT_LOGGED_IN -> getString(R.string.err_notloggedin)
                                            else -> getString(R.string.toast_error)
                                        }
                                        Toast.makeText(
                                            requireContext(),
                                            toastText,
                                            Toast.LENGTH_LONG
                                        ).show()

                                        avDetailsAcceptBT.text = getString(R.string.submit)
                                        avDetailsAcceptBT.visibility = View.GONE
                                        button.isEnabled = true
                                    }
                                }
                            )
                        }
                    }
                })
            }
        }
    }

}