package com.redc4ke.taniechlanie.data.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.priceString
import com.redc4ke.taniechlanie.databinding.RowSheet2Binding
import java.math.BigDecimal


class DetailsShopAdapter(
    private val alcoObject: AlcoObject,
    private val shops: Map<Int, Shop>,
    private val context: Context):
    RecyclerView.Adapter<DetailsShopViewHolder>() {

    var idList = alcoObject.shopToPrice.keys.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowSheet2Binding.inflate(inflater, parent, false)

        return DetailsShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailsShopViewHolder, position: Int) {
        val shopId = idList[position]
        val price = alcoObject.shopToPrice[shopId]
        holder.vb.sheet2shopTV.text = shops[shopId]?.name
        if (price != null) {
            holder.vb.sheet2priceTV.text = priceString(alcoObject
                .shopToPrice[shopId] ?: BigDecimal.valueOf(0), context)
        } else {
            holder.vb.sheet2priceTV.apply {
                text = context.getString(R.string.details_addprice)
                setTextColor(context.getColor(R.color.primaryLightColor))
            }
            holder.vb.sheet2editBT.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return idList.size
    }

    fun update(filtered: ArrayList<Int>) {
        idList = filtered
        notifyDataSetChanged()
    }

}

class DetailsShopViewHolder(var vb: RowSheet2Binding): RecyclerView.ViewHolder(vb.root)