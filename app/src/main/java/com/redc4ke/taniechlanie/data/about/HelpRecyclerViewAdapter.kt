package com.redc4ke.taniechlanie.data.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.AnimationUtils
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.ui.MainActivity
import kotlinx.android.synthetic.main.row_about_help.view.*

class HelpRecyclerViewAdapter(private val frag: Fragment): RecyclerView.Adapter<HelpViewHolder>() {
    private val faq = (frag.activity as MainActivity).faq
    private val expanded = arrayListOf<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.row_about_help, parent, false)

        faq.forEach{ _ ->
            expanded.add(false)
        }

        return HelpViewHolder(row)
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        val view = holder.view

        view.help_row_headerTV.text = faq[position]["question"]
        view.help_row_textTV.text = faq[position]["answer"]

        view.help_row_mainCL.setOnClickListener {
            expanded[position] = !expanded[position]

            //Animation
            val deg: Float = if (expanded[position]) 90F
            else 0F
            view.help_row_IV.animate().rotation(deg).setDuration(200).start()

            view.help_row_subrow.visibility = if (expanded[position]) View.VISIBLE
            else View.GONE
        }

        view.help_row_subrow.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return faq.size
    }

}

class HelpViewHolder(val view: View): RecyclerView.ViewHolder(view)