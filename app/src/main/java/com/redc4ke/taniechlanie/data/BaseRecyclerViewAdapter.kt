package com.redc4ke.taniechlanie.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewAdapter<VH: RecyclerView.ViewHolder, VB: ViewBinding> :
        RecyclerView.Adapter<VH>() {

    protected var _binding: VB? = null
    protected val binding get() = _binding!!
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _binding = null
    }
}