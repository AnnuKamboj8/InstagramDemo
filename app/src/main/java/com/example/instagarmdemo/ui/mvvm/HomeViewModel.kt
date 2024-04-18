package com.example.instagarmdemo.ui.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.Models.Reel
import com.example.instagarmdemo.ui.Models.Story
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
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

    private val _storyList = MutableLiveData<List<Story>>()
    val storyList: LiveData<List<Story>> get() = _storyList

    private val _followedList = MutableLiveData<List<UserModel>>()
    val followedList: LiveData<List<UserModel>> get() = _followedList


    fun fetchPosts() {
        firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
            .addSnapshotListener { followSnapshot, _ ->
                val followedUsers = followSnapshot?.toObjects<UserModel>() ?: emptyList()

                GlobalScope.launch(Dispatchers.Main) {
                    val userUIDs =
                        followedUsers.map { it.uid } + FirebaseAuth.getInstance().currentUser!!.uid

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

    private suspend fun getStoriesFromUser(uid: String): List<Story> {
        val storySnapshot = firestore.collection(Keys.STORY)
            .whereEqualTo("uid", uid)
            .get()
            .await()

        return storySnapshot.toObjects<Story>()
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

    fun fetchStories() {
        firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
            .addSnapshotListener { followSnapshot, _ ->
                val followedUsers = followSnapshot?.toObjects<UserModel>() ?: emptyList()

                GlobalScope.launch(Dispatchers.Main) {
                    val userUIDs =
                        followedUsers.map { it.uid } + FirebaseAuth.getInstance().currentUser!!.uid

                    val allPosts = mutableListOf<Story>()

                    for (userUID in userUIDs) {
                        val story = getStoriesFromUser(userUID)
                        allPosts.addAll(story)


                    }
                    _storyList.value = allPosts.sortedByDescending { it.time }
                }
            }
    }

    fun  fetchFollowedUsers() {

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + Keys.FOLLOW)
            .get()
            .addOnSuccessListener { followSnapshot ->
                val followedUsers = followSnapshot.toObjects<UserModel>()
                    .filter { it.hasAddedStory } // Filter users with added stories
                _followedList.value = followedUsers


            }
            .addOnFailureListener { exception ->
                // Handle failure
            }


        }



fun  fetchStoryTime() {
    var timeString: String
    Firebase.firestore.collection(Keys.STORY)
        .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
        .addOnSuccessListener {
            val story = it.toObject<Story>()!!
            timeString = story.time

        }
}



    }

