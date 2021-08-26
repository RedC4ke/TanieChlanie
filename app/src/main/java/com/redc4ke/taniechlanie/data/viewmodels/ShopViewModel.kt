package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.data.FirebaseListener
import com.redc4ke.taniechlanie.data.Shop

class ShopViewModel : ViewModel() {
    private val shopLiveData = MutableLiveData<Map<Int, Shop>>()
    private val tempMap = mutableMapOf<Int, Shop>()
    private var lastId = 0

    fun add(shop: Shop) {
        tempMap[shop.id!!] = shop
        shopLiveData.value = tempMap
    }

    fun getLastId(): Int {
        lastId = shopLiveData.value?.values?.sortedBy { it.id }?.last()?.id!!
        return lastId
    }

    fun getData(): LiveData<Map<Int, Shop>> {
        return shopLiveData
    }

    fun fetch(firestoreRef: FirebaseFirestore, firebaseListener: FirebaseListener?) {
        firestoreRef.collection("shops").orderBy("id").get()
            .addOnSuccessListener {
                tempMap.clear()
                shopLiveData.value = mapOf()
                it.forEach { document ->
                    add(Shop(
                        document.getLong("id")?.toInt(),
                        document.getString("name") ?: ""))
                }
                firebaseListener?.onComplete(FirebaseListener.SUCCESS)
            }
            .addOnFailureListener {
                firebaseListener?.onComplete(FirebaseListener.OTHER)
            }
    }
}