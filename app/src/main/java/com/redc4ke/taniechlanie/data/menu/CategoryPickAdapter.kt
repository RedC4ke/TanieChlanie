package com.redc4ke.taniechlanie.data.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.databinding.RowCategoryPickBinding
import com.redc4ke.taniechlanie.databinding.RowDetailsCatlistBinding

class CategoryPickAdapter(private val context: Context) :
    RecyclerView.Adapter<CategoryPickViewHolder>() {

    private var mCatList = listOf<Category>()
    private var selectedCategories: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryPickViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowCategoryPickBinding.inflate(inflater, parent, false)

        return CategoryPickViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryPickViewHolder, position: Int) {
        val category = mCatList[position]

        if (category.image != null) {
            holder.vb.rowCatPickIV.visibility = View.VISIBLE
            Glide.with(context).load(category.image).into(holder.vb.rowCatPickIV)
        }

        holder.vb.rowCatPickTV.text = category.name

        holder.vb.rowCatPickCHB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedCategories.add(category.id)
            } else {
                selectedCategories.remove(category.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return mCatList.size
    }

    fun update(catList: List<Category>) {
        mCatList = catList
        notifyDataSetChanged()
    }

    fun getSelected(): List<Int> {
        return selectedCategories
    }
}

class CategoryPickViewHolder(var vb: RowCategoryPickBinding) : RecyclerView.ViewHolder(vb.root)