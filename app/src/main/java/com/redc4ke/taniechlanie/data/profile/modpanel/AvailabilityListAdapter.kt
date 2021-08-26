package com.redc4ke.taniechlanie.data.profile.modpanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.priceString
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.AvailabilityRequest
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.databinding.RowAlcoholBinding
import com.redc4ke.taniechlanie.ui.profile.modpanel.AvailabilityListFragment
import com.redc4ke.taniechlanie.ui.profile.modpanel.AvailabilityListFragmentDirections

class AvailabilityListAdapter(
    private val parentFrag: AvailabilityListFragment,
    private val alcoObjectViewModel: AlcoObjectViewModel,
    private val categoryViewModel: CategoryViewModel
) : RecyclerView.Adapter<AvailabilityListViewHolder>() {

    private var requestList: List<AvailabilityRequest> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailabilityListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAlcoholBinding.inflate(inflater, parent, false)

        return AvailabilityListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvailabilityListViewHolder, position: Int) {
        val alcoObjectId = requestList[position].alcoObjectId
        val alcoObject = alcoObjectViewModel.get(alcoObjectId)

        with(holder.vb) {
            nameTV.text = alcoObject?.name
            priceTV.text = alcoObject?.shopToPrice?.values?.toList()?.get(0).toString()
            valueTV.text = ""
            categoryViewModel.getAll().observe((parentFrag), {
                val image =
                    categoryViewModel.getMajor(alcoObject?.categories ?: listOf())?.image
                Glide.with(parentFrag).load(image).into(categoryIV)
            })

            root.setOnClickListener {
                val directions =
                    AvailabilityListFragmentDirections.openAvailabilityDetails(requestList[position])
                parentFrag.findNavController().navigate(directions)
            }
        }
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    fun update(list: List<AvailabilityRequest>) {
        requestList = list
        notifyDataSetChanged()
    }
}

class AvailabilityListViewHolder(val vb: RowAlcoholBinding) :
    RecyclerView.ViewHolder(vb.root)