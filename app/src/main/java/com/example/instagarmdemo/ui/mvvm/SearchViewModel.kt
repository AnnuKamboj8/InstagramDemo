package com.example.instagarmdemo.ui.mvvm

import androidx.lifecycle.ViewModel
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class SearchViewModel : ViewModel() {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUserEmail: String? by lazy { FirebaseAuth.getInstance().currentUser?.email }
    fun searchUsers(query: String, onSuccess: (List<UserModel>) -> Unit, onFailure: () -> Unit) {
        firestore.collection(Keys.USER_NODE)
            .get()
            .addOnSuccessListener { documents ->
                val userList = mutableListOf<UserModel>()
                for (document in documents) {
                    val user = document.toObject<UserModel>()
                    if (user.email != currentUserEmail && user.name?.toLowerCase()?.contains(query.toLowerCase()) == true) {
                        userList.add(user)
                    }
                }
                onSuccess(userList)
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun loadAllUsers(onSuccess: (List<UserModel>) -> Unit, onFailure: () -> Unit) {
        firestore.collection(Keys.USER_NODE)
            .get()
            .addOnSuccessListener { documents ->
                val userList = mutableListOf<UserModel>()
                for (document in documents) {
                    val user = document.toObject<UserModel>()
                    if (user.email != currentUserEmail) {
                        userList.add(user)
                    }
                }
                onSuccess(userList)
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}