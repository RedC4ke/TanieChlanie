package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.RequestListener

class CategoryViewModel : ViewModel() {
    private val categoryLiveData = MutableLiveData<Map<Int, Category>>()
    private val tempMap = mutableMapOf<Int, Category>()
    private val majorCategories = MutableLiveData<Map<Int, Category>>()
    private val notMajorCategories = MutableLiveData<Map<Int, Category>>()
    private val tempMajorMap = mutableMapOf<Int, Category>()
    private val tempNotMajorMap = mutableMapOf<Int, Category>()

    fun add(id: Int, name: String, imageUrl: String?, major: Boolean) {
        val cat = Category(id, name, imageUrl, major)
        tempMap[id] = cat
        categoryLiveData.value = tempMap

        if (major) {
            tempMajorMap[id] = cat
        } else {
            tempNotMajorMap[id] = cat
        }

        majorCategories.value = tempMajorMap
        notMajorCategories.value = tempNotMajorMap
    }

    fun getAll(): LiveData<Map<Int, Category>> {
        return categoryLiveData
    }

    fun getAllMajor(): LiveData<Map<Int, Category>> {
        return majorCategories
    }

    fun getAllNotMajor(): LiveData<Map<Int, Category>> {
        return notMajorCategories
    }

    fun getMajor(catList: List<Int>): Category? {
        catList.forEach {
            val cat = categoryLiveData.value?.get(it)
            if (cat?.major == true) {
                return cat
            }
        }
        return null
    }

    fun getWithMajorFirst(catList: List<Int>): List<Category?> {
        val list = mutableListOf(getMajor(catList))
        catList.forEach {
            val category = categoryLiveData.value?.get(it)
            if (category != null && category != list[0]) {
                list.add(category)
            }
        }

        return list
    }

    fun fetch(firestoreRef: FirebaseFirestore, requestListener: RequestListener) {
        firestoreRef.collection("categories").orderBy("id").get()
            .addOnSuccessListener {
                tempMajorMap.clear()
                tempMap.clear()
                it.forEach { document ->
                    val id = document.getLong("id")!!.toInt()
                    val name = document.getString("name")!!
                    val url = document.getString("image")
                    val major = document.getBoolean("major")!!

                    add(id, name, url, major)
                }
                requestListener.onComplete(RequestListener.SUCCESS)
            }
            .addOnFailureListener {
                requestListener.onComplete(RequestListener.OTHER)
            }
    }
}