package com.redc4ke.taniechlanie.data.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.setImage
import kotlinx.coroutines.flow.merge
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.math.BigDecimal
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ReviewViewModel: ViewModel() {
    private val reviews = MutableLiveData<Map<Int, List<Review>>>()
    private val userReview = MutableLiveData<MutableMap<String, List<Review>>>(mutableMapOf())
    private val tempList = mutableMapOf<Int, MutableList<Review>>()
    private val ref = FirebaseFirestore.getInstance()


    fun add(id: Int, r: MutableList<Review>) {
        tempList[id] = r
        reviews.value = tempList
    }

    fun getAll(): MutableLiveData<Map<Int, List<Review>>> {
        return reviews
    }

    fun get(id: Int): List<Review> {
        return reviews.value!![id]!!.sortedByDescending { it.usefulness }
    }

    fun getUser(uid: String, alcoObjectViewModel: AlcoObjectViewModel):
            List<Pair<AlcoObject, Review>> {

        val list = userReview.value?.get(uid) ?: listOf()
        val result = mutableListOf<Pair<AlcoObject, Review>>()
        list.sortedBy { it.timestamp }.forEach {
            val alcoObject = alcoObjectViewModel.get(it.objectID.toInt())
            if (alcoObject != null) {
                result.add(Pair(alcoObject, it))
            }
        }
        return result
    }

    fun parse(context: Context, review: Review, iv: ImageView,
              username: TextView, timestamp: TextView, ratingBar: MaterialRatingBar) {
        var user: UserData
        ref.collection("users").document(review.author).get()
            .addOnSuccessListener {
                val data = it.data!!
                user = UserData(
                    review.author,
                    data["name"] as String,
                    data["created"] as Timestamp,
                    data["groups"] as ArrayList<String>,
                    data["stats"] as Map<String, Int>,
                    data["title"] as Long,
                    data["avatar"] as String
                )

                setImage(context, review.author, iv, Uri.parse(user.avatar))
                username.text = user.name
                timestamp.text = DateFormat.getDateInstance().format(review.timestamp.toDate())
                ratingBar.rating = review.rating.toFloat()
            }
    }

    fun download(id: Int): Task<QuerySnapshot> {
        val col = ref.collection("reviews")
            .whereEqualTo("object_id", id)

        return col.get()
            .addOnSuccessListener {
                val list = mutableListOf<Review>()
                it.forEach {doc ->
                    Log.d("huj", "guwno")
                    list.add(retrieve(doc))
                }
                add(id, list)
                Log.d("ReviewViewModel", "Added ${list.size} reviews for $id")
            }
            .addOnFailureListener {
                Log.d("ReviewViewModel", "Review download failed: $it")
            }
    }

    fun downloadUser(uid: String): Task<QuerySnapshot> {
        val col = ref.collection("reviews")
        return col.whereEqualTo("author", uid).get()
            .addOnSuccessListener {
                val list = mutableListOf<Review>()
                it.forEach {document ->
                    Log.d("ReviewViewModel", "Retrieved $document")
                    list.add(retrieve(document))
                    Log.d("ReviewViewModel", "Retrieved $list")
                }
                userReview.value!![uid] = list
            }
    }

    fun addReview(context: Context, id: Int, user: FirebaseUser,
                  rating: Double, content: String): Task<Void> {
        val data = mapOf(
            "id" to UUID.randomUUID(),
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
                reviewsUpdate(user, 1)
            }
            .addOnFailureListener {
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
                            download(review.objectID.toInt())
                            reviewsUpdate(user, -1)
                        }
                }
            }
    }

    private fun reviewsUpdate(user: FirebaseUser, value: Long) {
        ref.collection("users").document(user.uid).update(
            "stats.reviews", FieldValue.increment(value))
    }

    private fun retrieve(doc: DocumentSnapshot): Review {
        val data = doc.data
        return Review(
            data?.get("id") as HashMap<*, *>,
            data["object_id"] as Long,
            data["author"] as String,
            data["rating"] as Double,
            data["timestamp"] as Timestamp,
            data["content"] as String,
            data["usefulness"] as Long
        )
    }

}

data class Review(
    val reviewID: HashMap<*, *>,
    val objectID: Long,
    val author: String,
    val rating: Double,
    val timestamp: Timestamp,
    val content: String,
    val usefulness: Long
)