package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.databinding.RowAlcoholBinding
import com.redc4ke.taniechlanie.ui.menu.MenuFragment


class AlcoListAdapter(
        private var data: ArrayList<AlcoObject>,
        private val fragment: MenuFragment
) : BaseRecyclerViewAdapter<AlcoViewHolder, RowAlcoholBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowAlcoholBinding
        get() = RowAlcoholBinding::inflate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        _binding = bindingInflater.invoke(inflater, parent, false)

        return AlcoViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AlcoViewHolder, position: Int) {

        with (binding) {
            if (position == 0) {
                holder.view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    //this.topMargin = 30
                }
            }

            //Set views for this row
            nameTV.text = data[position].name
            priceTV.text = priceString(data[position], fragment)
            volumeTV.text = volumeString(data[position], fragment)
            voltageTV.text = voltageString(data[position], fragment)

            val id = data[position].id
            holder.view.transitionName = "rowAlcoholTransitionName_$id"

            holder.view.setOnClickListener {
                fragment.onItemClick(holder.view, data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setFilter (f: ArrayList<AlcoObject>) {
        data = f
        notifyDataSetChanged()
    }

    fun update (vm: AlcoObjectViewModel) {
        data = vm.getAll().value as ArrayList<AlcoObject>
        notifyDataSetChanged()
    }

}


class AlcoViewHolder(val view: View) : RecyclerView.ViewHolder(view)




