package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class UserViewModel: ViewModel() {
    private var currentUser = MutableLiveData<FirebaseUser?>()

    fun login(user: FirebaseUser?) {
        currentUser.value = user
    }

    fun getUser(): MutableLiveData<FirebaseUser?> {
        return currentUser
    }

    fun logout() {
        currentUser.value = null
    }
}