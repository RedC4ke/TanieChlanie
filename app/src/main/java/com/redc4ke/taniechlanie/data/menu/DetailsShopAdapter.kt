package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.BaseRecyclerViewAdapter
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.databinding.RowSheet2Binding


class DetailsShopAdapter(
    alcoObject: AlcoObject,
    private val shops: Map<Int, Shop>):
    BaseRecyclerViewAdapter<DetailsShopViewHolder, RowSheet2Binding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowSheet2Binding
        get() = RowSheet2Binding::inflate
    var list = alcoObject.shop

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        _binding = bindingInflater.invoke(inflater, parent, false)

        return DetailsShopViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: DetailsShopViewHolder, position: Int) {
        val shopId = list[position]
        binding.sheet2ShopTV.text = shops[shopId]?.name
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