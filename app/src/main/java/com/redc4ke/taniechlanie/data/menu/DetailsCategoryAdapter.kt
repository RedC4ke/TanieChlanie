package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.BaseRecyclerViewAdapter
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.databinding.RowDetailsCatlistBinding
import com.squareup.picasso.Picasso

class DetailsCategoryAdapter(private val alcoObject: AlcoObject,
                             private val categories: Map<Int, Category>):
        BaseRecyclerViewAdapter<DetailsCategoryViewHolder, RowDetailsCatlistBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowDetailsCatlistBinding
        get() = RowDetailsCatlistBinding::inflate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsCategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        _binding = bindingInflater.invoke(inflater, parent, false)

        return DetailsCategoryViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: DetailsCategoryViewHolder, position: Int) {

        val id = alcoObject.categories[position]
        val category = categories[id]

        binding.catlistNameTV.text = category?.name
        if (category?.image != null) {
            Picasso.get().load(category.image).into(binding.catlistIV)
        }
    }

    override fun getItemCount(): Int {
        return alcoObject.categories.size
    }

}

class DetailsCategoryViewHolder(val view: View): RecyclerView.ViewHolder(view)
