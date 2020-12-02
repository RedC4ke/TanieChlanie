package com.redc4ke.taniechlanie.data.about

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import kotlinx.android.synthetic.main.row_about.view.*

class AboutRecyclerViewAdapter(private val context: Context):
    RecyclerView.Adapter<AboutViewHolder>() {
    
    private val headers = context.resources.getStringArray(R.array.about_header)
    private val descriptions =  context.resources.getStringArray(R.array.about_description)
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_about, parent, false)
        
        return  AboutViewHolder(row)
    }

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) {
        val view = holder.view
        view.about_row_IV.apply {
            setBackgroundResource(when (position) {
                0 -> R.drawable.ic_baseline_person_outline_24
                1 -> R.drawable.ic_outline_bug_report_24
                2 -> R.drawable.ic_outline_policy_24
                else -> R.drawable.ic_baseline_error_outline_24
            })
        }
        view.about_row_headerTV.text = headers[position]
        view.about_row_descriptionTV.text = descriptions[position]

        if (position == headers.size-1) {
            val divider = view.about_row_divider
            (divider.parent as ViewGroup).removeView(divider)
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.bottomMargin = 26
            }
        }

        if (position == 0) {
            val creditsList = arrayListOf<Subrow>(
                Subrow(R.drawable.github_mark_light_120px_plus,
                    "GitHub", "Redc4ke", "https://github.com/RedC4ke"),
                Subrow(R.drawable.ko_fi_icon_rgbfordarkbg,
                    "Ko-fi", "Redc4ke", ""))
            )

            val rv = view.about_row_RV
            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = AboutRowAdapter()
        }
    }

    override fun getItemCount(): Int {
        return headers.size
    }
}

class AboutViewHolder(val view: View): RecyclerView.ViewHolder(view)

class AboutRowAdapter(private val data: ArrayList<Subrow>):
    RecyclerView.Adapter<AboutRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutRowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_about_expanded, parent, false)

        return  AboutRowViewHolder(row)
    }

    override fun onBindViewHolder(holder: AboutRowViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class AboutRowViewHolder(val view: View): RecyclerView.ViewHolder(view)

data class Subrow(
    val drawable: Int,
    val header: String,
    val text: String,
    val url: String)