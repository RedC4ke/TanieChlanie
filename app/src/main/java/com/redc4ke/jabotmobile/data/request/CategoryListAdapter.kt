package com.redc4ke.jabotmobile.data.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.jabotmobile.R
import com.redc4ke.jabotmobile.data.ItemCategories
import com.redc4ke.jabotmobile.ui.request.CategoryFragment
import kotlinx.android.synthetic.main.row_request_category.view.*

class CategoryListAdapter(val fragment: CategoryFragment): RecyclerView.Adapter<CategoryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.row_request_category, parent, false)

        return CategoryListViewHolder(item)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val category = ItemCategories.list[position]
        holder.view.row_cat_name_TV.text = category.name
        holder.view.row_cat_icon.setBackgroundResource(category.icon)

        holder.view.setOnClickListener {
            fragment.findNavController().navigate(R.id.from_category)
        }
    }

    override fun getItemCount(): Int {
        return ItemCategories.list.size
    }

}

class CategoryListViewHolder(val view: View): RecyclerView.ViewHolder(view)