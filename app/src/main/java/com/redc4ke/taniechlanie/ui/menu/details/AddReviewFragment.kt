package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.Review
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.databinding.FragmentReviewAddBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class AddReviewFragment(
    private val alcoobject_id: Int,
    private val review: Review?):
    BaseDialogFragment<FragmentReviewAddBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewAddBinding
        get() = FragmentReviewAddBinding::inflate
    private var correct1 = false
    private var correct2 = false
    private lateinit var reviewViewModel: ReviewViewModel
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.isCancelable = false
        reviewViewModel = ViewModelProvider(requireActivity())
            .get(ReviewViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            if (review != null) {
                reviewAddET.setText(review.content)
                reviewAddRB.rating = review.rating.toFloat()
            }
            reviewAddET.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    when (s?.trim()?.length!!) {
                        0 -> {
                            revievAddL.error = getString(R.string.err_emptyField)
                            revievAddL.isErrorEnabled = true
                            correct2 = false
                        }
                        in 1..1000 -> {
                            revievAddL.isErrorEnabled = false
                            correct2 = true
                        }
                        else -> {
                            revievAddL.error = getString(R.string.err_tooLong, "1000")
                            revievAddL.isErrorEnabled = true
                            correct2 = false
                        }
                    }
                }

            })
            revievAddCancelBT.setOnClickListener {
                this@AddReviewFragment.dismiss()
            }
            reviewAddSendBT.setOnClickListener {
                val rating = binding.reviewAddRB.rating
                if (rating == 0F) {
                    reviewAddRatingErrorTV.visibility = View.VISIBLE
                    correct1 = false
                } else {
                    reviewAddRatingErrorTV.visibility = View.GONE
                    correct1 = true
                }
                if (correct1 && correct2 && user != null) {
                    reviewViewModel.addReview(requireContext(), alcoobject_id, user,
                        reviewAddRB.rating.toDouble(), reviewAddET.text.toString())
                        .addOnSuccessListener {
                            this@AddReviewFragment.dismiss()
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}