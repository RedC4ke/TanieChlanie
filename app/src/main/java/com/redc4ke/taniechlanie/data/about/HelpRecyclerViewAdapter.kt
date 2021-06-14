package com.redc4ke.taniechlanie.data.about

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.databinding.RowAboutHelpBinding
import com.redc4ke.taniechlanie.ui.MainActivity

class HelpRecyclerViewAdapter(frag: Fragment) :
        RecyclerView.Adapter<HelpViewHolder>() {

    private val faq = (frag.activity as MainActivity).faq
    private val expanded = arrayListOf<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAboutHelpBinding.inflate(inflater, parent, false)

        faq.forEach{ _ ->
            expanded.add(false)
        }

        return HelpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        with(holder.vb) {

            helpRowHeaderTV.text = faq[position]["question"]
            helpRowTextTV.text = faq[position]["answer"]

            helpRowMainCL.setOnClickListener {
                expanded[position] = !expanded[position]

                //Animation
                val deg: Float = if (expanded[position]) 90F
                else 0F
                helpRowIV.animate().rotation(deg).setDuration(200).start()

                helpRowSubrow.visibility = if (expanded[position]) View.VISIBLE
                else View.GONE
            }

            helpRowSubrow.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return faq.size
    }

}

class HelpViewHolder(var vb: RowAboutHelpBinding): RecyclerView.ViewHolder(vb.root)