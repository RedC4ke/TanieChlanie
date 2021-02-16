package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ReviewViewModel: ViewModel() {
    private val reviews = MutableLiveData<Map<Int, List<Review>>>()
    private val tempList = mapOf<Int, MutableList<Review>>()
    private val count = mutableMapOf<Int, Int>()


    fun add(id: Int, r: List<Review>) {
        tempList[id] to r
        reviews.value = tempList
    }

    fun getAll(): MutableLiveData<Map<Int, List<Review>>> {
        return reviews
    }

    fun get(id: Int): Pair<List<Review>, Int> {
        return Pair(reviews.value!![id]!!, count[id]!!)
    }

    fun parse(review: Review, iv: ImageView, username: TextView, timestamp: TextView) {

    }

    fun download(id: Int): com.google.android.gms.tasks.Task<QuerySnapshot> {
        val ref = FirebaseFirestore.getInstance().collection("reviews")
            .document("").collection(id.toString())

        return ref.get()
            .addOnSuccessListener {
                val list = mutableListOf<Review>()
                it.forEach {doc ->
                    val data = doc.data
                    val review = Review(
                        data["id"] as Int,
                        data["author"] as Int,
                        data["rating"] as Int,
                        data["timestamp"] as Timestamp,
                        data["content"] as String,
                        data["usefulness"] as Int
                    )
                    list.add(review)
                }
                add(id, list)
                count[id] = list.size
                Log.d("ReviewViewModel", "Added ${list.size} reviews for $id")
            }
            .addOnFailureListener {
                Log.d("ReviewViewModel", "Review download failed: $it")
            }
    }
}

data class Review(
    val reviewID: Int,
    val author: Int,
    val rating: Int,
    val timestamp: Timestamp,
    val content: String,
    val usefulness: Int
)