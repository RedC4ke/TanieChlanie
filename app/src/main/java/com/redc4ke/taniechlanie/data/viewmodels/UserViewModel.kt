package com.redc4ke.taniechlanie.data.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.ui.MainActivity
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class UserViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var titles: MutableMap<Int, Map<String, Any>> = mutableMapOf()
    private val defaultAvatar =
        Uri.parse("https://firebasestorage.googleapis.com/v0/b/jabot-5ce1f.appspot.com/o/users%2Fdefault%2Favatar.png?alt=media&token=66bdbd65-2fd2-4688-b130-0c2ae045eb78")
    private var currentUser = MutableLiveData<FirebaseUser?>()
    private val staticUser get() = currentUser.value
    private val userName = MutableLiveData<String>()
    private val userAvatar = MutableLiveData<Uri>()
    private val userEmail = MutableLiveData<String>()
    private val userStats = MutableLiveData<Map<String, Any>?>()
    private val userTitle = MutableLiveData<Map<String, Any?>>()
    private var favourites = MutableLiveData<ArrayList<Long>>()
    private val permissionLevel = MutableLiveData<Int>()
    private val reviewBan = MutableLiveData<Boolean>()

    init {
        downloadTitles()
    }

    fun login(context: Context, user: FirebaseUser?, new: Boolean = false) {
        currentUser.value = user

        if (new) {
            createUser(user!!, context)
        } else if (user != null) {
            downloadData()
        }
    }

    private fun createUser(user: FirebaseUser, context: Context?) {
        var avatar = defaultAvatar.toString()
        val stats = mapOf(
            "submits" to 0,
            "reviews" to 0,
            "commitment" to 0
        )

        userStats.value = stats
        userName.value = user.displayName
        userEmail.value = user.email

        if (user.photoUrl == null) {
            setAvatarUrl(defaultAvatar, object : RequestListener {
                override fun onComplete(resultCode: Int) {
                    userAvatar.value = defaultAvatar
                }
            })
        } else {
            avatar = user.photoUrl.toString()
        }

        val data = mapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "created" to Timestamp.now(),
            "stats" to stats,
            "title" to 1,
            "avatar" to avatar
        )

        firestore
            .runTransaction {
                it.set(firestore.collection("users").document(user.uid), data)
                it.set(
                    firestore.collection("security").document(user.uid),
                    "permissionLevel" to 0
                )
            }
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
    }

    fun getPermissionLevel(): Int {
        return if (staticUser == null) {
            -1
        } else {
            permissionLevel.value ?: 0
        }
    }

    fun hasReviewBan(): Boolean {
        return reviewBan.value ?: false
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

    fun getAvatarUrl(): MutableLiveData<Uri> {
        return userAvatar
    }

    fun getUserName(): MutableLiveData<String> {
        return userName
    }

    fun getFavourites(): MutableLiveData<ArrayList<Long>> {
        return favourites
    }

    fun getEmail(): MutableLiveData<String> {
        return userEmail
    }

    fun getOtherUser(uid: String): LiveData<UserData?> {
        val returnValue = MutableLiveData<UserData?>()
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener {
                val user = UserData(
                    uid,
                    it.getString("name"),
                    it.getTimestamp("created"),
                    it.get("stats") as? Map<String, Any> ?: mapOf(),
                    it.getLong("title"),
                    it.getString("avatar"),
                    it.get("favourites") as? ArrayList<Long> ?: ArrayList()
                )

                returnValue.value = user
            }

        return returnValue
    }

    private fun downloadData() {
        if (staticUser != null) {
            userName.value = staticUser?.displayName
            userAvatar.value = staticUser?.photoUrl
            userEmail.value = staticUser?.email

            firestore.collection("users").document(staticUser!!.uid).get()
                .addOnSuccessListener {
                    if (!it.exists()) {
                        createUser(staticUser!!, null)
                        downloadData()
                    } else {
                        downloadPermissions()

                        val data = it.data
                        if (data != null) {
                            val userData = UserData(
                                data["uid"] as String,
                                data["name"] as String?,
                                data["created"] as Timestamp?,
                                data["stats"] as Map<String, Int>,
                                data["title"] as Long,
                                data["avatar"] as String?,
                                data["favourites"] as ArrayList<Long>? ?: arrayListOf()
                            )
                            userStats.value = userData.stats
                            userTitle.value =
                                titles[(data["title"] as Long).toInt()] ?: mapOf("name" to "n/a")
                            favourites.value = userData.favourites

                            if (staticUser != null) userData.integrityCheck(firestore, staticUser!!)
                        }
                    }
                }
                .addOnFailureListener {
                    if ((it as FirebaseFirestoreException).code ==
                        FirebaseFirestoreException.Code.NOT_FOUND
                    ) {
                        createUser(staticUser!!, null)
                        downloadData()
                    }
                }
        }
    }

    private fun downloadPermissions() {
        if (staticUser != null)
            firestore.collection("security").document(staticUser!!.uid)
                .get()
                .addOnSuccessListener {
                    permissionLevel.value = it.getLong("permissionLevel")?.toInt()
                    reviewBan.value = it.getBoolean("hasReviewBan")
                }
                .addOnFailureListener {
                    Log.w("UserViewModel", "downloadPermissions failed: $it")
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

    fun setAvatar(file: File, requestListener: RequestListener) {
        try {
            val imageUri = Uri.fromFile(file)
            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
                .child(staticUser!!.uid)
                .child(UUID.randomUUID().toString() + ".jpg")

            storageRef.putFile(imageUri)
                .addOnSuccessListener { _ ->
                    storageRef.downloadUrl
                        .addOnSuccessListener {
                            setAvatarUrl(it, requestListener)
                        }
                }

        } catch (e: Exception) {
            requestListener.onComplete(RequestListener.OTHER)
        }
    }

    private fun setAvatarUrl(uri: Uri, requestListener: RequestListener) {
        staticUser?.updateProfile(
            UserProfileChangeRequest.Builder().setPhotoUri(uri).build()
        )
            ?.addOnSuccessListener {
                firestore.collection("users").document(staticUser?.uid.toString())
                    .update("avatar", uri.toString())
                    .addOnSuccessListener {
                        userAvatar.value = uri
                        requestListener.onComplete(RequestListener.SUCCESS)
                    }
                    .addOnFailureListener {
                        requestListener.onComplete(RequestListener.OTHER)
                    }
            }
            ?.addOnFailureListener {
                requestListener.onComplete(RequestListener.OTHER)
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
                        userName.value = name
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
                userEmail.value = address
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
    val stats: Map<String, Any>,
    val title: Long?,
    var avatar: String?,
    var favourites: ArrayList<Long>
) {
    fun integrityCheck(firestore: FirebaseFirestore, user: FirebaseUser) {
        if (uid == user.uid) {
            if (name != user.displayName || avatar != user.photoUrl.toString()) {

                firestore.collection("users").document(uid)
                    .update(
                        mapOf(
                            "name" to user.displayName,
                            "avatar" to user.photoUrl.toString()
                        )
                    )
            }
        }
    }
}
