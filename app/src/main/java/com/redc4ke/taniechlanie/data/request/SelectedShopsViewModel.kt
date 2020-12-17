package com.redc4ke.taniechlanie.data.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedShopsViewModel: ViewModel() {
    private val selectedShops = MutableLiveData<ArrayList<Int>>()
    private val tempList = arrayListOf<Int>()

    fun add(id: Int) {
        if (id !in tempList)
            tempList.add(id)
            selectedShops.value = tempList
    }

    fun remove(id: Int) {
        if (id in tempList)
            tempList.remove(id)
            selectedShops.value = tempList
    }

    fun get(): LiveData<ArrayList<Int>> {
        return  selectedShops
    }

    fun isAdded(id: Int):Boolean {
        return id in tempList
    }
}