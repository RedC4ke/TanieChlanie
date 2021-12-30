package com.redc4ke.taniechlanie.data.menu

import android.content.Context
import android.util.Log
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
    private var selectedCategories: MutableList<Category> = mutableListOf()

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

        holder.vb.rowCatPickCHB.isChecked = selectedCategories.contains(mCatList[position])

        holder.vb.rowCatPickTV.text = category.name

        holder.vb.root.setOnClickListener {
            holder.vb.rowCatPickCHB.toggle()
        }

        holder.vb.rowCatPickCHB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedCategories.add(category)
            } else {
                selectedCategories.remove(category)
            }
        }
    }

    override fun getItemCount(): Int {
        return mCatList.size
    }

    fun update(catList: List<Category>, selected: List<Category>) {
        mCatList = catList
        selectedCategories = selected.toMutableList()
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedCategories = mCatList.toMutableList()
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedCategories = mutableListOf()
        notifyDataSetChanged()
    }

    fun getSelected(): List<Category> {
        return selectedCategories
    }
}

class CategoryPickViewHolder(var vb: RowCategoryPickBinding) : RecyclerView.ViewHolder(vb.root)