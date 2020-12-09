package com.redc4ke.taniechlanie.data

import android.os.Environment
import android.util.Log
import com.redc4ke.taniechlanie.ui.MainActivity
import java.io.File

class Categories(private val activity: MainActivity) {
    private val categories: MutableMap<Int, Category> = mutableMapOf()

    fun add(id: Int, name: String, imageUrl: String) {
        val dir = File(activity.applicationContext.filesDir, "/categories")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val imageFile = File(dir, "$name.jpg")
        activity.storage.getReferenceFromUrl(imageUrl).getFile(imageFile)
            .addOnSuccessListener {
                categories[id] = Category(id, name, imageFile)
            }
            .addOnFailureListener {
                categories[id] = Category(id, name, null)
            }


    }

    fun get(id: Int): Category? {
        return categories[id]
    }

    fun getAll(): ArrayList<Category> {
        val list: ArrayList<Category> = arrayListOf()
        categories.forEach {
            list.add(it.value)
        }
        return list
    }

    fun size(): Int {
        return categories.size
    }

    data class Category(
            val id: Int,
            val name: String,
            val image: File?
    )

}

