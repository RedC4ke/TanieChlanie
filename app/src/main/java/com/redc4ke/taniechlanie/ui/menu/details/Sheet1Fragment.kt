package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.menu.DetailsCategoryAdapter
import com.redc4ke.taniechlanie.data.menu.ReviewAdapter
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.databinding.FragmentSheet1Binding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import java.math.BigDecimal
import java.math.RoundingMode

class Sheet1Fragment : BaseFragment<FragmentSheet1Binding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSheet1Binding
        get() = FragmentSheet1Binding::inflate
    private lateinit var alcoObject: AlcoObject
    private lateinit var mainActivity: MainActivity
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var reviewViewModel: ReviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_left, R.transition.slide_to_left)

        alcoObject = (requireParentFragment().parentFragment as DetailsFragment).alcoObject
        mainActivity = requireActivity() as MainActivity
        categoryViewModel = ViewModelProvider(mainActivity).get(CategoryViewModel::class.java)
        reviewViewModel = ViewModelProvider(mainActivity).get(ReviewViewModel::class.java)
        reviewViewModel.download(alcoObject.id)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = super.onCreateView(inflater, container, savedInstanceState)

        with(binding) {
            detailsPriceTV.text = getString(R.string.suff_price,
                String.format("%.2f", alcoObject.price))
            detailsValueTV.text = valueString(alcoObject, (requireActivity() as MainActivity))
            detailsVolumeTV.text = volumeString(alcoObject, requireContext())
            detailsVoltageTV.text = voltageString(alcoObject, requireContext())
            descriptionTV.text = alcoObject.description
            categoryRV.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            categoryViewModel.get().observe(viewLifecycleOwner, {
                val categories = categoryViewModel.getWithMajor(alcoObject)
                categoryRV.adapter = DetailsCategoryAdapter(categories)
            })
            reviewViewModel.getAll().observe(viewLifecycleOwner, {
                val reviews = it[alcoObject.id] ?: listOf()
                val ratings = mutableListOf<Double>()
                reviews.forEach { review ->
                    ratings.add(review.rating)
                }
                Log.d("huj", reviews.toString())
                reviewCountTV.text = getString(R.string.details_count, reviews.size.toString())
                if (ratings.isNotEmpty()) {
                    ratingTV.text = String.format(BigDecimal(ratings.average())
                        .setScale(2, RoundingMode.HALF_EVEN).toString())
                } else {
                    ratingTV.text = "N/A"
                }

                ratingBar.rating = ratings.average().toFloat()
                reviewRV.layoutManager = LinearLayoutManager(context)
                val size = if (reviews.size >=4) {
                    4
                } else {
                    reviews.size
                }
                reviewRV.adapter = ReviewAdapter(
                    requireContext(), reviews.take(size), reviewViewModel)
            })
            reviewAddTV.setOnClickListener {
                AddReviewFragment(alcoObject.id).show(parentFragmentManager, "addReview")
            }
        }

        return root
    }

}