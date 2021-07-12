package com.redc4ke.taniechlanie.data.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal

class ModpanelViewModel : ViewModel() {

    private companion object {
        val newBooze = MutableLiveData<MutableList<Request>>().also {
            it.value = mutableListOf()
        }
        val changedBooze = MutableLiveData<MutableList<Request>>().also {
            it.value = mutableListOf()
        }
    }

    fun getNewBooze(): LiveData<MutableList<Request>> {
        return newBooze
    }

    fun getChangedBooze(): LiveData<MutableList<Request>> {
        return changedBooze
    }

    @Suppress("UNCHECKED_CAST")
    fun fetch() {
        val firestoreRef = FirebaseFirestore.getInstance()
            .collection("pending").document("pending")

        firestoreRef.collection("newBooze").get()
            .addOnSuccessListener {
                newBooze.value = mutableListOf()
                it.documents.forEach { document ->
                    newBooze.value!!.add(
                        Request(
                            document.getString("author"),
                            document.getString("name"),
                            document.getLong("volume"),
                            document.getDouble("voltage")?.toBigDecimal(),
                            document.get("categories") as? List<Int>,
                            document.get("shop") as? Pair<String, String>,
                            document.getBoolean("shopIsNew"),
                            document.getDouble("price"),
                            document.getString("photo"),
                            null
                        )
                    )
                }
            }
        firestoreRef.collection("changedBooze").get()
            .addOnSuccessListener {
                changedBooze.value = mutableListOf()
                it.documents.forEach { document ->
                    TODO()
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun acceptNewBooze(
        request: Request,
        listener: RequestUploadListener,
        shopViewModel: ShopViewModel
    ) {
        val firestoreRef = FirebaseFirestore.getInstance()
        val winesRef = firestoreRef.collection("wines")
        val pricesRef = firestoreRef.collection("prices")
        val shopsRef = firestoreRef.collection("shops")
        val usersRef = firestoreRef.collection("users")
        val firebaseRef = FirebaseDatabase.getInstance().reference.child("idTrace")
        var id: Long
        firebaseRef.get()
            .addOnSuccessListener {
                id = it.value.toString().toLong()
                firebaseRef.setValue(id + 1).addOnSuccessListener {
                    request.id = id
                    firestoreRef.runTransaction { transaction ->
                        if (request.shopIsNew == true) {
                            val shopId = shopViewModel.getLastId()
                            request.shop = Pair(shopId.toString(), request.shop!!.second)
                            transaction.set(
                                shopsRef.document(request.shop!!.second), hashMapOf(
                                    "id" to shopId,
                                    "name" to request.shop!!.second
                                )
                            )
                        }
                        transaction.set(winesRef.document(), hashMapOf(
                            "id" to request.id,
                            "name" to request.name,
                            "author" to request.author,
                            "approvedBy" to FirebaseAuth.getInstance().uid,
                            "volume" to request.volume,
                            "voltage" to request.voltage,
                            "categories" to request.categories,
                            "photo" to request.photo
                        ))
                        transaction.set(pricesRef.document(request.id.toString()), hashMapOf(
                            "shop" to hashMapOf(request.shop!!.first to hashMapOf(
                                "is_promo" to false,
                                "price" to request.price
                            ))
                        ))
                        var userStat = transaction.get(usersRef.document(request.author!!))
                            .get("stats") as? HashMap<String, HashMap<String, Long>>

                    }


                }
            }
    }


}

data class Request(
    val author: String?,
    val name: String?,
    val volume: Long?,
    val voltage: BigDecimal?,
    val categories: List<Int>?,
    var shop: Pair<String, String>?,
    val shopIsNew: Boolean?,
    val price: Double?,
    val photo: String?,
    var id: Long?
)
