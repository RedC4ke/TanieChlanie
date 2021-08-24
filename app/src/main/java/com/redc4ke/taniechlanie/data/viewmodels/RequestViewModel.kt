package com.redc4ke.taniechlanie.data.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.FirebaseListener
import com.redc4ke.taniechlanie.data.Shop
import java.io.File
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

class RequestViewModel : ViewModel() {

    private object CurrentRequest {
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
    private val requestList = MutableLiveData<List<Pair<Int, AlcoObjectRequest>>>()

    fun setShop(shop: Shop, isNew: Boolean = false) {
        CurrentRequest.shop.value = shop
        CurrentRequest.shopIsNew = isNew
    }

    fun getShop(): MutableLiveData<Shop> {
        return CurrentRequest.shop
    }

    fun fillRequest(name: String, vm: Int, volt: BigDecimal, pc: BigDecimal) {
        with(CurrentRequest) {
            alcoholName = name
            volume = vm
            voltage = volt
            price = pc
        }
    }

    fun setImage(file: File?) {
        CurrentRequest.image = file
    }

    fun setPhotoName(name: String) {
        photoName.value = name
    }

    fun getPhotoName(): MutableLiveData<String> {
        return photoName
    }

    fun setMajorCat(category: Category) {
        CurrentRequest.majorCategory = category
    }

    fun addCategory(index: Int, category: Category) {
        val catList = (CurrentRequest.categories.value ?: mutableMapOf())
        if (catList[index] != category) {
            catList[index] = category
        }
        CurrentRequest.categories.value = catList
    }

    fun deleteCategory(index: Int) {
        val catList = (CurrentRequest.categories.value ?: mutableMapOf())
        catList.remove(index)
        val newList = catList.values.toList()
        CurrentRequest.categories.value =
            newList.mapIndexed { ind, category -> ind to category }.toMap().toMutableMap()
    }

    fun getSelectedCategories(): LiveData<MutableMap<Int, Category>> {
        return CurrentRequest.categories
    }

    fun uploadNewBooze(listener: FirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            listener.onComplete(FirebaseListener.NOT_LOGGED_IN)
            return
        }
        if (CurrentRequest.categories.value?.values?.toTypedArray()?.distinct()?.size
            != CurrentRequest.categories.value?.size
        ) {
            listener.onComplete(FirebaseListener.REPEATING_CATEGORIES)
            return
        }
        if (CurrentRequest.image != null) {
            val storageRef =
                FirebaseStorage.getInstance().reference.child("itemPhotos/${UUID.randomUUID()}.jpg")
            storageRef.putFile(Uri.fromFile(CurrentRequest.image))
                .addOnFailureListener {
                    listener.onComplete(FirebaseListener.OTHER)
                }
                .addOnSuccessListener {
                    uploadNBData(listener, user, "gs://jabot-5ce1f.appspot.com" + storageRef.path)
                }
        } else {
            uploadNBData(listener, user, null)
        }

    }

    private fun uploadNBData(
        listener: FirebaseListener,
        user: FirebaseUser,
        photoURL: String?
    ) {
        val firestoreRef = FirebaseFirestore.getInstance().collection("requests")
            .document("requests").collection("newBooze")

        val categories = CurrentRequest.categories.value!!.values.map { it.id }.toMutableList()
        if (CurrentRequest.majorCategory != null) {
            categories.add(CurrentRequest.majorCategory!!.id)
        } else {
            listener.onComplete(FirebaseListener.OTHER)
            return
        }

        firestoreRef
            .add(
                hashMapOf(
                    "author" to user.uid,
                    "categories" to categories,
                    "description" to null,
                    "name" to CurrentRequest.alcoholName,
                    "voltage" to CurrentRequest.voltage!!.toDouble(),
                    "volume" to CurrentRequest.volume,
                    "photo" to photoURL,
                    "shop" to mapOf(
                        (CurrentRequest.shop.value?.id?.toString()
                            ?: "null") to CurrentRequest.shop.value?.name
                    ),
                    "shopIsNew" to CurrentRequest.shopIsNew,
                    "price" to CurrentRequest.price!!.toDouble(),
                    "state" to Request.RequestState.PENDING,
                    "created" to Timestamp.now()
                )
            )
            .addOnFailureListener {
                listener.onComplete(FirebaseListener.OTHER)
            }
            .addOnSuccessListener {
                listener.onComplete(FirebaseListener.SUCCESS)
            }
    }

    fun uploadAvailability(
        availabilityRequest: AvailabilityRequest,
        firebaseListener: FirebaseListener
    ) {
        val firestoreRef =
            FirebaseFirestore.getInstance().collection("requests").document("requests")
                .collection("availability")
        firestoreRef.add(availabilityRequest)
            .addOnFailureListener {
                firebaseListener.onComplete(FirebaseListener.OTHER)
            }
            .addOnSuccessListener {
                firebaseListener.onComplete(FirebaseListener.SUCCESS)
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun fetch(uid: String, firebaseListener: FirebaseListener) {
        val tempList = mutableListOf<Pair<Int, AlcoObjectRequest>>()
        val firestoreRef = FirebaseFirestore.getInstance().collection("requests")
            .document("requests")

        firestoreRef.collection("newBooze").whereEqualTo("author", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.forEach {
                    tempList.add(
                        Pair(
                            Request.RequestType.NEW_BOOZE,
                            AlcoObjectRequest(
                                uid,
                                it.getString("name"),
                                it.getLong("volume"),
                                it.getDouble("voltage")?.toBigDecimal(),
                                it.get("categories") as? List<Int>,
                                (it.get("shop") as? Map<String?, String>)?.toList()?.get(0),
                                it.getBoolean("shopIsNew"),
                                it.getDouble("price"),
                                it.getString("photo"),
                                null,
                                it.id,
                                it.getTimestamp("created"),
                                it.getLong("state")?.toInt(),
                                it.getString("reason"),
                                it.getTimestamp("reviewed")
                            )
                        )
                    )
                    requestList.value = tempList
                    firebaseListener.onComplete(FirebaseListener.SUCCESS)
                }
            }
            .addOnFailureListener {
                firebaseListener.onComplete(FirebaseListener.OTHER)
            }
    }

    fun getRequestList(): MutableLiveData<List<Pair<Int, AlcoObjectRequest>>> {
        return requestList
    }
}

interface Request : Serializable {
    object RequestState {
        const val PENDING = 1
        const val APPROVED = 2
        const val DECLINED = 3
    }

    object RequestType {
        const val NEW_BOOZE = 1
        const val AVAILABILITY = 2
    }
}

data class AlcoObjectRequest(
    val author: String?,
    val name: String?,
    val volume: Long?,
    val voltage: BigDecimal?,
    val categories: List<Int>?,
    var shop: Pair<String?, String>?,
    val shopIsNew: Boolean?,
    val price: Double?,
    val photo: String?,
    var id: Long?,
    var requestId: String?,
    var created: Timestamp?,
    var state: Int?,
    var reason: String?,
    var reviewed: Timestamp?
) : Request

data class AvailabilityRequest(
    val author: String,
    val alcoObjectId: Int,
    val shop: Pair<Int?, String>,
    val shopIsNew: Boolean,
    val isEdited: Boolean,
    val price: Double,
    val created: Timestamp?,
    val state: Int?,
    val requestId: String?,
    val reason: String?,
    val reviewed: Timestamp?
) : Request