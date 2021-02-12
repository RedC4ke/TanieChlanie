package com.redc4ke.taniechlanie.data.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.databinding.RowAboutExpandedBinding
import com.redc4ke.taniechlanie.ui.MainActivity

class AboutSubrowAdapter(private val data: ArrayList<Subrow>, private val frag: Fragment):
    RecyclerView.Adapter<AboutSubrowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutSubrowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAboutExpandedBinding.inflate(inflater, parent, false)

        return  AboutSubrowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AboutSubrowViewHolder, position: Int) {
        val item = data[position]
        val vb = holder.vb

        if (item.drawable != null) {
            vb.aboutSubrowIV.setBackgroundResource(item.drawable)
        } else {
            vb.aboutSubrowIV.visibility = View.GONE
        }
        vb.aboutSubrowHeaderTV.text = item.header
        vb.aboutSubrowTextTV.text = item.text
        vb.root.setOnClickListener {
            val act = frag.requireActivity() as MainActivity
            act.openBrowser(item.url)
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class AboutSubrowViewHolder(var vb: RowAboutExpandedBinding): RecyclerView.ViewHolder(vb.root)

data class Subrow(
    val drawable: Int?,
    val header: String,
    val text: String,
    val url: String)