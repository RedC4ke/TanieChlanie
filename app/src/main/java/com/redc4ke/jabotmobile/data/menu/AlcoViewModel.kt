package com.redc4ke.jabotmobile.data.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.Serializable

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

data class AlcoObject(
        val id: Int?,
        val name: String?,
        val minPrice: Float?,
        val maxPrice: Float?,
        val promoPrice: Float?,
        val volume: Int?,
        val voltage: Float?,
        val shop: ArrayList<String>?,
        val categories: ArrayList<Int>?
): Serializable

