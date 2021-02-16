package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.AlcoObject

class AlcoObjectViewModel: ViewModel() {
    private val tempList: MutableList<AlcoObject> = mutableListOf()
    private val alcoList = MutableLiveData(listOf<AlcoObject>())

    fun addObject(o: AlcoObject) {
        tempList.add(o)
        alcoList.value = tempList
    }

    fun getAll(): MutableLiveData<List<AlcoObject>> {
        alcoList.value = alcoList.value?.sortedWith(compareBy { it.name })
        return alcoList
    }

    fun addAll(o: MutableList<AlcoObject>) {
        alcoList.value = o
    }

    
}



