package com.redc4ke.taniechlanie.data.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.BaseRecyclerViewAdapter
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.databinding.RowRequestShopBinding
import com.redc4ke.taniechlanie.ui.request.ShopFragment

class ShopListAdapter(
        shopList: Map<Int, Shop>,
        private var frag: ShopFragment):
        BaseRecyclerViewAdapter<ShopListViewHolder, RowRequestShopBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowRequestShopBinding
        get() = RowRequestShopBinding::inflate
    private val list = shopList.values.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        _binding = bindingInflater.invoke(inflater, parent, false)

        return ShopListViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        with (binding) {
            val checkbox = reqShopCHB
            val viewModel = frag.selectedShopsViewModel
            checkbox.isChecked = viewModel.isAdded(position + 1)

            val shop = list[position]
            rowShopNameTV.text = shop.name
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    frag.selectedShopsViewModel.add(shop.id)
                } else {
                    frag.selectedShopsViewModel.remove(shop.id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class ShopListViewHolder(val view: View): RecyclerView.ViewHolder(view)
