package com.redc4ke.taniechlanie.data.request

import android.view.LayoutInflater
import android.view.ViewGroup
import com.redc4ke.taniechlanie.databinding.FragmentRequestBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class RequestFragment : BaseFragment<FragmentRequestBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestBinding
        get() = FragmentRequestBinding::inflate

}