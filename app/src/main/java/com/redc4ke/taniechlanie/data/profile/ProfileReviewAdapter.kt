package com.redc4ke.taniechlanie.data.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.viewmodels.Review
import com.redc4ke.taniechlanie.databinding.RowProfileReviewBinding
import java.text.DateFormat

class ProfileReviewAdapter(
    private val context: Context,
    private var reviewList: List<Pair<AlcoObject, Review>>
) : RecyclerView.Adapter<ProfileReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowProfileReviewBinding.inflate(inflater)

        return ProfileReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileReviewViewHolder, position: Int) {
        val item = reviewList[position]
        with(holder.vb) {
            photo(item.first, reviewVariationImageIV)
            reviewVariationNameTV.text = item.first.name
            reviewVariationRB.rating = item.second.rating.toFloat()
            reviewVariationTimestampTV.text = DateFormat.getDateInstance()
                .format(item.second.timestamp.toDate())
            reviewVariationContentTV.text = item.second.content
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    private fun photo(alc: AlcoObject, iv: ImageView) {
        if (alc.photo != null) {
            Glide.with(context).load(alc.photo).into(iv)
        } else {
            iv.setImageResource(R.drawable.liquor)
        }
    }

    fun addData(list: List<Pair<AlcoObject, Review>>) {
        val pos = reviewList.size
        reviewList = list
        notifyItemRangeChanged(pos-1, reviewList.size)
    }

}

class ProfileReviewViewHolder(val vb: RowProfileReviewBinding) : RecyclerView.ViewHolder(vb.root)