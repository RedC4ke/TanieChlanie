package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.ui.MainActivity
import java.io.File

class CategoryViewModel: ViewModel() {
    private val categoryLiveData = MutableLiveData<Map<Int, Category>>()
    private val tempList = mutableMapOf<Int, Category>()

    fun add(id: Int, name: String, imageUrl: String, activity: MainActivity) {
        val dir = File(activity.applicationContext.filesDir, "/categories")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val imageFile = File(dir, "$name.jpg")

        activity.storage.getReferenceFromUrl(imageUrl).getFile(imageFile)
            .addOnSuccessListener {
                tempList[id] = Category(id, name, imageFile)
                categoryLiveData.value = tempList
            }
            .addOnFailureListener {
                tempList[id] = Category(id, name, null)
                categoryLiveData.value = tempList
            }


    }

    fun get(): LiveData<Map<Int, Category>> {
        return categoryLiveData
    }
}