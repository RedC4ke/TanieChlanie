package com.redc4ke.taniechlanie.data.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.BaseRecyclerViewAdapter
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.databinding.RowRequestCategoryBinding
import com.redc4ke.taniechlanie.ui.request.CategoryFragment
import com.squareup.picasso.Picasso

class CategoryListAdapter(
        private val categoryList: Map<Int, Category>,
        private val fragment: CategoryFragment):
        BaseRecyclerViewAdapter<CategoryListViewHolder, RowRequestCategoryBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowRequestCategoryBinding
        get() = RowRequestCategoryBinding::inflate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        _binding = bindingInflater.invoke(inflater, parent, false)

        return CategoryListViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {

        val output: Category?
        if (position == 0) {
            binding.rowCatNameTV.text = "Brak"
            output = null
        } else {
            val category = categoryList[position-1]
            binding.rowCatNameTV.text = category?.name
            if (category?.image != null)
                Picasso.get().load(category.image).into(binding.rowCatIcon)
            output = category
        }

        holder.view.setOnClickListener {
            fragment.onItemClick(output)
        }



    }

    override fun getItemCount(): Int {
        // return sortedCategoryList.size
        return categoryList.size + 1
    }

}

class CategoryListViewHolder(val view: View): RecyclerView.ViewHolder(view)