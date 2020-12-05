package com.redc4ke.taniechlanie.data.about

import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.ui.MainActivity
import kotlinx.android.synthetic.main.row_about.view.*
import kotlinx.android.synthetic.main.row_about_expanded.view.*

class AboutRecyclerViewAdapter(private val frag: Fragment):
    RecyclerView.Adapter<AboutViewHolder>() {

    private var expanded = arrayListOf(false, false)
    private val context = frag.requireContext()
    private val headers = context.resources.getStringArray(R.array.about_header)
    private val descriptions =  context.resources.getStringArray(R.array.about_description)
    private val subrows = arrayListOf(
        arrayListOf(
                Subrow(R.drawable.github_mark_120px_plus,
                        "GitHub", "Przejdź", "https://github.com/RedC4ke"),
                Subrow(R.drawable.ko_fi_icon_rgb_stroke,
                        "Ko-fi", "Przejdź", "https://ko-fi.com/redc4ke")
        ),
        arrayListOf(
                Subrow(
                        R.drawable.github_mark_120px_plus,
                        "TanieChlanie-issues", "Przejdź",
                        "https://github.com/RedC4ke/TanieChlanie-issues"
                )
        )
    )

    
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
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.bottomMargin = 26
            }
            divider.visibility = View.GONE
        }

        when (position) {
            0,1 -> addExpandable(view, subrows[position], position)
            2 -> view.about_row_mainCL.setOnClickListener {
                (frag.requireActivity() as MainActivity)
                        .openBrowser("https://taniechlanie.ml/regulamin")
            }
        }

        when (position) {
            2 -> view.about_row_subrow.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return headers.size
    }

    private fun addExpandable(view: View, itemList: ArrayList<Subrow>, position: Int) {
        val rv = view.about_row_RV
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = AboutRowAdapter(itemList, frag)

        view.about_row_mainCL.setOnClickListener {
            expanded[position] = !expanded[position]
            notifyItemChanged(position)
        }

        view.about_row_subrow.visibility =
                if (expanded[position]) View.VISIBLE else View.GONE
    }
}

class AboutViewHolder(val view: View): RecyclerView.ViewHolder(view)






class AboutRowAdapter(private val data: ArrayList<Subrow>, private val frag: Fragment):
    RecyclerView.Adapter<AboutRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutRowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_about_expanded, parent, false)

        return  AboutRowViewHolder(row)
    }

    override fun onBindViewHolder(holder: AboutRowViewHolder, position: Int) {
        val view = holder.view
        val item = data[position]
        with (view) {
            about_subrow_IV.setBackgroundResource(item.drawable)
            about_subrow_headerTV.text = item.header
            about_subrow_textTV.text = item.text

            setOnClickListener {
                val act = frag.requireActivity() as MainActivity
                act.openBrowser(item.url)
            }
        }

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