package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class FaqViewModel : ViewModel() {

    private val faqList = MutableLiveData<List<Map<String, String>>>()

    fun get(): MutableLiveData<List<Map<String, String>>> {
        return faqList
    }

    fun fetch(firestoreRef: FirebaseFirestore) {
        firestoreRef.collection("faq").orderBy("question").get()
            .addOnSuccessListener {
                val tempList: ArrayList<Map<String, String>> = arrayListOf()
                it.forEach { document ->
                    tempList.add(
                        mapOf(
                            "question" to document["question"] as String,
                            "answer" to document["answer"] as String
                        )
                    )
                }
                faqList.value = tempList
            }
            .addOnFailureListener {
            }

    }

}