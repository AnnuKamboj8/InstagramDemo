package com.example.instagarmdemo.ui.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.Models.Reel
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid

    private val _postList = MutableLiveData<List<Post>>()
    val postList: LiveData<List<Post>> get() = _postList


    fun fetchPosts() {
        firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
            .addSnapshotListener { followSnapshot, _ ->
                val followedUsers = followSnapshot?.toObjects<UserModel>() ?: emptyList()

                GlobalScope.launch(Dispatchers.Main) {
                    val userUIDs = followedUsers.map { it.uid } + FirebaseAuth.getInstance().currentUser!!.uid

                    val allPosts = mutableListOf<Post>()

                    for (userUID in userUIDs) {
                        val posts = getPostsFromUser(userUID)
                        allPosts.addAll(posts)
                    }
                    _postList.value = allPosts.sortedByDescending { it.time }
                }
            }
    }

    private suspend fun getPostsFromUser(uid: String): List<Post> {
        val postSnapshot = firestore.collection(Keys.POST)
            .whereEqualTo("uid", uid)
            .get()
            .await()

        return postSnapshot.toObjects<Post>()
    }

    fun loadReels(
        onSuccess: (List<Reel>) -> Unit,
        onFailure: () -> Unit
    ) {
        Firebase.firestore.collection(Keys.REEL)
            .get()
            .addOnSuccessListener { documents ->
                val reelList = documents.mapNotNull { it.toObject<Reel>() }
                onSuccess(reelList)
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}
