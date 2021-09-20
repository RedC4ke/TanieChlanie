package com.redc4ke.taniechlanie.data.profile.modpanel

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.Report
import com.redc4ke.taniechlanie.data.viewmodels.UserData
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.RowAlcoholBinding
import com.redc4ke.taniechlanie.databinding.RowReportBinding
import com.redc4ke.taniechlanie.ui.profile.modpanel.ReportListFragment
import com.redc4ke.taniechlanie.ui.profile.modpanel.ReportListFragmentDirections
import java.text.DateFormat

class ReportListAdapter(
    private val parentFrag: ReportListFragment,
    private val userViewModel: UserViewModel
) : RecyclerView.Adapter<ReportListViewHolder>() {

    private var reportList = listOf<Report>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowReportBinding.inflate(inflater, parent, false)

        return ReportListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportListViewHolder, position: Int) {
        var userData: UserData? = null

        with(holder.vb) {
            userViewModel.getOtherUser(reportList[position].author)
                .observe(parentFrag.viewLifecycleOwner, {
                    userData = it
                    if (userData != null) {
                        rowReportNameTV.text = userData?.name
                        if (userData?.created != null) {
                            rowReportTimestampTV.text =
                                DateFormat.getDateInstance().format(userData!!.created!!.toDate())
                        }
                        if (userData?.avatar != null) {
                            Glide.with(parentFrag)
                                .load(userData!!.avatar)
                                .into(rowReportAvatarIV)
                        }
                    }
                })

            root.setOnClickListener {
                val directions =
                    ReportListFragmentDirections.actionReportDestToReportDetailsDest(reportList[position])
                parentFrag.findNavController().navigate(directions)
            }
        }
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    fun update(list: List<Report>) {
        reportList = list
        notifyDataSetChanged()
    }
}

class ReportListViewHolder(val vb: RowReportBinding) : RecyclerView.ViewHolder(vb.root)