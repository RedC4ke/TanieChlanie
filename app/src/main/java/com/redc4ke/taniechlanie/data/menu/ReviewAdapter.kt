package com.redc4ke.taniechlanie.data.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.data.viewmodels.Review
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.databinding.RowReviewBinding
import com.redc4ke.taniechlanie.databinding.RowSheet2Binding

class ReviewAdapter(
    private val context: Context,
    private val list: List<Review>,
    private val reviewViewModel: ReviewViewModel):
    RecyclerView.Adapter<ReviewViewHolder>() {

    val col = FirebaseFirestore.getInstance().collection("users")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowReviewBinding.inflate(inflater, parent, false)

        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        with (holder.vb) {
            val review = list[position]
            contentTV.setTrimLength(180)
            contentTV.text = review.content
            reviewViewModel.parse(context, review, avatarIV, usernameTV, timestampTV, reviewRB)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ReviewViewHolder(val vb: RowReviewBinding): RecyclerView.ViewHolder(vb.root)