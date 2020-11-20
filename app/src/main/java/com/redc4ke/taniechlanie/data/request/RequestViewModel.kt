package com.redc4ke.taniechlanie.data.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.ItemCategory

class RequestViewModel: ViewModel() {
    //Categories
    private val itemCategoryList = MutableLiveData(mutableListOf<ItemCategory>())

    fun addCategory(cat: ItemCategory) {
        itemCategoryList.value?.add(cat)
    }

    fun getCategories(): MutableLiveData<MutableList<ItemCategory>> {
        return itemCategoryList
    }
}