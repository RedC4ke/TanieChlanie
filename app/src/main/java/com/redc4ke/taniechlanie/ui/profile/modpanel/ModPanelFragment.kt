package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.view.LayoutInflater
import android.view.ViewGroup
import com.redc4ke.taniechlanie.databinding.FragmentModPanelBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ModPanelFragment : BaseFragment<FragmentModPanelBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentModPanelBinding
        get() = FragmentModPanelBinding::inflate

}