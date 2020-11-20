package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import kotlinx.android.synthetic.main.row_details_shop.view.*


class DetailsShopAdapter(private val list: ArrayList<String>?): RecyclerView.Adapter<DetailsShopViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_details_shop, parent, false)

        return DetailsShopViewHolder(row)
    }

    override fun onBindViewHolder(holder: DetailsShopViewHolder, position: Int) {
        if (list != null)
            holder.view.name_details_shop.text = list[position]
        else holder.view.name_details_shop.text = "N/A"
    }

    override fun getItemCount(): Int {
        return list?.size ?: 1
    }

}

class DetailsShopViewHolder(val view: View): RecyclerView.ViewHolder(view)