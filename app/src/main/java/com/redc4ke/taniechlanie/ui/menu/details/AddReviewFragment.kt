package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ConnectionCheck
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.viewmodels.Review
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.databinding.FragmentReviewAddBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class AddReviewFragment(
    private val alcoobject_id: Long,
    private val review: Review?
) :
    BaseDialogFragment<FragmentReviewAddBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewAddBinding
        get() = FragmentReviewAddBinding::inflate
    private var correct1 = false
    private var correct2 = false
    private lateinit var reviewViewModel: ReviewViewModel
    private val user = FirebaseAuth.getInstance().currentUser
    private var update = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.isCancelable = false
        reviewViewModel = ViewModelProvider(requireActivity())
            .get(ReviewViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            if (review != null) {
                reviewAddET.setText(review.content)
                reviewAddRB.rating = review.rating.toFloat()
                update = true
            }
            reviewAddET.addTextChangedListener(object : TextWatcher {
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
                            reviewAddL.error = getString(R.string.err_emptyField)
                            reviewAddL.isErrorEnabled = true
                            correct2 = false
                        }
                        in 1..1000 -> {
                            reviewAddL.isErrorEnabled = false
                            correct2 = true
                        }
                        else -> {
                            reviewAddL.error = getString(R.string.err_tooLong, "1000")
                            reviewAddL.isErrorEnabled = true
                            correct2 = false
                        }
                    }
                }

            })
            reviewAddCancelBT.setOnClickListener {
                this@AddReviewFragment.dismiss()
            }
            reviewAddSendBT.setOnClickListener { button ->
                reviewAddSendBT.text = ""
                reviewAddPB.visibility = View.VISIBLE
                button.isEnabled = false

                val rating = binding.reviewAddRB.rating
                if (rating == 0F) {
                    reviewAddRatingErrorTV.visibility = View.VISIBLE
                    correct1 = false
                } else {
                    reviewAddRatingErrorTV.visibility = View.GONE
                    correct1 = true
                }
                if (correct1 && correct2 && user != null) {
                    ConnectionCheck.perform(object : RequestListener {
                        override fun onComplete(resultCode: Int) {
                            if (resultCode == RequestListener.NETWORK_ERR) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.err_no_connection),
                                    Toast.LENGTH_LONG
                                ).show()
                                reviewAddSendBT.text = getString(R.string.send)
                                reviewAddPB.visibility = View.GONE
                                button.isEnabled = true
                            } else {
                                reviewViewModel.addReview(
                                    requireContext(),
                                    alcoobject_id,
                                    user,
                                    reviewAddRB.rating.toDouble(),
                                    reviewAddET.text.toString(),
                                    update
                                )
                                    .addOnSuccessListener {
                                        this@AddReviewFragment.dismiss()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.toast_error),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        reviewAddSendBT.text = getString(R.string.send)
                                        reviewAddPB.visibility = View.GONE
                                        button.isEnabled = true
                                    }
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}