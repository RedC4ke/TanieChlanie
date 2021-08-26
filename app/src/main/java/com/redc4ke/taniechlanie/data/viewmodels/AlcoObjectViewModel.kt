package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.FirebaseListener
import java.math.BigDecimal

class AlcoObjectViewModel: ViewModel() {
    private val tempList = mutableListOf<AlcoObject>()
    private val alcoList = MutableLiveData(listOf<AlcoObject>())

    fun addObject(o: AlcoObject) {
        tempList.add(o)
        alcoList.value = tempList
    }

    fun getAll(): MutableLiveData<List<AlcoObject>> {
        alcoList.value = alcoList.value?.sortedWith(compareBy { it.name })
        return alcoList
    }

    fun get(id: Long): AlcoObject? {
        return tempList.find { it.id == id }
    }

    fun fetch(firestoreRef: FirebaseFirestore, firebaseListener: FirebaseListener) {
        firestoreRef.collection("wines").orderBy("name").get()
            .addOnSuccessListener {
                tempList.clear()
                alcoList.value = listOf()

                it.forEach { document ->
                    val data = document.data
                    val output = mapOf<String, Any?>(
                        "id" to document.getLong("id"),
                        "name" to document.getString("name"),
                        "volume" to document.getLong("volume")?.toInt(),
                        "voltage" to document.getLong("voltage")?.toBigDecimal(),
                        "categories" to data["categories"] as? ArrayList<Int>,
                        "photo" to document.getString("photo")
                    )
                    getPrices(output as MutableMap<String, Any?>, firestoreRef, firebaseListener)
                }

                firebaseListener.onComplete(FirebaseListener.SUCCESS)
            }
            .addOnFailureListener {
                firebaseListener.onComplete(FirebaseListener.OTHER)
            }
    }

    private fun getPrices(
        input: MutableMap<String, Any?>,
        firestoreRef: FirebaseFirestore,
        firebaseListener: FirebaseListener)
    {
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
                firebaseListener.onComplete(FirebaseListener.OTHER)
            }
    }
}



