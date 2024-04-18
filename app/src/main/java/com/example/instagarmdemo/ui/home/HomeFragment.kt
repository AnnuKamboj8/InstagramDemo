package com.example.instagarmdemo.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.instagarmdemo.databinding.FragmentHomeBinding
import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.Models.Story
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.ui.adapter.HomePostAdapter
import com.example.instagarmdemo.ui.adapter.StoryRvAdapter
import com.example.instagarmdemo.ui.mvvm.HomeViewModel
import com.example.instagarmdemo.utility.Keys
import com.example.instagarmdemo.utility.PreferenceHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomePostAdapter

    private var postList = ArrayList<Post>()
    private var followList = ArrayList<UserModel>()

    private lateinit var storyAdapter: StoryRvAdapter
    private val viewModel: HomeViewModel by viewModels()
    private val pHelpers by lazy {
        PreferenceHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPostRecyclerView()
        setupStoryRecyclerView()
        observeViewModel()
    }

    private fun setupPostRecyclerView() {
        adapter = HomePostAdapter(requireContext(), postList, pHelpers, FirebaseAuth.getInstance().currentUser!!.uid)
        binding.homePostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homePostRecyclerView.adapter = adapter
    }

    private fun setupStoryRecyclerView() {
        storyAdapter = StoryRvAdapter(requireContext(), followList)
        binding.storyRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.storyRecyclerView.adapter = storyAdapter
    }

 /*   private fun fetchFollowedUsers() {
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + Keys.FOLLOW)
            .get()
            .addOnSuccessListener { followSnapshot ->
                val followedUsers = followSnapshot.toObjects<UserModel>()
                followList.clear()

                for (user in followedUsers) {
                    if (user.hasAddedStory) {
                        followList.add(user)
                    }
                }
                storyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }*/



    private fun observeViewModel() {
        viewModel.fetchPosts()
        viewModel.postList.observe(viewLifecycleOwner) { posts ->
            postList.clear()
            postList.addAll(posts)
            adapter.notifyDataSetChanged()
            loadLikeStates()
        }

        viewModel.fetchFollowedUsers()
       
        viewModel.followedList.observe(viewLifecycleOwner){storyFollowUser ->
            followList.clear()
            followList.addAll(storyFollowUser)
            storyAdapter.notifyDataSetChanged()

        }
    }

    private fun loadLikeStates() {
        postList.forEach { post ->
            val isLiked = pHelpers.loadLikeState(post.postId, FirebaseAuth.getInstance().currentUser!!.uid)
            post.isLikedImage = isLiked
        }
        adapter.notifyDataSetChanged()
    }
}


/*val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
binding = FragmentHomeBinding.inflate(inflater, container, false)
adapter = HomePostAdapter(requireContext(), postList, preferenceHelper,currentUserID)
binding.homePostRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
binding.homePostRecyclerView.adapter = adapter
val newPostList: List<Post> = arrayListOf()

    adapter.updatePosts(newPostList)



Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
    .get()
    .addOnSuccessListener { followSnapshot ->
        val followedUsers = followSnapshot.toObjects<UserModel>()

        lifecycleScope.launch {
            // add current user's UID in the list
            val userUIDs = followedUsers.map { it.uid } + FirebaseAuth.getInstance().currentUser!!.uid

            for (userUID in userUIDs) {
                val posts = getPostsFromUser(userUID)
                postList.sortedByDescending { it.time }
                postList.addAll(posts)
                adapter.notifyDataSetChanged()
            }
        }
    }

return binding.root
}
private suspend fun getPostsFromUser(uid: String): List<Post> {
return suspendCancellableCoroutine { continuation ->
    Firebase.firestore.collection(Keys.POST).whereEqualTo("uid", uid)
        .get()
        .addOnSuccessListener { postSnapshot ->
            val tempList = ArrayList<Post>()
            for (postDocument in postSnapshot.documents) {
                val post: Post = postDocument.toObject<Post>()!!
                tempList.add(post)
            }

            continuation.resume(tempList) {
            }
        }
        .addOnFailureListener { exception ->
            Log.e("HomeFragment", "Error getting posts: $exception")
            continuation.resumeWithException(exception)
        }
}
}

*/
