package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.ui.menu.MenuFragment
import kotlinx.android.synthetic.main.row_alcohol.view.*


class AlcoListAdapter(
        private var data: ArrayList<AlcoObject>,
        private val fragment: MenuFragment
) : RecyclerView.Adapter<AlcoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_alcohol, parent, false)

        return AlcoViewHolder(row)
    }

    override fun onBindViewHolder(holder: AlcoViewHolder, position: Int) {
        //Assign views to variables
        val name = holder.view.name_TV
        val price = holder.view.price_TV
        val volume = holder.view.volume_TV
        val voltage = holder.view.voltage_TV

        if (position == 0) {
            holder.view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = 30
            }
        }

        //Set views for this row
        name.text = data[position].name
        price.text = priceString(data[position], fragment)
        volume.text = volumeString(data[position], fragment)
        voltage.text = voltageString(data[position], fragment)

        val id = data[position].id
        holder.view.transitionName = "rowAlcoholTransitionName_$id"

        holder.view.setOnClickListener {
            fragment.onItemClick(holder.view, data[position])
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




