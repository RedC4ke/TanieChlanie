package com.redc4ke.taniechlanie.data.profile

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.setImage
import com.redc4ke.taniechlanie.data.viewmodels.Review
import com.redc4ke.taniechlanie.databinding.RowProfileReviewBinding
import java.text.DateFormat

class ProfileReviewAdapter(
    private val context: Context,
    private val reviewList: List<Pair<AlcoObject, Review>>
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
            setImage(context, alc.name, iv, Uri.parse(alc.photo!!))
        } else {
            iv.setImageResource(R.drawable.liquor)
        }
    }

}

class ProfileReviewViewHolder(val vb: RowProfileReviewBinding) : RecyclerView.ViewHolder(vb.root)