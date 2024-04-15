package com.example.instagarmdemo.ui.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagarmdemo.ui.Models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignViewModel: ViewModel() {

    private val _userRegistered = MutableLiveData<Boolean>()
    val userRegistered: LiveData<Boolean> get() = _userRegistered

    private val _userProfile = MutableLiveData<UserModel>()
    val userProfile: LiveData<UserModel> get() = _userProfile

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String>
        get() = _loginResult


    fun createUserWithEmailAndPassword(email: String, name: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid ?: ""

                    val usersCollection = FirebaseFirestore.getInstance().collection("User")
                    val userDocument = usersCollection.document(userId)
                    val userData = hashMapOf(
                        "uid" to userId,
                        "email" to email,
                        "name" to name,
                        "password" to password
                    )
                    userDocument.set(userData)
                        .addOnSuccessListener {
                            Log.d("tag", "**** User data added to Firestore")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error adding user data: ${e.message}")
                        }
                } else {
                    onFailure("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    fun updateUserProfile(user: UserModel, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val usersCollection = FirebaseFirestore.getInstance().collection("User")
        val userDocument = usersCollection.document(userId)

        userDocument.set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure("Error updating user data: ${e.message}")
            }
    }

    fun signIn(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loginResult.value = "Success"
            }
            .addOnFailureListener { e ->
                _loginResult.value = e.localizedMessage ?: "Unknown error"
            }
    }

}












