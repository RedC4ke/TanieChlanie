package com.redc4ke.taniechlanie.data.request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.databinding.RowRequestCategoryBinding
import com.redc4ke.taniechlanie.ui.request.CategoryFragment
import com.squareup.picasso.Picasso

class CategoryListAdapter(
        private val categoryList: Map<Int, Category>,
        private val fragment: CategoryFragment):
        RecyclerView.Adapter<CategoryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = RowRequestCategoryBinding.inflate(inflater, parent, false)

        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {

        val output: Category?
        if (position == 0) {
            holder.vb.rowCatNameTV.text = "Brak"
            output = null
        } else {
            val category = categoryList[position-1]
            holder.vb.rowCatNameTV.text = category?.name
            if (category?.image != null)
                Picasso.get().load(category.image).into(holder.vb.rowCatIcon)
            output = category
        }

        holder.vb.root.setOnClickListener {
            fragment.onItemClick(output)
        }



    }

    override fun getItemCount(): Int {
        // return sortedCategoryList.size
        return categoryList.size + 1
    }

}

class CategoryListViewHolder(var vb: RowRequestCategoryBinding): RecyclerView.ViewHolder(vb.root)