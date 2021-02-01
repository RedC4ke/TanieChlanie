package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.Shop

class ShopViewModel: ViewModel() {
    private val shopLiveData = MutableLiveData<Map<Int, Shop>>()
    private val tempList = mutableMapOf<Int, Shop>()

    fun add (shop: Shop) {
        tempList[shop.id] = shop
        shopLiveData.value = tempList
    }

    fun getData(): LiveData<Map<Int, Shop>> {
        return shopLiveData
    }
}