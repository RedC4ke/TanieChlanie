package com.redc4ke.taniechlanie.data.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.autoBreak
import com.redc4ke.taniechlanie.data.lowestPriceString
import com.redc4ke.taniechlanie.data.valueString
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.databinding.RowAlcoholBinding
import com.redc4ke.taniechlanie.ui.AlcoListFragment
import com.redc4ke.taniechlanie.ui.MainActivity


class AlcoListAdapter(
    private var data: List<AlcoObject>,
    private val context: MainActivity,
    private val parentFrag: AlcoListFragment
) :
    RecyclerView.Adapter<AlcoViewHolder>() {

    private val categoryViewModel = ViewModelProvider(context).get(CategoryViewModel::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAlcoholBinding.inflate(inflater, parent, false)

        return AlcoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlcoViewHolder, position: Int) {

        with(holder.vb) {

            //Set views for this row
            nameTV.text = autoBreak(data[position].name)
            priceTV.text = lowestPriceString(data[position], context)
            valueTV.text = valueString(data[position], context)

            val id = data[position].id
            root.transitionName = "rowAlcoholTransitionName_$id"

            root.setOnClickListener {
                parentFrag.onItemClick(root, data[position])
            }

            categoryViewModel.getAll().observe((context), {
                //Temp solution, why is sometimes data.size larger than position?
                if (position < data.size) {
                    val image = categoryViewModel.getMajor(data[position].categories)?.image

                    Glide.with(context).load(image).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(categoryIV)
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun update(list: List<AlcoObject>) {
        data = list
        notifyDataSetChanged()
    }

}


class AlcoViewHolder(var vb: RowAlcoholBinding) : RecyclerView.ViewHolder(vb.root)




