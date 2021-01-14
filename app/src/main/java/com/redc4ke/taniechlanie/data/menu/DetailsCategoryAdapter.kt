package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Category
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_details_catlist.view.*

class DetailsCategoryAdapter(private val alcoObject: AlcoObject,
                             private val categories: Map<Int, Category>):
        RecyclerView.Adapter<DetailsCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsCategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.row_details_catlist, parent, false)

        return DetailsCategoryViewHolder(item)
    }

    override fun onBindViewHolder(holder: DetailsCategoryViewHolder, position: Int) {
        val view = holder.view
        val id = alcoObject.categories[position]
        val category = categories[id]

        view.catlist_nameTV.text = category?.name
        if (category?.image != null) {
            Picasso.get().load(category.image).into(view.catlist_IV)
        }
    }

    override fun getItemCount(): Int {
        return alcoObject.categories.size
    }

}

class DetailsCategoryViewHolder(val view: View): RecyclerView.ViewHolder(view)
