package com.redc4ke.taniechlanie.data.request

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.ui.request.ShopFragment
import kotlinx.android.synthetic.main.row_request_shop.view.*

class ShopListAdapter(private val shopList: ArrayList<Shop>, private var frag: ShopFragment):
    RecyclerView.Adapter<ShopListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_request_shop, parent, false)

        return ShopListViewHolder(row)
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val shop = shopList[position]
        holder.view.row_shop_name_TV.text = shop.name

        holder.view.req_shop_CHB.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                frag.selectedShops.add(shop.id)
            } else {
                frag.selectedShops.remove(shop.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

}

class ShopListViewHolder(val view: View): RecyclerView.ViewHolder(view)
