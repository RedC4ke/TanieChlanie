package com.redc4ke.taniechlanie.data.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Shop
import kotlinx.android.synthetic.main.row_sheet2.view.*


class DetailsShopAdapter(
    private val alcoObject: AlcoObject,
    private val shops: Map<Int, Shop>):
    RecyclerView.Adapter<DetailsShopViewHolder>() {

    var list = alcoObject.shop

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_sheet2, parent, false)

        return DetailsShopViewHolder(row)
    }

    override fun onBindViewHolder(holder: DetailsShopViewHolder, position: Int) {
        val shopId = list[position]
        holder.view.sheet2_shopTV.text = shops[shopId]?.name
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(filtered: ArrayList<Int>) {
        list = filtered
        notifyDataSetChanged()
    }

}

class DetailsShopViewHolder(val view: View): RecyclerView.ViewHolder(view)