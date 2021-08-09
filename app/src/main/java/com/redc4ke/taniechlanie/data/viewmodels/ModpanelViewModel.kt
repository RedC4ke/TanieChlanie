package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.math.BigDecimal

class ModpanelViewModel : ViewModel() {

    private val newBooze = MutableLiveData<MutableList<Request>>()
    private val changedBooze = MutableLiveData<MutableList<Request>>()


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
                val tempList = mutableListOf<Request>()
                it.documents.forEach { document ->
                    val request =
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
                    tempList.add(request)
                    newBooze.value = tempList
                    Log.d("huj", request.toString())
                }
            }
            .addOnFailureListener {

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
                        //Increment author upload count
                        val user = transaction.get(usersRef.document(request.author!!))
                        val stats = (user.get("stats") as? HashMap<String, Long>)!!.toMutableMap()
                        val submits = stats["submits"] ?: 0
                        stats["submits"] = submits + 1
                        transaction.set(
                            usersRef.document(request.author), hashMapOf(
                                "stats" to stats
                            )
                        )

                        //Add shop if new
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

                        //Set the main document
                        transaction.set(
                            winesRef.document(), hashMapOf(
                                "id" to request.id,
                                "name" to request.name,
                                "author" to request.author,
                                "approvedBy" to FirebaseAuth.getInstance().uid,
                                "volume" to request.volume,
                                "voltage" to request.voltage,
                                "categories" to request.categories,
                                "photo" to request.photo
                            )
                        )

                        //Set price
                        transaction.set(
                            pricesRef.document(request.id.toString()), hashMapOf(
                                "shop" to hashMapOf(
                                    request.shop!!.first to hashMapOf(
                                        "is_promo" to false,
                                        "price" to request.price
                                    )
                                )
                            )
                        )
                    }.addOnFailureListener {
                        listener.onComplete(RequestUploadListener.OTHER)
                    }.addOnSuccessListener {
                        listener.onComplete(RequestUploadListener.SUCCESS)
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
) : Serializable
