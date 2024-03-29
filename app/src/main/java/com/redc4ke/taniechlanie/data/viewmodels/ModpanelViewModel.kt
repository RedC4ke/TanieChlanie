package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Repo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.ui.profile.modpanel.RequestDetailsFragmentArgs

class ModpanelViewModel : ViewModel() {

    private val newBooze = MutableLiveData<MutableList<NewBoozeRequest>>()
    private val availability = MutableLiveData<MutableList<AvailabilityRequest>>()
    private val reports = MutableLiveData<MutableList<Report>>()

    private val firestoreInstance = FirebaseFirestore.getInstance()

    fun getNewBooze(): LiveData<MutableList<NewBoozeRequest>> {
        return newBooze
    }

    fun getAvailability(): LiveData<MutableList<AvailabilityRequest>> {
        return availability
    }

    fun getReports(): LiveData<MutableList<Report>> {
        return reports
    }

    @Suppress("UNCHECKED_CAST")
    fun fetch() {
        val firestoreRef = firestoreInstance
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
                            document.getTimestamp("created")?.toDate(),
                            document.getLong("state")?.toInt(),
                            null,
                            null
                        )
                    tempList.add(request)
                }
                newBooze.value = tempList
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
                        document.getTimestamp("created")?.toDate(),
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

        firestoreRef.collection("reports")
            .whereIn("state", listOf(Request.RequestState.PENDING, Request.RequestState.FORWARDED))
            .orderBy("created")
            .get()
            .addOnSuccessListener { snapshot ->
                reports.value = mutableListOf()
                val tempList = mutableListOf<Report>()

                snapshot.forEach {
                    val request = Report(
                        it.id,
                        it.getLong("reportType")?.toInt() ?: return@forEach,
                        it.getString("itemId") ?: return@forEach,
                        it.getLong("reason")?.toInt() ?: return@forEach,
                        it.getString("details"),
                        it.getString("author") ?: return@forEach,
                        it.getTimestamp("created")?.toDate() ?: return@forEach,
                        it.getLong("state")?.toInt() ?: return@forEach,
                        null,
                        null
                    )
                    tempList.add(request)
                }
                reports.value = tempList
            }
            .addOnFailureListener {
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun acceptNewBooze(
        request: NewBoozeRequest,
        shopViewModel: ShopViewModel,
        listener: RequestListener
    ) {
        val requestsRef = firestoreInstance.collection("requests")
            .document("requests").collection("newBooze")
        val winesRef = firestoreInstance.collection("wines")
        val pricesRef = firestoreInstance.collection("prices")
        val shopsRef = firestoreInstance.collection("shops")
        val usersRef = firestoreInstance.collection("users")
        val firebaseRef = FirebaseDatabase.getInstance().reference.child("idTrace")
        var id: Long
        firebaseRef.get()
            .addOnSuccessListener {
                id = it.value.toString().toLong()
                firebaseRef.setValue(id + 1).addOnSuccessListener {
                    request.id = id
                    firestoreInstance.runTransaction { transaction ->
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
                                shopsRef.document(request.shopName ?: ""), hashMapOf(
                                    "id" to request.shopId,
                                    "name" to request.shopName
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
                                "voltage" to request.voltage!!.toDouble(),
                                "categories" to request.categories,
                                "photo" to request.photo
                            )
                        )

                        //Set price
                        transaction.set(
                            pricesRef.document(request.id.toString()), hashMapOf(
                                "shop" to hashMapOf(
                                    request.shopId.toString() to hashMapOf(
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
                        if (request.shopIsNew == true) {
                            shopViewModel.add(Shop(request.shopId, request.shopName ?: ""))
                        }
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
        firestoreInstance
            .runTransaction {
                if (request.shopIsNew) {
                    val shopId = shopViewModel.getLastId() + 1
                    it.set(
                        firestoreInstance.collection("shops").document(request.shopName), hashMapOf(
                            "id" to shopId,
                            "name" to request.shopName
                        )
                    )
                    request.shopId = shopId
                    shopViewModel.fetch(firestoreInstance, null)
                }

                it.update(
                    firestoreInstance.collection("prices")
                        .document(request.alcoObjectId.toString()),
                    hashMapOf(
                        "shop.${request.shopId}" to hashMapOf("price" to request.price)
                    ) as Map<String, Any>
                )

                it.update(
                    firestoreInstance.collection("requests").document("requests")
                        .collection("availability")
                        .document(request.requestId!!), hashMapOf(
                        "state" to Request.RequestState.APPROVED,
                        "reviewed" to Timestamp.now()
                    ) as Map<String, Any>
                )

                it.update(
                    firestoreInstance.collection("users").document(request.author),
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
        val firestoreRef = firestoreInstance.collection("requests")
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

    fun blockReporting(uid: String, listener: RequestListener) {
        firestoreInstance
            .collection("requests")
            .document("requests")
            .collection("reports")
            .whereEqualTo("author", uid)
            .get()
            .addOnSuccessListener {
                val reportsToDelete = it.documents

                firestoreInstance
                    .runTransaction { transaction ->
                        reportsToDelete.forEach { snapshot ->
                            transaction.delete(snapshot.reference)
                        }

                        transaction
                            .update(
                                firestoreInstance.collection("security").document(uid),
                                "hasReportBan", true
                            )
                    }
                    .addOnSuccessListener {
                        reports.value?.removeIf {report -> report.author == uid }
                        listener.onComplete(RequestListener.SUCCESS)
                    }
                    .addOnFailureListener {
                        listener.onComplete(RequestListener.OTHER)
                    }
            }
            .addOnFailureListener {
                listener.onComplete(RequestListener.OTHER)
            }
    }

    fun blockReviewing(uid: String) {
        firestoreInstance.collection("security").document(uid)
            .update("hasReviewBan", true)
            .addOnFailureListener {
            }
    }

    fun sendReportForward(report: Report, listener: RequestListener) {
        firestoreInstance.collection("requests").document("requests")
            .collection("reports").document(report.requestId!!)
            .update("state", Request.RequestState.FORWARDED)
            .addOnSuccessListener {
                listener.onComplete(RequestListener.SUCCESS)
            }
            .addOnFailureListener {
                listener.onComplete(RequestListener.OTHER)
            }
    }

    fun completeReport(report: Report, approved: Boolean, listener: RequestListener) {
        firestoreInstance.collection("requests").document("requests")
            .collection("reports").document(report.requestId!!)
            .update(
                "state", if (approved) {
                    Request.RequestState.APPROVED
                } else {
                    Request.RequestState.DECLINED
                }
            )
            .addOnSuccessListener {
                listener.onComplete(RequestListener.SUCCESS)
                reports.value?.remove(report)
            }
            .addOnFailureListener {
                listener.onComplete(RequestListener.OTHER)
            }
    }
}


