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

class ShopListAdapter(
        private val shopList: Map<Int, Shop>,
        private var frag: ShopFragment):
        RecyclerView.Adapter<ShopListViewHolder>() {

    private val list = shopList.values.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_request_shop, parent, false)

        return ShopListViewHolder(row)
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val view = holder.view
        val checkbox = view.req_shop_CHB
        val viewModel = frag.selectedShopsViewModel
        checkbox.isChecked = viewModel.isAdded(position + 1)

        val shop = list[position]
        view.row_shop_name_TV.text = shop.name
        checkbox.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                frag.selectedShopsViewModel.add(shop.id)
            } else {
                frag.selectedShopsViewModel.remove(shop.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class ShopListViewHolder(val view: View): RecyclerView.ViewHolder(view)
