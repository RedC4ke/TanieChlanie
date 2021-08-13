package com.redc4ke.taniechlanie.data.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.FirebaseListener
import com.redc4ke.taniechlanie.data.Shop
import java.io.File
import java.lang.reflect.Array
import java.math.BigDecimal
import java.util.*

class RequestViewModel : ViewModel() {

    private object Request {
        val shop = MutableLiveData<Shop>()
        var shopIsNew = false
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

    fun setShop(shop: Shop, isNew: Boolean = false) {
        Request.shop.value = shop
        Request.shopIsNew = isNew
    }

    fun getShop(): MutableLiveData<Shop> {
        return Request.shop
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

    fun upload(listener: FirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            listener.onComplete(FirebaseListener.NOT_LOGGED_IN)
            return
        }
        if (Request.categories.value?.values?.toTypedArray()?.distinct()?.size
            != Request.categories.value?.size) {
            listener.onComplete(FirebaseListener.REPEATING_CATEGORIES)
            return
        }
        if (Request.image != null) {
            val storageRef =
                FirebaseStorage.getInstance().reference.child("itemPhotos/${UUID.randomUUID()}.jpg")
            storageRef.putFile(Uri.fromFile(Request.image))
                .addOnFailureListener {
                    listener.onComplete(FirebaseListener.OTHER)
                }
                .addOnSuccessListener {
                    uploadData(listener, user, "gs://jabot-5ce1f.appspot.com" + storageRef.path)
                }
        } else {
            uploadData(listener, user, null)
        }

    }

    private fun uploadData(
        listener: FirebaseListener,
        user: FirebaseUser,
        photoURL: String?
    ) {
        val firestoreRef = FirebaseFirestore.getInstance().collection("requests")
            .document("requests").collection("newBooze")

        val categories = Request.categories.value!!.values.map { it.id }.toMutableList()
        if (Request.majorCategory != null) {
            categories.add(Request.majorCategory!!.id)
        } else {
            listener.onComplete(FirebaseListener.OTHER)
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
                "shop" to hashMapOf(
                    (Request.shop.value?.id?.toString() ?: "null") to Request.shop.value?.name
                ),
                "shopIsNew" to Request.shopIsNew,
                "price" to Request.price!!.toDouble(),
                "status" to RequestStatus.PENDING
            )
        )
            .addOnFailureListener {
                listener.onComplete(FirebaseListener.OTHER)
            }
            .addOnSuccessListener {
                listener.onComplete(FirebaseListener.SUCCESS)
            }
    }

}

object RequestStatus {
    const val PENDING = 1
    const val APPROVED = 2
    const val DECLINED = 3
}