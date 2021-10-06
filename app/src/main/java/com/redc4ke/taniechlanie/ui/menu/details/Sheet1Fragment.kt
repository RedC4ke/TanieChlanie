package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.menu.DetailsCategoryAdapter
import com.redc4ke.taniechlanie.data.menu.ReviewAdapter
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.Review
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentSheet1Binding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.popup.AddReviewFragment
import java.math.BigDecimal
import java.math.RoundingMode

class Sheet1Fragment : BaseFragment<FragmentSheet1Binding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSheet1Binding
        get() = FragmentSheet1Binding::inflate
    private lateinit var alcoObject: AlcoObject
    private lateinit var mainActivity: MainActivity
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var userViewModel: UserViewModel
    private var userReview: Review? = null
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_left, R.transition.slide_to_left)

        alcoObject = (requireParentFragment().parentFragment as DetailsFragment).alcoObject
        mainActivity = requireActivity() as MainActivity
        categoryViewModel = ViewModelProvider(mainActivity).get(CategoryViewModel::class.java)
        reviewViewModel = ViewModelProvider(mainActivity).get(ReviewViewModel::class.java)
        userViewModel = ViewModelProvider(mainActivity)[UserViewModel::class.java]
        reviewViewModel.download(alcoObject.id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = super.onCreateView(inflater, container, savedInstanceState)

        with(binding) {
            detailsPriceTV.text = lowestPriceString(alcoObject, requireContext())
            detailsValueTV.text = valueString(alcoObject, (requireActivity() as MainActivity))
            detailsVolumeTV.text = volumeString(alcoObject.volume, requireContext())
            detailsVoltageTV.text = voltageString(alcoObject.voltage, requireContext())
            descriptionTV.text =
                alcoObject.description ?: getString(R.string.details_descriptionDisclaimer)
            categoryRV.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            categoryViewModel.getAll().observe(viewLifecycleOwner, {
                val categories = categoryViewModel.getWithMajorFirst(alcoObject.categories)
                categoryRV.adapter = DetailsCategoryAdapter(categories)
            })

            detailsAvailabilityBT.setOnClickListener {
                findNavController().navigate(
                    R.id.action_sheet1_dest_to_sheet2_dest
                )
            }

            reviewViewModel.getAll().observe(viewLifecycleOwner, {
                val reviews: MutableList<Review> =
                    (it[alcoObject.id] ?: mutableListOf()) as MutableList<Review>
                val ratings = mutableListOf<Double>()
                reviews.forEach { review ->
                    ratings.add(review.rating)
                }

                userReview = reviews.find { review ->
                    review.author ==
                            user?.uid
                }

                reviewCountTV.text = getString(R.string.details_count, reviews.size.toString())
                if (ratings.isNotEmpty()) {
                    ratingTV.text = String.format(
                        BigDecimal(ratings.average())
                            .setScale(2, RoundingMode.HALF_EVEN).toString()
                    )
                } else {
                    ratingTV.text = "N/A"
                }

                ratingBar.rating = ratings.average().toFloat()
                reviewRV.layoutManager = LinearLayoutManager(context)
                val size = if (reviews.size >= 6) {
                    6
                } else {
                    reviews.size
                }

                if (userReview != null) {
                    reviewAddTV.text = getString(R.string.details_editReview)
                } else {
                    reviewAddTV.text = getString(R.string.details_rate)
                }

                reviewRV.adapter = ReviewAdapter(
                    this@Sheet1Fragment,
                    reviews.take(size), reviewViewModel, userReview, alcoObject.id
                )
            })
            reviewAddTV.setOnClickListener {
                when (true) {
                    (FirebaseAuth.getInstance().currentUser == null) -> {
                        (requireActivity() as MainActivity).login()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_notloggedin),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    (userViewModel.hasReviewBan()) -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_noPermission),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        AddReviewFragment(alcoObject.id, userReview).show(
                            parentFragmentManager,
                            "addReview"
                        )
                    }
                }
            }
        }

        return root
    }

}