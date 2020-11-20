package com.redc4ke.taniechlanie.data.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ItemCategories
import com.redc4ke.taniechlanie.data.ItemCategory
import com.redc4ke.taniechlanie.ui.request.CategoryFragment
import kotlinx.android.synthetic.main.row_request_category.view.*

class CategoryListAdapter(val fragment: CategoryFragment): RecyclerView.Adapter<CategoryListViewHolder>() {

    private val sortedCategoryList = ItemCategories.list.sortedBy { it.name }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.row_request_category, parent, false)

        return CategoryListViewHolder(item)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val tv = holder.view.row_cat_name_TV
        val icon = holder.view.row_cat_icon
        val output: ItemCategory?
        if (position == 0) {
            tv.text = "Brak"
            output = null
        } else {
            val category = sortedCategoryList[position-1]
            tv.text = category.name
            icon.setBackgroundResource(category.icon)
            output = category
        }

        holder.view.setOnClickListener {
            fragment.onItemClick(output)
        }



    }

    override fun getItemCount(): Int {
        return ItemCategories.list.size + 1
    }

}

class CategoryListViewHolder(val view: View): RecyclerView.ViewHolder(view)