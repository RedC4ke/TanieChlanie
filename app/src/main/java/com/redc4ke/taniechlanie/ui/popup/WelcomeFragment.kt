package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.redc4ke.taniechlanie.R
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Disable dismissing when clicked outside of the fragment
        this.isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onStart() {
        welcome_rulesTV.movementMethod = LinkMovementMethod()

        start_BT.setOnClickListener {
            if (welcome_rulesCHB.isChecked) dismiss()
            else welcome_warningTV.visibility = View.VISIBLE
        }

        super.onStart()
    }

}