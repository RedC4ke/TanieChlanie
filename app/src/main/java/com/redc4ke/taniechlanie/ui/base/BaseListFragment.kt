package com.redc4ke.taniechlanie.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.redc4ke.taniechlanie.data.AlcoObject

abstract class BaseListFragment<VB: ViewBinding> : BaseFragment<VB>() {
    abstract val alcoObjectList: MutableLiveData<List<AlcoObject>>
}
