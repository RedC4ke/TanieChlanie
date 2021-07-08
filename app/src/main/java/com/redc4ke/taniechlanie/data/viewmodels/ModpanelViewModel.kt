package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
                            document.getString("author") ?: return@forEach,
                            document.getString("name") ?: return@forEach,
                            document.getLong("volume") ?: return@forEach,
                            document.getDouble("voltage")?.toBigDecimal() ?: return@forEach,
                            document.get("categories") as? List<Int> ?: return@forEach,
                            document.get("price") as? HashMap<String, Double> ?: return@forEach,
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

    fun acceptNewBooze(request: Request, listener: RequestUploadListener) {
        val firestoreRef = FirebaseFirestore.getInstance().collection("wines")
        val firebaseRef = FirebaseDatabase.getInstance().reference.child("idTrace")
        var id: Long
        firebaseRef.get()
            .addOnSuccessListener {
                id = it.value.toString().toLong()
                firebaseRef.setValue(id + 1).addOnSuccessListener {
                    request.id = id
                    firestoreRef.add(request)
                        .addOnSuccessListener {
                            listener.onComplete(RequestUploadListener.SUCCESS)
                        }
                }
            }
            .addOnFailureListener {
                listener.onComplete(RequestUploadListener.OTHER)
            }
    }

}

data class Request(
    val author: String,
    val name: String,
    val volume: Long,
    val voltage: BigDecimal,
    val categories: List<Int>,
    val price: HashMap<String, Double>,
    val photo: String?,
    var id: Long?
)
