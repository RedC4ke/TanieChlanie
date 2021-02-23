package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.ui.MainActivity
import java.io.File

class CategoryViewModel: ViewModel() {
    private val categoryLiveData = MutableLiveData<Map<Int, Category>>()
    private val tempList = mutableMapOf<Int, Category>()

    fun add(id: Int, name: String, imageUrl: String, major: Boolean, activity: MainActivity) {
        val dir = File(activity.applicationContext.filesDir, "/categories")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val imageFile = File(dir, "$name.jpg")

        activity.storage.getReferenceFromUrl(imageUrl).getFile(imageFile)
            .addOnSuccessListener {
                tempList[id] = Category(id, name, imageFile, major)
                categoryLiveData.value = tempList
            }
            .addOnFailureListener {
                tempList[id] = Category(id, name, null, major)
                categoryLiveData.value = tempList
            }


    }

    fun get(): LiveData<Map<Int, Category>> {
        return categoryLiveData
    }

    fun getMajor(alcoObject: AlcoObject): Category? {
        val catList = alcoObject.categories
        catList.forEach {
            val cat = categoryLiveData.value?.get(it)
            if (cat?.major == true) {
                return cat
            }
        }
        return null
    }

    fun getWithMajor(alcoObject: AlcoObject): List<Category?> {
        val list = mutableListOf(getMajor(alcoObject))
        alcoObject.categories.forEach {
            val category = categoryLiveData.value?.get(it)
            if (category != null && category != list[0]) {
                list.add(category)
            }
        }

        return list
    }
}