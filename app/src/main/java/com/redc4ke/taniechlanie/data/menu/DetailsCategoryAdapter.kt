package com.redc4ke.taniechlanie.data.menu

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ItemCategories
import kotlinx.android.synthetic.main.row_details_category.view.*
import kotlinx.android.synthetic.main.row_details_category_single.view.*
import kotlinx.android.synthetic.main.row_details_category_twin.view.*

class DetailsCategoryAdapter(private val categoryList: ArrayList<Int>?): RecyclerView.Adapter<DetailsCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsCategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = when (categoryList?.size) {
            1 -> inflater.inflate(R.layout.row_details_category_single, parent, false)
            2 -> inflater.inflate(R.layout.row_details_category_twin, parent, false)
            else -> inflater.inflate(R.layout.row_details_category, parent, false)
        }


        return DetailsCategoryViewHolder(item)
    }

    override fun onBindViewHolder(holder: DetailsCategoryViewHolder, position: Int) {
        if (categoryList != null) {
            val icon: ImageView
            val tv: TextView?
            when (categoryList.size) {
                1 -> {
                    icon = holder.view.drawable_details_categoryBig
                    tv = holder.view.details_category_single_TV
                }
                2 -> {
                    icon = holder.view.drawable_details_categoryTwin
                    tv = holder.view.details_category_twin_TV
                    if (position == 1) {
                        holder.view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            this.topMargin = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                7.5f,
                                holder.view.resources.displayMetrics
                            ).toInt()
                        }
                    }
                }
                else -> {
                    icon = holder.view.drawable_details_category
                    tv = null
                }
            }

            icon.setBackgroundResource(ItemCategories.list[categoryList[position]].icon)

            if (tv != null) {
                tv.text = ItemCategories.list[categoryList[position]].name
            }

        }

    }

    override fun getItemCount(): Int {
        return categoryList?.size ?: 1
    }

}

class DetailsCategoryViewHolder(val view: View): RecyclerView.ViewHolder(view)
