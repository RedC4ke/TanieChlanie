package com.redc4ke.taniechlanie.data.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.ui.MainActivity
import java.io.File
import kotlin.collections.set

class UserViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var titles: MutableMap<Int, Map<String, Any>> = mutableMapOf()
    private val defaultAvatar =
        Uri.parse("gs://jabot-5ce1f.appspot.com/users/default/avatar.png")
    private var currentUser = MutableLiveData<FirebaseUser?>()
    private val staticUser get() = currentUser.value
    private val userStats = MutableLiveData<Map<String, Any>?>()
    private val userTitle = MutableLiveData<Map<String, Any?>>()
    private val update = MutableLiveData<Boolean>()
    private var updateValue = false
    private var favourites = MutableLiveData<ArrayList<Long>>()
    private var groups = MutableLiveData<ArrayList<String>>()

    init {
        downloadTitles()
    }

    fun login(context: Context, user: FirebaseUser?, new: Boolean = false) {
        currentUser.value = user

        if (new) {
            var avatar = defaultAvatar.toString()
            val stats = mapOf(
                "submits" to 0,
                "reviews" to 0
            )
            userStats.value = stats

            if (user!!.photoUrl == null) {
                setAvatarUrl(defaultAvatar)
            } else {
                avatar = user.photoUrl.toString()
            }

            val data = mapOf(
                "uid" to user.uid,
                "name" to user.displayName,
                "created" to Timestamp.now(),
                "groups" to arrayListOf(
                    "user"
                ),
                "stats" to stats,
                "title" to 1,
                "avatar" to avatar
            )
            firestore.collection("users").document(user.uid).set(data)
                .addOnSuccessListener {
                    downloadData()
                }
                .addOnFailureListener {
                    user.delete()
                    Toast.makeText(
                        context,
                        it.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else if (user != null) {
            downloadData()
        }
    }

    fun isModerator(): Boolean {
        return staticUser != null && groups.value?.contains("moderator") == true
    }

    fun getUser(): MutableLiveData<FirebaseUser?> {
        return currentUser
    }

    fun getStats(): MutableLiveData<Map<String, Any>?> {
        return userStats
    }

    fun getTitle(): MutableLiveData<Map<String, Any?>> {
        return userTitle
    }

    fun getUserUpdates(): MutableLiveData<Boolean> {
        return update
    }

    fun getFavourites(): MutableLiveData<ArrayList<Long>> {
        return favourites
    }

    fun downloadData() {
        if (staticUser != null) {
            firestore.collection("users").document(staticUser!!.uid).get()
                .addOnSuccessListener {
                    val data = it.data
                    if (data != null) {
                        val userData = UserData(
                            data["uid"] as String,
                            data["name"] as String?,
                            data["created"] as Timestamp?,
                            data["groups"] as ArrayList<String>?,
                            data["stats"] as Map<String, Int>,
                            data["title"] as Long,
                            data["avatar"] as String?,
                            data["favourites"] as ArrayList<Long>? ?: arrayListOf()
                        )
                        userStats.value = userData.stats
                        userTitle.value =
                            titles[(data["title"] as Long).toInt()] ?: mapOf("name" to "n/a")
                        favourites.value = userData.favourites
                        groups.value = userData.groups ?: arrayListOf()

                        if (staticUser != null) userData.integrityCheck(firestore, staticUser!!)
                    }
                    update.value = !updateValue
                }
        }
    }

    private fun downloadTitles() {
        firestore.collection("titles").get()
            .addOnSuccessListener {
                it.forEach { document ->
                    val data = document.data
                    titles[(data["id"] as Long).toInt()] = mapOf(
                        "name" to data["name"] as String
                    )
                }
                downloadData()
            }
            .addOnFailureListener {
                Log.w("UserViewModel", it.toString())
            }
    }

    fun addFavourite(context: Context, alcoObject: AlcoObject) {
        if (staticUser != null) {
            firestore.collection("users").document(staticUser!!.uid)
                .update("favourites", FieldValue.arrayUnion(alcoObject.id))
                .addOnSuccessListener {
                    Toast.makeText(
                        context, context.getString(R.string.details_favtoast),
                        Toast.LENGTH_SHORT
                    ).show()
                    downloadData()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context, context.getString(R.string.error, it.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun removeFavourite(context: Context, alcoObject: AlcoObject) {
        if (staticUser != null) {
            firestore.collection("users").document(staticUser!!.uid)
                .update("favourites", FieldValue.arrayRemove(alcoObject.id))
                .addOnSuccessListener {
                    Toast.makeText(
                        context, context.getString(R.string.details_favremove),
                        Toast.LENGTH_SHORT
                    ).show()
                    downloadData()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context, context.getString(R.string.error, it.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun setAvatar(context: Context, file: File) {
        try {
            val imageUri = Uri.fromFile(file)
            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
                .child(staticUser!!.uid)
                .child(file.name)

            storageRef.putFile(imageUri)
                .addOnSuccessListener { imageUpload ->
                    imageUpload.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            setAvatarUrl(it)
                        }
                }

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Błąd: $e",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setAvatarUrl(uri: Uri) {
        staticUser?.updateProfile(
            UserProfileChangeRequest.Builder().setPhotoUri(uri).build()
        )
            ?.addOnSuccessListener {
                firestore.collection("users").document(staticUser?.uid.toString())
                    .update("avatar", uri)
                    .addOnSuccessListener {
                        update.value = !updateValue
                        Log.d("userVM", "Avatar uri set to: $uri")
                    }
                    .addOnFailureListener {
                        Log.d("userVM", "Avatar uri sync failed: $it")
                    }
            }
            ?.addOnFailureListener {
                Log.d("userVM", "Avatar uri not set: $it")
            }
    }

    fun setDisplayName(name: String) {
        staticUser?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(name).build()
        )
            ?.addOnSuccessListener {
                firestore.collection("users").document(staticUser?.uid.toString())
                    .update("name", name)
                    .addOnSuccessListener {
                        update.value = !updateValue
                        Log.d("userVM", "DisplayName set to: $name")
                    }
                    .addOnFailureListener {
                        Log.d("userVM", "Display name sync failed: $it")
                    }
            }
            ?.addOnFailureListener {
                Log.d("userVM", "Display name not set: $it")
            }
    }

    fun setEmail(address: String, context: Context) {
        staticUser?.updateEmail(address)
            ?.addOnSuccessListener {
                update.value = !updateValue
                Toast.makeText(
                    context, "Adres e-mail został zaktualizowany",
                    Toast.LENGTH_LONG
                ).show()
            }
            ?.addOnFailureListener {
                Toast.makeText(
                    context, "Błąd: $it",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun setPassword(password: String, context: Context) {
        staticUser?.updatePassword(password)
            ?.addOnSuccessListener {
                update.value = !updateValue
                Toast.makeText(
                    context, "Hasło zostało zaktualizowane",
                    Toast.LENGTH_LONG
                ).show()
            }
            ?.addOnFailureListener {
                Toast.makeText(
                    context, "Błąd: $it",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun deleteAccount(activity: MainActivity) {
        val uid = staticUser?.uid
        staticUser?.delete()
            ?.addOnSuccessListener {
                firestore.collection("users").document(uid!!).delete()
                update.value = !updateValue
                activity.findNavController(R.id.alcoListNH)
                    .navigate(R.id.action_profile_dest_to_list_dest)
                Toast.makeText(
                    activity, "Konto usunięte",
                    Toast.LENGTH_LONG
                ).show()
            }
            ?.addOnFailureListener {
                Toast.makeText(
                    activity, "Błąd: $it",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

}

data class UserData(
    val uid: String,
    var name: String?,
    val created: Timestamp?,
    val groups: ArrayList<String>?,
    val stats: Map<String, Any>,
    val title: Long?,
    var avatar: String?,
    var favourites: ArrayList<Long>
) {
    fun integrityCheck(firestore: FirebaseFirestore, user: FirebaseUser) {
        if (uid == user.uid) {
            if (name != user.displayName || avatar != user.photoUrl.toString()) {

                firestore.collection("users").document(uid).update(
                    mapOf(
                        "name" to user.displayName,
                        "avatar" to user.photoUrl.toString()
                    )
                )
                    .addOnSuccessListener {
                        Log.d("UserData", "Updated!")
                    }
                    .addOnFailureListener {
                        Log.d("UserData", "Error: $it")
                    }
            }
        }
    }
}
