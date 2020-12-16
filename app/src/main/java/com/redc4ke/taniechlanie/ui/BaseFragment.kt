package com.redc4ke.taniechlanie.ui

import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater

open class BaseFragment: Fragment() {

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