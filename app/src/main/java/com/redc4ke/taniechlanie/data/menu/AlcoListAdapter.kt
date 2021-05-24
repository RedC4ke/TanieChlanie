package com.redc4ke.taniechlanie.data.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.databinding.RowAlcoholBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.menu.AlcoListFragment
import com.redc4ke.taniechlanie.ui.menu.MenuFragment


class AlcoListAdapter(
        private var data: List<AlcoObject>,
        private val context: MainActivity,
        private val parentFrag: AlcoListFragment) : RecyclerView.Adapter<AlcoViewHolder>() {

    private val categoryViewModel = ViewModelProvider(context).get(CategoryViewModel::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAlcoholBinding.inflate(inflater, parent, false)

        return AlcoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlcoViewHolder, position: Int) {

        with (holder.vb) {
            //Set views for this row
            nameTV.text = autoBreak(data[position].name)
            priceTV.text = priceString(data[position], context)
            valueTV.text = valueString(data[position], context)
            categoryViewModel.get().observe((context), {
                val image = categoryViewModel.getMajor(data[position])?.image
                Glide.with(context).load(image).into(categoryIV)
            })

            val id = data[position].id
            root.transitionName = "rowAlcoholTransitionName_$id"

            root.setOnClickListener {
                parentFrag.onItemClick(root, data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun update (list: List<AlcoObject>) {
        data = list
        notifyDataSetChanged()
    }

}


class AlcoViewHolder(var vb: RowAlcoholBinding) : RecyclerView.ViewHolder(vb.root)




