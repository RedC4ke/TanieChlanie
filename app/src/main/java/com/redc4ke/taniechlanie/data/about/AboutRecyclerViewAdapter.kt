package com.redc4ke.taniechlanie.data.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.databinding.RowAboutBinding
import com.redc4ke.taniechlanie.ui.MainActivity

class AboutRecyclerViewAdapter(private val frag: Fragment):
    RecyclerView.Adapter<AboutViewHolder>() {

    private val act = frag.requireActivity() as MainActivity
    private val context = frag.requireContext()
    private val expanded = arrayListOf<Boolean>()
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
            Subrow(R.drawable.github_mark_120px_plus,
                "TanieChlanie-issues", "Przejdź",
                "https://github.com/RedC4ke/TanieChlanie-issues")
        ),
        arrayListOf(
            Subrow(null,
                "Freepik",
                "www.flaticon.com",
                "https://www.flaticon.com/authors/freepik"),
            Subrow(null,
                "Pixel perfect",
                "www.flaticon.com",
                "https://www.flaticon.com/authors/pixel-perfect"),
            Subrow(null,
                "Smashicons",
                "www.flaticon.com",
                "https://www.flaticon.com/authors/smashicons"),
            Subrow(null,
                "Nhor Phai",
                "www.flaticon.com",
                "https://www.flaticon.com/authors/nhor-phai"),
            Subrow(null,
                "monkik",
                "www.flaticon.com",
                "https://www.flaticon.com/authors/monkik"),
            Subrow(null,
                "Linector",
                "www.flaticon.com",
                "https://www.flaticon.com/authors/linector"),
        ),
        arrayListOf()
    )

    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowAboutBinding.inflate(inflater, parent, false)

        headers.forEach { _ ->
            expanded.add(false)
        }
        
        return  AboutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) {

        val binding = holder.vb

        binding.aboutRowDivider.visibility =
                if (position == 4) View.INVISIBLE
                else View.VISIBLE

        binding.aboutRowIV.apply {
            setBackgroundResource(when (position) {
                0 -> R.drawable.ic_baseline_person_outline_24
                1 -> R.drawable.ic_outline_bug_report_24
                2 -> R.drawable.ic_outline_design_services_24
                3 -> R.drawable.ic_outline_policy_24
                4 -> R.drawable.ic_baseline_help_outline_24
                else -> R.drawable.ic_baseline_error_outline_24
            })
        }
        binding.aboutRowHeaderTV.text = headers[position]
        binding.aboutRowDescriptionTV.text = descriptions[position]

        //Expandable or link
        when (position) {
            0,1,2 -> addExpandable(binding, subrows[position], position)
            3 -> binding.aboutRowMainCL.setOnClickListener {
                act.openBrowser("https://taniechlanie.ml/regulamin")
            }
            4 -> binding.aboutRowMainCL.setOnClickListener {
                frag.findNavController().navigate(R.id.to_help_dest)
            }
        }

    }

    override fun getItemCount(): Int {
        return headers.size
    }

    private fun addExpandable(vb: RowAboutBinding, itemList: ArrayList<Subrow>, position: Int) {
        val rv = vb.aboutRowRV
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = AboutSubrowAdapter(itemList, frag)

        vb.aboutRowMainCL.setOnClickListener {
            expanded[position] = !expanded[position]
            notifyItemChanged(position)
        }

        vb.aboutRowSubrow.visibility =
                if (expanded[position]) View.VISIBLE else View.GONE
    }
}

class AboutViewHolder(var vb: RowAboutBinding): RecyclerView.ViewHolder(vb.root)






