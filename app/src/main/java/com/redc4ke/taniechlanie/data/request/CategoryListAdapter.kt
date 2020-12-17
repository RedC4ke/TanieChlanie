package com.redc4ke.taniechlanie.data.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.ui.request.CategoryFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_request_category.view.*

class CategoryListAdapter(
        private val categoryList: Map<Int, Category>,
        private val fragment: CategoryFragment):
        RecyclerView.Adapter<CategoryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.row_request_category, parent, false)

        return CategoryListViewHolder(item)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val tv = holder.view.row_cat_name_TV
        val icon = holder.view.row_cat_icon
        val output: Category?
        if (position == 0) {
            tv.text = "Brak"
            output = null
        } else {
            val category = categoryList[position-1]
            tv.text = category?.name
            if (category?.image != null)
                Picasso.get().load(category.image).into(icon)
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