package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.Shop

class ModpanelViewModel : ViewModel() {

    private val newBooze = MutableLiveData<MutableList<NewBoozeRequest>>()
    private val availability = MutableLiveData<MutableList<AvailabilityRequest>>()
    private val changedBooze = MutableLiveData<MutableList<NewBoozeRequest>>()


    fun getNewBooze(): LiveData<MutableList<NewBoozeRequest>> {
        return newBooze
    }

    fun getAvailability(): LiveData<MutableList<AvailabilityRequest>> {
        return availability
    }

    fun getChangedBooze(): LiveData<MutableList<NewBoozeRequest>> {
        return changedBooze
    }

    @Suppress("UNCHECKED_CAST")
    fun fetch() {
        val firestoreRef = FirebaseFirestore.getInstance()
            .collection("requests").document("requests")

        firestoreRef.collection("newBooze")
            .whereEqualTo("state", Request.RequestState.PENDING).orderBy("created").get()
            .addOnSuccessListener {
                newBooze.value = mutableListOf()
                val tempList = mutableListOf<NewBoozeRequest>()
                it.documents.forEach { document ->
                    val request =
                        NewBoozeRequest(
                            document.getString("author"),
                            document.getString("name"),
                            document.getLong("volume"),
                            document.getDouble("voltage")?.toBigDecimal(),
                            document.get("categories") as? List<Int>,
                            document.getLong("shopId")?.toInt(),
                            document.getString("shopName"),
                            document.getBoolean("shopIsNew"),
                            document.getDouble("price"),
                            document.getString("photo"),
                            null,
                            document.id,
                            document.getTimestamp("created"),
                            document.getLong("state")?.toInt(),
                            null,
                            null
                        )
                    tempList.add(request)
                    newBooze.value = tempList
                }
            }
            .addOnFailureListener {
            }

        firestoreRef.collection("availability")
            .whereEqualTo("state", Request.RequestState.PENDING).orderBy("created").get()
            .addOnSuccessListener {
                availability.value = mutableListOf()
                val tempList = mutableListOf<AvailabilityRequest>()
                it.documents.forEach { document ->
                    val request = AvailabilityRequest(
                        document.getString("author") ?: "",
                        document.getLong("alcoObjectId")!!,
                        document.getLong("shopId")?.toInt(),
                        document.getString("shopName")!!,
                        document.getBoolean("shopIsNew")!!,
                        document.getBoolean("edited")!!,
                        document.getDouble("price")!!,
                        document.getTimestamp("created"),
                        document.getLong("state")!!.toInt(),
                        document.id,
                        null,
                        null
                    )
                    tempList.add(request)
                }
                availability.value = tempList
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
        request: NewBoozeRequest,
        shopViewModel: ShopViewModel,
        listener: RequestListener
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
                            request.shopId = shopViewModel.getLastId() + 1
                            transaction.set(
                                shopsRef.document(request.shopName!!), hashMapOf(
                                    "id" to request.shopId,
                                    "name" to request.shopName
                                )
                            )
                            shopViewModel.add(Shop(request.shopId, request.shopName))
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
                                    request.shopId to hashMapOf(
                                        "price" to request.price!!.toDouble()
                                    )
                                )
                            )
                        )

                        //Change request status to approved
                        requestsRef.document(request.requestId ?: "").update(
                            "state", Request.RequestState.APPROVED,
                            "reviewed", Timestamp.now()
                        )

                    }.addOnFailureListener {
                        listener.onComplete(RequestListener.OTHER)
                    }.addOnSuccessListener {
                        newBooze.value?.remove(request)
                        listener.onComplete(RequestListener.SUCCESS)
                    }
                }
            }
    }

    fun acceptAvailability(
        request: AvailabilityRequest,
        shopViewModel: ShopViewModel,
        requestListener: RequestListener
    ) {
        val firestoreRef = FirebaseFirestore.getInstance()

        firestoreRef
            .runTransaction {
                if (request.shopIsNew) {
                    val shopId = shopViewModel.getLastId() + 1
                    it.set(
                        firestoreRef.collection("shops").document(request.shopName), hashMapOf(
                            "id" to shopId,
                            "name" to request.shopName
                        )
                    )
                    request.shopId = shopId
                    shopViewModel.fetch(firestoreRef, null)
                }

                it.update(
                    firestoreRef.collection("prices").document(request.alcoObjectId.toString()),
                    hashMapOf(
                        "shop.${request.shopId}" to hashMapOf("price" to request.price)
                    ) as Map<String, Any>
                )

                it.update(
                    firestoreRef.collection("requests").document("requests")
                        .collection("availability")
                        .document(request.requestId!!), hashMapOf(
                        "state" to Request.RequestState.APPROVED,
                        "reviewed" to Timestamp.now()
                    ) as Map<String, Any>
                )

                it.update(
                    firestoreRef.collection("users").document(request.author),
                    "stats.commitment",
                    FieldValue.increment(1)
                )
            }
            .addOnFailureListener {
                requestListener.onComplete(RequestListener.OTHER)
            }
            .addOnSuccessListener {
                availability.value?.remove(request)
                requestListener.onComplete(RequestListener.SUCCESS)
            }
    }

    fun declineRequest(request: Request, reason: String, requestListener: RequestListener) {
        val firestoreRef = FirebaseFirestore.getInstance().collection("requests")
            .document("requests")
        val collection = when (request) {
            is NewBoozeRequest -> firestoreRef.collection("newBooze")
            is AvailabilityRequest -> firestoreRef.collection("availability")
            else -> return
        }

        collection.document(request.requestId!!)
            .update(
                "state",
                Request.RequestState.DECLINED,
                "reason",
                reason,
                "reviewed",
                Timestamp.now()
            )
            .addOnSuccessListener {
                requestListener.onComplete(RequestListener.SUCCESS)
            }
            .addOnFailureListener {
                requestListener.onComplete(RequestListener.OTHER)
            }

        fetch()
    }

}


