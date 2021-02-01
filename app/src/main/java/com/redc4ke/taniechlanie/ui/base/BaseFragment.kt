package com.redc4ke.taniechlanie.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding>: Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)

        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun setTransitions(enter: Int?, exit: Int?) {
        val inflater = TransitionInflater.from(requireContext())
        val enterTrans =
            if (enter != null) inflater.inflateTransition(enter)
            else null
        val exitTrans =
            if (exit != null) inflater.inflateTransition(exit)
            else null

        enterTransition = enterTrans
        exitTransition = exitTrans
        returnTransition = exitTrans
        allowEnterTransitionOverlap = true
        allowReturnTransitionOverlap = true

    }
}