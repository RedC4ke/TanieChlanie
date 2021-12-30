package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.RequestListener
import java.math.BigDecimal

class AlcoObjectViewModel : ViewModel() {
    private val tempList = mutableListOf<AlcoObject>()
    private val alcoList = MutableLiveData(listOf<AlcoObject>())
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private var itemCount = 0

    private fun addObject(o: AlcoObject) {
        tempList.add(o)
        if (tempList.size == itemCount) finalize()
    }

    private fun finalize() {
        alcoList.value = tempList
    }

    fun getAll(): MutableLiveData<List<AlcoObject>> {
        alcoList.value = alcoList.value?.sortedWith(compareBy { it.name })
        return alcoList
    }

    fun get(id: Long): AlcoObject? {
        return tempList.find { it.id == id }
    }

    fun fetch(firestoreRef: FirebaseFirestore, requestListener: RequestListener) {
        firestoreRef.collection("wines")
            .orderBy("name")
            .get()
            .addOnSuccessListener {
                tempList.clear()
                itemCount = 0
                alcoList.value = listOf()

                it.forEach { document ->
                    val data = document.data
                    val output = mapOf<String, Any?>(
                        "id" to document.getLong("id"),
                        "name" to document.getString("name"),
                        "volume" to document.getLong("volume")?.toInt(),
                        "voltage" to document.getLong("voltage")?.toBigDecimal(),
                        "categories" to data["categories"] as? List<Int>,
                        "photo" to document.getString("photo")
                    )

                    itemCount += 1

                    getPrices(
                        output as MutableMap<String, Any?>,
                        firestoreRef,
                        requestListener
                    )
                }
                requestListener.onComplete(RequestListener.SUCCESS)
            }
            .addOnFailureListener {
                requestListener.onComplete(RequestListener.OTHER)
            }
    }

    fun deleteBooze(id: Long, listener: RequestListener) {
        firestoreInstance.collection("wines").whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                val alcoObjectReference = it.documents[0].reference
                firestoreInstance
                    .runTransaction {
                        it.delete(firestoreInstance.collection("prices").document(id.toString()))
                        it.delete(alcoObjectReference)
                    }
                    .addOnSuccessListener {
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

    fun deletePhoto(id: Long, listener: RequestListener) {
        firestoreInstance.collection("wines").whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                it.documents[0].reference
                    .update(
                        "photo", null
                    )
                    .addOnSuccessListener {
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

    fun changeData(
        boozeId: Long,
        name: String,
        volume: Int,
        voltage: BigDecimal,
        listener: RequestListener
    ) {
        firestoreInstance.collection("wines")
            .whereEqualTo("id", boozeId).get()
            .addOnSuccessListener {
                it.documents[0].reference
                    .update(
                        "name", name,
                        "volume", volume,
                        "voltage", voltage.toDouble()
                    )
                    .addOnSuccessListener {
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

    fun addCategory(boozeId: Long, categoryId: Int, listener: RequestListener) {
        firestoreInstance.collection("wines").whereEqualTo("id", boozeId).get()
            .addOnSuccessListener {
                it.documents[0].reference.update("categories", FieldValue.arrayUnion(categoryId))
                    .addOnSuccessListener {
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

    fun removeCategory(boozeId: Long, categoryId: Int, listener: RequestListener) {
        firestoreInstance.collection("wines").whereEqualTo("id", boozeId).get()
            .addOnSuccessListener {
                it.documents[0].reference
                    .update("categories", FieldValue.arrayRemove(categoryId))
                    .addOnSuccessListener {
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

    fun updateMajorCategory(
        boozeId: Long,
        oldCategory: Int,
        newCategory: Int,
        listener: RequestListener
    ) {
        firestoreInstance.collection("wines").whereEqualTo("id", boozeId).get()
            .addOnSuccessListener {
                it.documents[0].reference.update(
                    "categories",
                    FieldValue.arrayRemove(oldCategory),
                    "categories",
                    FieldValue.arrayUnion(newCategory)
                )
                    .addOnSuccessListener {
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

    private fun getPrices(
        input: MutableMap<String, Any?>,
        firestoreRef: FirebaseFirestore,
        requestListener: RequestListener
    ) {
        val result: MutableMap<String, Any?> = input

        firestoreRef.collection("prices").document(input["id"].toString()).get()
            .addOnSuccessListener { it ->
                val data = it.data
                val shopList = (data?.get("shop") as Map<String, Map<String, Any>>)
                val shopIds = mutableListOf<Int>()
                shopList.forEach {
                    shopIds.add(it.key.toInt())
                }
                result["shop"] = shopIds

                val shopToPrice = mutableMapOf<Int, BigDecimal?>()
                shopList.forEach {
                    val price = it.value["price"]?.toString()?.toBigDecimal()
                    shopToPrice[it.key.toInt()] = price
                }

                val alcoObject = AlcoObject(
                    result["id"] as Long,
                    result["name"] as String,
                    shopToPrice,
                    result["volume"] as Int,
                    result["voltage"] as BigDecimal,
                    result["categories"] as ArrayList<Int>,
                    result["photo"] as String?,
                    null
                )

                if (!tempList.contains(alcoObject))
                    addObject(alcoObject)
            }
            .addOnFailureListener {
                requestListener.onComplete(RequestListener.OTHER)
            }
    }
}



