package com.redc4ke.taniechlanie.data.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.viewmodels.Review
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.databinding.RowReviewBinding
import com.redc4ke.taniechlanie.databinding.RowSheet2Binding
import com.redc4ke.taniechlanie.ui.menu.details.AddReviewFragment
import com.redc4ke.taniechlanie.ui.menu.details.Sheet1Fragment

class ReviewAdapter(
    private val fragment: Sheet1Fragment,
    private val list: List<Review>,
    private val reviewViewModel: ReviewViewModel,
    private val userReview: Review?,
    private val id: Long):
    RecyclerView.Adapter<ReviewViewHolder>() {

    private val finalList = mutableListOf<Review>()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        if (userReview != null) {
            finalList.add(userReview)
        }
        list.forEach {
            if (it != userReview){
                finalList.add(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowReviewBinding.inflate(inflater, parent, false)

        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        with (holder.vb) {
            val popup = PopupMenu(fragment.context, reviewMenuBT)
            if (position == 0 && userReview != null) {
                popup.menuInflater.inflate(R.menu.myreview_menu, popup.menu)
            } else {
                popup.menuInflater.inflate(R.menu.review_menu, popup.menu)
            }
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.review_report -> {
                        Toast.makeText(fragment.context,
                            "Ta funkcja jest czasowo niedostępna!", Toast.LENGTH_LONG).show()
                        true}
                    R.id.review_edit -> {
                        if (user?.uid == finalList[position].author) {
                            AddReviewFragment(id, finalList[position])
                                .show(fragment.parentFragmentManager, "reviewEdit")
                        }
                        true}
                    R.id.review_delete -> {
                        if (user?.uid == finalList[position].author) {
                            reviewViewModel.remove(finalList[position], user)
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }

            }
            reviewMenuBT.setOnClickListener {
                popup.show()
            }

            val review = finalList[position]
            contentTV.setTrimLength(180)
            contentTV.text = review.content
            reviewViewModel.parse(fragment.requireContext(), review, avatarIV,
                usernameTV, timestampTV, reviewRB)
        }
    }

    override fun getItemCount(): Int {
        return finalList.size
    }
}

class ReviewViewHolder(val vb: RowReviewBinding): RecyclerView.ViewHolder(vb.root)