package com.redc4ke.taniechlanie.data.profile.modpanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectRequest
import com.redc4ke.taniechlanie.databinding.RowAlcoholBinding
import com.redc4ke.taniechlanie.ui.profile.modpanel.NewBoozeListFragment

class NewBoozeListAdapter(
    private val parentFrag: NewBoozeListFragment,
    private val categoryViewModel: CategoryViewModel
) :
    RecyclerView.Adapter<NewBoozeListViewHolder>() {

    private var requestList = listOf<AlcoObjectRequest>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBoozeListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAlcoholBinding.inflate(inflater, parent, false)

        return NewBoozeListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewBoozeListViewHolder, position: Int) {
        with(holder.vb) {
            val request = requestList[position]
            nameTV.text = request.name
            priceTV.text = request.price.toString()
            valueTV.text = ""
            categoryViewModel.getAll().observe((parentFrag), {
                val image =
                    categoryViewModel.getMajor(requestList[position].categories ?: listOf())?.image
                Glide.with(parentFrag).load(image).into(categoryIV)
            })

            root.setOnClickListener {
                parentFrag.onItemClick(requestList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    fun update(data: List<AlcoObjectRequest>) {
        requestList = data
        notifyDataSetChanged()
    }

}

class NewBoozeListViewHolder(val vb: RowAlcoholBinding) : RecyclerView.ViewHolder(vb.root)