package com.redc4ke.taniechlanie.data.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.data.BaseRecyclerViewAdapter
import com.redc4ke.taniechlanie.databinding.RowAboutExpandedBinding
import com.redc4ke.taniechlanie.ui.MainActivity

class AboutSubrowAdapter(private val data: ArrayList<Subrow>, private val frag: Fragment):
    BaseRecyclerViewAdapter<AboutSubrowViewHolder, RowAboutExpandedBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowAboutExpandedBinding
        get() = RowAboutExpandedBinding::inflate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutSubrowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        _binding = bindingInflater.invoke(inflater, parent, false)

        return  AboutSubrowViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AboutSubrowViewHolder, position: Int) {
        val item = data[position]
        with (binding) {
            if (item.drawable != null) {
                aboutSubrowIV.setBackgroundResource(item.drawable)
            } else {
                aboutSubrowIV.visibility = View.GONE
            }
            aboutSubrowHeaderTV.text = item.header
            aboutSubrowTextTV.text = item.text

            root.setOnClickListener {
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