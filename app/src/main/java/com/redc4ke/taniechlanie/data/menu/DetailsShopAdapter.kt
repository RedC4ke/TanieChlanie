package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.data.priceString
import com.redc4ke.taniechlanie.databinding.RowSheet2Binding
import com.redc4ke.taniechlanie.ui.menu.details.DetailsFragment
import com.redc4ke.taniechlanie.ui.popup.AvailabilitySubmitFragment
import java.math.BigDecimal


class DetailsShopAdapter(
    private val alcoObject: AlcoObject,
    private val shops: Map<Int, Shop>,
    private val detailsFragment: DetailsFragment
) :
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
            holder.vb.sheet2priceTV.text = priceString(
                alcoObject
                    .shopToPrice[shopId] ?: BigDecimal.valueOf(0),
                detailsFragment.requireContext()
            )

            val popup = PopupMenu(detailsFragment.requireContext(), holder.vb.sheet2editBT)
            popup.menuInflater.inflate(R.menu.availability_menu, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.availability_editprice -> {
                        AvailabilitySubmitFragment(detailsFragment, shopId)
                            .show(detailsFragment.parentFragmentManager, "av_submit")
                        true
                    }
                    else -> false
                }
            }

            holder.vb.sheet2editBT.setOnClickListener { popup.show() }
        } else {
            holder.vb.sheet2priceTV.apply {
                text = context.getString(R.string.details_addprice)
                setTextColor(context.getColor(R.color.primaryLightColor))
            }
            holder.vb.sheet2editBT.visibility = View.GONE
            holder.vb.sheet2priceTV.setOnClickListener {
                AvailabilitySubmitFragment(detailsFragment, shopId)
                    .show(detailsFragment.parentFragmentManager, "av_submit")
            }
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

class DetailsShopViewHolder(var vb: RowSheet2Binding) : RecyclerView.ViewHolder(vb.root)