package com.redc4ke.taniechlanie.data.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.data.Category
import java.io.File
import java.math.BigDecimal
import java.util.*

class RequestViewModel : ViewModel() {

    private object Request {
        val shopName = MutableLiveData<String>()
        var alcoholName: String? = null
        var volume: Int? = null
        var voltage: BigDecimal? = null
        var price: BigDecimal? = null
        var image: File? = null
        var majorCategory: Category? = null
        var categories = MutableLiveData<MutableMap<Int, Category>>().apply {
            this.value = mutableMapOf()
        }
    }

    private val photoName = MutableLiveData<String>()

    fun setShop(name: String) {
        Request.shopName.value = name
    }

    fun getShop(): MutableLiveData<String> {
        return Request.shopName
    }

    fun fillRequest(name: String, vm: Int, volt: BigDecimal, pc: BigDecimal) {
        with(Request) {
            alcoholName = name
            volume = vm
            voltage = volt
            price = pc
        }
    }

    fun setImage(file: File?) {
        Request.image = file
    }

    fun setPhotoName(name: String) {
        photoName.value = name
    }

    fun getPhotoName(): MutableLiveData<String> {
        return photoName
    }

    fun setMajorCat(category: Category) {
        Request.majorCategory = category
    }

    fun addCategory(index: Int, category: Category) {
        val catList = (Request.categories.value ?: mutableMapOf())
        if (catList[index] != category) {
            catList[index] = category
        }
        Request.categories.value = catList
    }

    fun deleteCategory(index: Int) {
        val catList = (Request.categories.value ?: mutableMapOf())
        catList.remove(index)
        val newList = catList.values.toList()
        Request.categories.value =
            newList.mapIndexed { ind, category -> ind to category }.toMap().toMutableMap()
    }

    fun getSelectedCategories(): LiveData<MutableMap<Int, Category>> {
        return Request.categories
    }

    fun upload(listener: RequestUploadListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            listener.onComplete(2)
            return
        }
        if (Request.image != null) {
            val storageRef =
                FirebaseStorage.getInstance().reference.child("pendingPhotos/${UUID.randomUUID()}.jpg")
            storageRef.putFile(Uri.fromFile(Request.image))
                .addOnFailureListener {
                    listener.onComplete(4)
                }
                .addOnSuccessListener {
                    uploadData(listener, user, "gs://jabot-5ce1f.appspot.com" + storageRef.path)
                }
        } else {
            uploadData(listener, user, null)
        }

    }

    private fun uploadData(
        listener: RequestUploadListener,
        user: FirebaseUser,
        photoURL: String?
    ) {
        val firestoreRef = FirebaseFirestore.getInstance().collection("pending")
            .document("pending").collection("newBooze")

        val categories = Request.categories.value!!.values.map { it.id }.toMutableList()
        if (Request.majorCategory != null) {
            categories.add(Request.majorCategory!!.id)
        } else {
            listener.onComplete(RequestUploadListener.OTHER)
            return
        }

        firestoreRef.add(
            hashMapOf(
                "author" to user.uid,
                "categories" to categories,
                "description" to null,
                "name" to Request.alcoholName,
                "voltage" to Request.voltage!!.toDouble(),
                "volume" to Request.volume,
                "photo" to photoURL,
                "price" to mapOf(Request.shopName.value to Request.price!!.toDouble())
            )
        )
            .addOnFailureListener {
                listener.onComplete(RequestUploadListener.OTHER)
            }
            .addOnSuccessListener {
                listener.onComplete(RequestUploadListener.SUCCESS)
            }
    }

}

interface RequestUploadListener {
    companion object {
        const val SUCCESS = 1
        const val NOT_LOGGED_IN = 2
        const val REPEATING_CATEGORIES = 3
        const val OTHER = 4
    }

    fun onComplete(resultCode: Int)
}