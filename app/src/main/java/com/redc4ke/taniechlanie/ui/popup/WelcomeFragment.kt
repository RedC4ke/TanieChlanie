package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.databinding.FragmentWelcomeBinding
import com.redc4ke.taniechlanie.ui.BaseDialogFragment

class WelcomeFragment : BaseDialogFragment<FragmentWelcomeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWelcomeBinding
        get() = FragmentWelcomeBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Disable dismissing when clicked outside of the fragment
        this.isCancelable = false
    }

    override fun onStart() {
        binding.welcomeRulesTV.movementMethod = LinkMovementMethod()

        binding.startBT.setOnClickListener {
            if (binding.welcomeRulesCHB.isChecked) dismiss()
            else binding.welcomeWarningTV.visibility = View.VISIBLE
        }

        super.onStart()
    }

}