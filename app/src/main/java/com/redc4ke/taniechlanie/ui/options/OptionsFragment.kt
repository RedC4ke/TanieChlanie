package com.redc4ke.taniechlanie.ui.options

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.redc4ke.taniechlanie.R
import kotlinx.android.synthetic.main.fragment_options.*
import kotlin.math.round

class OptionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optionPickUnit()
    }

    private fun optionPickUnit() {
        val spinner = options_unit_SPINNER
        val list = arrayListOf("R", "mR")
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, list)
        spinner.adapter = adapter
        val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val rounded = prefs.getBoolean("rounded_mR", false)
        val selection = if (rounded) 0 else 1
        spinner.setSelection(selection)

        spinner.onItemSelectedListener =  object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = position == 0
                prefs.edit().putBoolean("rounded_mR", selected).apply()
                Log.d("huj", selected.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}