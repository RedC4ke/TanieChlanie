package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.databinding.RowAlcoholBinding
import com.redc4ke.taniechlanie.ui.menu.MenuFragment


class AlcoListAdapter(
        private var data: ArrayList<AlcoObject>,
        val fragment: MenuFragment) : RecyclerView.Adapter<AlcoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAlcoholBinding.inflate(inflater, parent, false)

        return AlcoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlcoViewHolder, position: Int) {

        with (holder.vb) {
            if (position == 0) {
                root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    //this.topMargin = 30
                }
            }

            //Set views for this row
            nameTV.text = data[position].name
            priceTV.text = priceString(data[position], fragment)
            volumeTV.text = volumeString(data[position], fragment)
            voltageTV.text = voltageString(data[position], fragment)

            val id = data[position].id
            root.transitionName = "rowAlcoholTransitionName_$id"

            root.setOnClickListener {
                fragment.onItemClick(root, data[position])
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


class AlcoViewHolder(var vb: RowAlcoholBinding) : RecyclerView.ViewHolder(vb.root)




