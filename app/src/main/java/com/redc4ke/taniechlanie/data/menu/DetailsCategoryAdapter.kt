package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.databinding.RowDetailsCatlistBinding
import com.squareup.picasso.Picasso

class DetailsCategoryAdapter(private val alcoObject: AlcoObject,
                             private val categories: Map<Int, Category>):
        RecyclerView.Adapter<DetailsCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsCategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowDetailsCatlistBinding.inflate(inflater, parent, false)

        return DetailsCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailsCategoryViewHolder, position: Int) {

        val id = alcoObject.categories[position]
        val category = categories[id]

        holder.vb.catlistNameTV.text = category?.name
        if (category?.image != null) {
            Picasso.get().load(category.image).into(holder.vb.catlistIV)
        }
    }

    override fun getItemCount(): Int {
        return alcoObject.categories.size
    }

}

class DetailsCategoryViewHolder(var vb: RowDetailsCatlistBinding): RecyclerView.ViewHolder(vb.root)
