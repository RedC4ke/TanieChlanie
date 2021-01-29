package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.databinding.RowSheet2Binding


class DetailsShopAdapter(
    alcoObject: AlcoObject,
    private val shops: Map<Int, Shop>):
    RecyclerView.Adapter<DetailsShopViewHolder>() {

    var list = alcoObject.shop

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowSheet2Binding.inflate(inflater, parent, false)

        return DetailsShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailsShopViewHolder, position: Int) {
        val shopId = list[position]
        holder.vb.sheet2ShopTV.text = shops[shopId]?.name
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(filtered: ArrayList<Int>) {
        list = filtered
        notifyDataSetChanged()
    }

}

class DetailsShopViewHolder(var vb: RowSheet2Binding): RecyclerView.ViewHolder(vb.root)