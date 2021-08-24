package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.FirebaseListener
import java.io.File

class CategoryViewModel : ViewModel() {
    private val categoryLiveData = MutableLiveData<Map<Int, Category>>()
    private val tempMap = mutableMapOf<Int, Category>()
    private val majorCategories = MutableLiveData<Map<Int, Category>>()
    private val tempMajorMap = mutableMapOf<Int, Category>()

    fun add(id: Int, name: String, imageUrl: String?, major: Boolean, path: File) {

        val dir = File(path, "/categories")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val imageFile = File(dir, "$name.jpg")

        if (imageUrl != null) {
            FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).getFile(imageFile)
                .addOnSuccessListener {
                    val cat = Category(id, name, imageFile, major)
                    tempMap[id] = cat
                    categoryLiveData.value = tempMap
                    if (major) {
                        tempMajorMap[id] = cat
                    }
                    majorCategories.value = tempMajorMap
                }
                .addOnFailureListener {
                    val cat = Category(id, name, null, major)
                    tempMap[id] = cat
                    categoryLiveData.value = tempMap
                    if (major) {
                        tempMajorMap[id] = cat
                    }
                    majorCategories.value = tempMajorMap
                }
        }
    }

    fun getAll(): LiveData<Map<Int, Category>> {
        return categoryLiveData
    }

    fun getAllMajor(): LiveData<Map<Int, Category>> {
        return majorCategories
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

    fun fetch(firestoreRef: FirebaseFirestore, path: File, firebaseListener: FirebaseListener) {
        firestoreRef.collection("categories").orderBy("id").get()
            .addOnSuccessListener {
                tempMajorMap.clear()
                tempMap.clear()
                it.forEach { document ->
                    val id = document.getLong("id")!!.toInt()
                    val name = document.getString("name")!!
                    val url = document.getString("image")
                    val major = document.getBoolean("major")!!

                    add(id, name, url, major, path)
                }
                firebaseListener.onComplete(FirebaseListener.SUCCESS)
            }
            .addOnFailureListener {
                firebaseListener.onComplete(FirebaseListener.OTHER)
            }
    }
}