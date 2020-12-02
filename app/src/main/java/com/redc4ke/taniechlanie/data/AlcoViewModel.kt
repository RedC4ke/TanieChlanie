package com.redc4ke.taniechlanie.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlcoViewModel: ViewModel() {
    private val tempList: MutableList<AlcoObject> = mutableListOf()
    private val alcoList = MutableLiveData(mutableListOf<AlcoObject>())

    fun addObject(o: AlcoObject) {
        tempList.add(o)
        alcoList.value = tempList
    }

    fun getAll(): MutableLiveData<MutableList<AlcoObject>> {
        return alcoList
    }

    fun addAll(o: MutableList<AlcoObject>) {
        alcoList.value = o
    }

    
}



