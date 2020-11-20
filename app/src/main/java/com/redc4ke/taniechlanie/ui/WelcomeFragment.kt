package com.redc4ke.taniechlanie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.redc4ke.taniechlanie.R
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onStart() {
        start_BT.setOnClickListener {
            dismiss()
        }

        super.onStart()
    }

}