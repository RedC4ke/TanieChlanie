package com.redc4ke.taniechlanie.data.menu

import android.graphics.BitmapFactory
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Categories
import com.redc4ke.taniechlanie.ui.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_details_category.view.*
import kotlinx.android.synthetic.main.row_details_category_single.view.*
import kotlinx.android.synthetic.main.row_details_category_twin.view.*

class DetailsCategoryAdapter(private val frag: Fragment, private val positions: ArrayList<Int>?):
        RecyclerView.Adapter<DetailsCategoryViewHolder>() {

    private var catList: ArrayList<Categories.Category> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsCategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = when (itemCount) {
            1 -> inflater.inflate(R.layout.row_details_category_single, parent, false)
            2 -> inflater.inflate(R.layout.row_details_category_twin, parent, false)
            else -> inflater.inflate(R.layout.row_details_category, parent, false)
        }

        val act = frag.requireActivity() as MainActivity
        val tempList: ArrayList<Categories.Category> = arrayListOf()
        positions?.forEach {
            val cat = act.categories.get(it)
            if (cat != null) {
                tempList.add(cat)
                Log.d("huj", cat.toString())
            }
        }

        catList = tempList

        return DetailsCategoryViewHolder(item)
    }

    override fun onBindViewHolder(holder: DetailsCategoryViewHolder, position: Int) {
        Log.d("huj", catList.size.toString())
        if (catList.size > 0) {
            val category = catList[position]

            val icon: ImageView
            val tv: TextView?
            when (catList.size) {
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

            if (category.image != null) {
                val img = category.image
                Picasso.get().load(img).into(icon)
            }

            if (tv != null) {
                tv.text = category.name
            }

        }

    }

    override fun getItemCount(): Int {
        return positions!!.size
    }

}

class DetailsCategoryViewHolder(val view: View): RecyclerView.ViewHolder(view)
