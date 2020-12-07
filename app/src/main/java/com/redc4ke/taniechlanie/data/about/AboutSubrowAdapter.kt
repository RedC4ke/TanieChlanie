package com.redc4ke.taniechlanie.data.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.ui.MainActivity
import kotlinx.android.synthetic.main.row_about_expanded.view.*

class AboutSubrowAdapter(private val data: ArrayList<Subrow>, private val frag: Fragment):
    RecyclerView.Adapter<AboutSubrowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutSubrowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_about_expanded, parent, false)

        return  AboutSubrowViewHolder(row)
    }

    override fun onBindViewHolder(holder: AboutSubrowViewHolder, position: Int) {
        val view = holder.view
        val item = data[position]
        with (view) {
            if (item.drawable != null) {
                about_subrow_IV.setBackgroundResource(item.drawable)
            } else {
                about_subrow_IV.visibility = View.GONE
            }
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

class AboutSubrowViewHolder(val view: View): RecyclerView.ViewHolder(view)

data class Subrow(
    val drawable: Int?,
    val header: String,
    val text: String,
    val url: String)