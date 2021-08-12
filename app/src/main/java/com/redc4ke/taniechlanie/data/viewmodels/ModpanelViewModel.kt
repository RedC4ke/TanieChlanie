package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.data.FirebaseListener
import com.redc4ke.taniechlanie.data.Shop
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
            .collection("requests").document("requests")

        firestoreRef.collection("newBooze")
            .whereEqualTo("status", RequestStatus.PENDING).get()
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
                            document.get("shop") as? Map<String, String>,
                            document.getBoolean("shopIsNew"),
                            document.getDouble("price"),
                            document.getString("photo"),
                            null,
                            document.id
                        )
                    tempList.add(request)
                    newBooze.value = tempList
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
        shopViewModel: ShopViewModel,
        listener: FirebaseListener
    ) {
        val firestoreRef = FirebaseFirestore.getInstance()
        val requestsRef = firestoreRef.collection("requests")
            .document("requests").collection("newBooze")
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
                        val shopName = request.shop!!.values.toList()[0]

                        //Increment author upload count
                        val user = transaction.get(usersRef.document(request.author!!))
                        val stats = (user.get("stats") as? HashMap<String, Long>)!!.toMutableMap()
                        val submits = stats["submits"] ?: 0
                        stats["submits"] = submits + 1
                        transaction.update(
                            usersRef.document(request.author), mapOf(
                                "stats" to stats
                            )
                        )

                        //Add shop if new
                        if (request.shopIsNew == true) {
                            val shopId = shopViewModel.getLastId()
                            request.shop = mapOf(shopId.toString() to shopName)
                            transaction.set(
                                shopsRef.document(shopName), hashMapOf(
                                    "id" to shopId,
                                    "name" to shopName
                                )
                            )
                            shopViewModel.add(Shop(shopId, shopName))
                        }

                        //Set the main document
                        transaction.set(
                            winesRef.document(), hashMapOf(
                                "id" to request.id,
                                "name" to request.name,
                                "author" to request.author,
                                "approvedBy" to FirebaseAuth.getInstance().uid,
                                "volume" to request.volume,
                                "voltage" to request.voltage!!.toDouble(),
                                "categories" to request.categories,
                                "photo" to request.photo
                            )
                        )

                        //Set price
                        transaction.set(
                            pricesRef.document(request.id.toString()), hashMapOf(
                                "shop" to hashMapOf(
                                    request.shop!!.keys.toList()[0] to hashMapOf(
                                        "is_promo" to false,
                                        "price" to request.price!!.toDouble()
                                    )
                                )
                            )
                        )

                        //Change request status to approved
                        requestsRef.document(request.requestId ?: "").update(
                            "status", RequestStatus.APPROVED
                        )

                    }.addOnFailureListener {
                        listener.onComplete(FirebaseListener.OTHER)
                    }.addOnSuccessListener {
                        fetch()
                        listener.onComplete(FirebaseListener.SUCCESS)
                    }
                }
            }
    }

    fun declineNewBooze(id: String, reason: String) {
        val firestoreRef = FirebaseFirestore.getInstance().collection("requests")
            .document("requests").collection("newBooze")

        firestoreRef.document(id)
            .update("status", RequestStatus.DECLINED, "reason", reason)

        fetch()
    }

}

data class Request(
    val author: String?,
    val name: String?,
    val volume: Long?,
    val voltage: BigDecimal?,
    val categories: List<Int>?,
    var shop: Map<String, String>?,
    val shopIsNew: Boolean?,
    val price: Double?,
    val photo: String?,
    var id: Long?,
    var requestId: String?
) : Serializable
