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


class ProfileViewModel :ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid

    private val _postList = MutableLiveData<ArrayList<Post>>()
    val postList: LiveData<ArrayList<Post>> get() = _postList

    private val _reelList = MutableLiveData<List<Reel>>()
    val reelList: LiveData<List<Reel>> get() = _reelList


    private val _userProfile = MutableLiveData<UserModel?>()
    val userProfile: LiveData<UserModel?> get() = _userProfile



    init {
        fetchUserPosts()
        fetchUserProfile()
        fetchUserReel()
    }



    fun fetchUserReel() {
        currentUserUid?.let { uid ->
            firestore.collection("$uid${Keys.REEL}")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tempList = mutableListOf<Reel>()
                    for (document in querySnapshot.documents) {
                        val reel = document.toObject<Reel>()
                        reel?.let { tempList.add(it) }
                    }
                    _reelList.value = tempList
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
    private fun fetchUserPosts() {
        firestore.collection(Keys.POST)
            .whereEqualTo("uid", currentUserUid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                val tempList = snapshot?.documents?.mapNotNull {
                    it.toObject(Post::class.java)
                }?.sortedByDescending { it.time }
                val posts = ArrayList(tempList)
                _postList.postValue(posts)
            }
    }


  /*  private fun fetchUserProfile() {
        firestore.collection("users").document(currentUserUid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<UserModel>()
                this._userProfile.value = user
            }
            .addOnFailureListener { exception ->
                // Handle failure

            }
    }*/


    fun fetchUserProfile() {
        currentUserUid?.let { uid ->
            firestore.collection(Keys.USER_NODE)
                .document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<UserModel>()
                    _userProfile.value = user
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
}





/* fun getMyPost(): LiveData<List<Post>> {

        val posts = MutableLiveData<List<Post>>()
        val firestore = FirebaseFirestore.getInstance()


        Firebase.firestore.collection(Keys.POST)
            .whereEqualTo("uid", FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                val postList = snapshot?.documents?.mapNotNull {
                    it.toObject((Post::class.java))
                }
                    ?.sortedByDescending { it.time }
            }

    }*/