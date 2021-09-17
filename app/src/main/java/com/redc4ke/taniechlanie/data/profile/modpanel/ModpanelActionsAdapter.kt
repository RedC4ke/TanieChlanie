package com.redc4ke.taniechlanie.data.profile.modpanel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.databinding.RowProfileBinding
import com.redc4ke.taniechlanie.ui.profile.modpanel.ModPanelFragment

class ModpanelActionsAdapter(private val modPanelFragment: ModPanelFragment) :
    RecyclerView.Adapter<ModpanelActionsViewHolder>() {

    private val menu = modPanelFragment.resources.getStringArray(R.array.mod_actionlist)
    val sources = mutableListOf<List<Any>>(listOf(), listOf(), listOf(), listOf())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModpanelActionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowProfileBinding.inflate(inflater, parent, false)

        return ModpanelActionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModpanelActionsViewHolder, position: Int) {
        with(holder.vb) {
            profileRowNameTV.text = menu[position]

            if (sources[position].isNotEmpty()) {
                profileRowCounterCV.visibility = View.VISIBLE
                profileRowCounterTV.text = sources[position].size.toString()
            } else {
                profileRowCounterCV.visibility = View.GONE
            }

            profileRowFrameFL.setOnClickListener {
                when (position) {
                    0 -> {
                        modPanelFragment.findNavController().navigate(R.id.newBooze_dest)
                    }
                    2 -> {
                        modPanelFragment.findNavController().navigate(R.id.availability_dest)
                    }
                    3 -> {
                        modPanelFragment.findNavController().navigate(R.id.report_dest)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return menu.size
    }

    fun updateSources(data: List<Any>, position: Int) {
        sources[position] = data
        notifyItemChanged(position)
    }
}

class ModpanelActionsViewHolder(val vb: RowProfileBinding) : RecyclerView.ViewHolder(vb.root)