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
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.databinding.FragmentReviewAddBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class AddReviewFragment(private val alcoobject_id: Int): BaseDialogFragment<FragmentReviewAddBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewAddBinding
        get() = FragmentReviewAddBinding::inflate
    private var correct = false
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
            reviewAddRB.setOnRatingChangeListener { _, rating ->
                if (rating == 0F) {
                    reviewAddRatingErrorTV.visibility = View.VISIBLE
                    correct = false
                } else {
                    reviewAddRatingErrorTV.visibility = View.GONE
                    correct = true
                }
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
                    val length = s?.trim()?.length!!
                    if (length == 0) {
                        revievAddL.error = getString(R.string.err_emptyField)
                        revievAddL.isErrorEnabled = true
                        correct = false
                    } else if (length > 1000) {
                        revievAddL.error = getString(R.string.err_tooLong, "1000")
                        revievAddL.isErrorEnabled = true
                        correct = false
                    } else {
                        revievAddL.isErrorEnabled = false
                        correct = true
                    }
                }

            })
            revievAddCancelBT.setOnClickListener {
                this@AddReviewFragment.dismiss()
            }
            reviewAddSendBT.setOnClickListener {
                if (correct && user != null) {
                    reviewViewModel.addReview(requireContext(), alcoobject_id, user,
                        reviewAddRB.rating.toDouble(), reviewAddET.text.toString())
                        .addOnSuccessListener {
                            this@AddReviewFragment.dismiss()
                        }
                }
            }
        }
    }
}