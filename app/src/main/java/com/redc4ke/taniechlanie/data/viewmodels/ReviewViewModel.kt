package com.redc4ke.taniechlanie.data.viewmodels

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.RequestListener
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.text.DateFormat
import java.util.*
import kotlin.collections.AbstractList
import kotlin.collections.HashMap

class ReviewViewModel: ViewModel() {
    private val reviews = MutableLiveData<Map<Long, List<Review>>>()
    private val userReview = MutableLiveData<MutableMap<String, List<Review>>>(mutableMapOf())
    private val tempMap = mutableMapOf<Long, MutableList<Review>>()
    private val ref = FirebaseFirestore.getInstance()


    fun add(id: Long, r: MutableList<Review>) {
        tempMap[id] = r
        reviews.value = tempMap
    }

    fun getAll(): MutableLiveData<Map<Long, List<Review>>> {
        return reviews
    }

    fun getForUser(uid: String, alcoObjectViewModel: AlcoObjectViewModel):
            List<Pair<AlcoObject, Review>> {

        val list = userReview.value?.get(uid) ?: listOf()
        val result = mutableListOf<Pair<AlcoObject, Review>>()
        list.sortedBy { it.timestamp }.forEach {
            val alcoObject = alcoObjectViewModel.get(it.objectID)
            if (alcoObject != null) {
                result.add(Pair(alcoObject, it))
            }
        }
        return result
    }

    fun parse(context: Context, review: Review, iv: ImageView,
              username: TextView, timestamp: TextView, ratingBar: MaterialRatingBar) {
        ref.collection("users").document(review.author).get()
            .addOnSuccessListener {
                val data = it.data!!
                val name = data["name"] as String
                val avatar = data["avatar"] as String

                Glide.with(context).load(avatar).into(iv)
                username.text = name
                timestamp.text = DateFormat.getDateInstance().format(review.timestamp.toDate())
                ratingBar.rating = review.rating.toFloat()
            }
    }

    fun getReview(id: String) : LiveData<Review?> {
        val review = MutableLiveData<Review?>()
        ref.collection("reviews").whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                review.value = retrieve(it.documents[0])
            }

        return review
    }

    fun download(id: Long): Task<QuerySnapshot> {
        val col = ref.collection("reviews")
            .whereEqualTo("object_id", id)

        return col.get()
            .addOnSuccessListener {
                val list = mutableListOf<Review>()
                it.forEach {doc ->
                    list.add(retrieve(doc))
                }
                add(id, list)
            }
            .addOnFailureListener {
            }
    }

    fun downloadUser(uid: String): Task<QuerySnapshot> {
        val col = ref.collection("reviews")
        return col.whereEqualTo("author", uid).get()
            .addOnSuccessListener {
                val list = mutableListOf<Review>()
                it.forEach {document ->
                    list.add(retrieve(document))
                }
                userReview.value!![uid] = list
            }
    }

    fun addReview(context: Context, id: Long, user: FirebaseUser,
                  rating: Double, content: String, update: Boolean = false): Task<Void> {
        val data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "object_id" to id,
            "author" to user.uid,
            "rating" to rating,
            "timestamp" to Timestamp.now(),
            "content" to content,
            "usefulness" to 0
        )
        return ref.collection("reviews")
            .document(id.toString() + ":" + user.uid).set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(context, "Recenzja dodana!", Toast.LENGTH_SHORT).show()
                download(id)
                if (!update) {
                    reviewsUpdate(user, 1)
                }
            }
            .addOnFailureListener {
                Log.w("huj", it.toString())
                Toast.makeText(context, "Nieznany błąd", Toast.LENGTH_LONG).show()
            }
    }

    fun remove(review: Review, user: FirebaseUser): Task<QuerySnapshot> {
        return ref.collection("reviews")
            .whereEqualTo("reviewID", review.reviewID).get()
            .addOnSuccessListener {
                it.forEach { document ->
                    document.reference.delete()
                        .addOnSuccessListener {
                            download(review.objectID)
                            reviewsUpdate(user, -1)
                        }
                }
            }
    }

    fun removeById(id: String, listener: RequestListener) {
        ref.collection("reviews").whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                it.documents.last().reference
                    .delete()
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

    private fun reviewsUpdate(user: FirebaseUser, value: Long) {
        ref.collection("users").document(user.uid).update(
            "stats.reviews", FieldValue.increment(value))
    }

    private fun retrieve(doc: DocumentSnapshot): Review {
        val data = doc.data
        return Review(
            doc.getString("id")!!,
            data?.get("object_id") as Long,
            data["author"] as String,
            data["rating"] as Double,
            data["timestamp"] as Timestamp,
            data["content"] as String,
            data["usefulness"] as Long
        )
    }

}

data class Review(
    val reviewID: String,
    val objectID: Long,
    val author: String,
    val rating: Double,
    val timestamp: Timestamp,
    val content: String,
    val usefulness: Long
)