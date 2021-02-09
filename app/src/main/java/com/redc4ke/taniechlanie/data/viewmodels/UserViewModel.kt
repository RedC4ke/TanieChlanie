package com.redc4ke.taniechlanie.data.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.BuildConfig
import java.io.File
import java.lang.Exception
import java.lang.reflect.Array.get
import java.util.*

class UserViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var titles: MutableMap<Int, Map<String, Any>> = mutableMapOf()
    private val defaultAvatar = Uri.parse("gs://jabot-5ce1f.appspot.com/users/default/avatar.png")
    private var currentUser = MutableLiveData<FirebaseUser?>()
    private val staticUser get() = currentUser.value
    private val userStats = MutableLiveData<Map<String, Any>>()
    private val userTitle = MutableLiveData<Map<String, Any?>>()
    private val update = MutableLiveData<Boolean>()

    init {
        downloadTitles()
        update.value = false
    }

    fun login(context: Context, user: FirebaseUser?, new: Boolean = false) {
        currentUser.value = user

        if (new) {
            val stats = mapOf(
                "submits" to 0,
                "reviews" to 0
            )
            userStats.value = stats
            setAvatarUrl(defaultAvatar)

            val data = mapOf(
                "uid" to user!!.uid,
                "created" to BuildConfig.VERSION_NAME + "; " + Date(),
                "groups" to arrayListOf(
                    "user"
                ),
                "stats" to stats,
                "title" to 1
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
                        Toast.LENGTH_LONG).show()
                }
        } else if (user != null) {
            downloadData()
        }
    }

    fun getUser(): MutableLiveData<FirebaseUser?> {
        return currentUser
    }

    fun getStats(): MutableLiveData<Map<String, Any>> {
        return userStats
    }

    fun getTitle(): MutableLiveData<Map<String, Any?>> {
        return userTitle
    }

    fun getUserUpdates(): MutableLiveData<Boolean> {
        return update
    }

    private fun downloadData() {
        if (staticUser != null) {
            firestore.collection("users").document(staticUser!!.uid).get()
                .addOnSuccessListener {
                    val data = it.data
                    if (data != null) {
                        userStats.value = data["stats"] as Map<String, Any>?
                        userTitle.value =
                            titles[(data["title"] as Long).toInt()] ?: mapOf("name" to "n/a")
                    }
                    update.value = !(update.value)!!
                }
        }
    }

    private fun downloadTitles() {
        firestore.collection("titles").get()
            .addOnSuccessListener {
                it.forEach{document ->
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

    fun setAvatar(context: Context, file: File) {
        try {
            val imageUri = Uri.fromFile(file)
            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
                .child(staticUser!!.uid)
                .child(file.name)

            storageRef.putFile(imageUri)
                .addOnSuccessListener {imageUpload ->
                    imageUpload.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            setAvatarUrl(it)
                        }
                }

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Błąd: $e",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAvatarUrl(uri: Uri) {
        staticUser?.updateProfile(
            UserProfileChangeRequest.Builder().setPhotoUri(uri).build())
            ?.addOnSuccessListener {
                update.value = !(update.value)!!
                Log.d("user", "Avatar uri set to: $uri")
            }
            ?.addOnFailureListener {
                Log.d("user","Avatar uri not set: $it")
            }
    }

    fun setDisplayName(name: String) {
        staticUser?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(name).build())
            ?.addOnSuccessListener {
                update.value = !(update.value)!!
                Log.d("user", "DisplayName set to: $name")
            }
            ?.addOnFailureListener {
                Log.d("user", "Display name not set: $it")
            }
    }

}