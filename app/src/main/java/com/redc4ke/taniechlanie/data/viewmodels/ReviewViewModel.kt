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

    fun download(id: Int): com.google.android.gms.tasks.Task<QuerySnapshot> {
        val col = ref.collection("reviews")
            .document("accepted").collection(id.toString())

        return col.get()
            .addOnSuccessListener {
                val list = mutableListOf<Review>()
                it.forEach {doc ->
                    val data = doc.data
                    val review = Review(
                        data["id"] as HashMap<*, *>,
                        data["object_id"] as Long,
                        data["author"] as String,
                        data["rating"] as Double,
                        data["timestamp"] as Timestamp,
                        data["content"] as String,
                        data["usefulness"] as Long
                    )
                    list.add(review)
                }
                add(id, list)
                Log.d("ReviewViewModel", "Added ${list.size} reviews for $id")
            }
            .addOnFailureListener {
                Log.d("ReviewViewModel", "Review download failed: $it")
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
        return ref.collection("reviews").document("accepted")
            .collection(id.toString()).document(user.uid).set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(context, "Recenzja dodana!", Toast.LENGTH_SHORT).show()
                download(id)
                reviewsUpdate(user, 1)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Nieznany błąd", Toast.LENGTH_LONG).show()
            }
    }

    fun remove(review: Review, user: FirebaseUser): Task<Void> {
        return ref.collection("reviews").document("accepted")
            .collection(review.objectID.toString()).document(user.uid).delete()
            .addOnSuccessListener {
                download(review.objectID.toInt())
                reviewsUpdate(user, -1)
            }
    }

    fun reviewsUpdate(user: FirebaseUser, value: Long) {
        ref.collection("users").document(user.uid).update(
            "stats.reviews", FieldValue.increment(value))
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