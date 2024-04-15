package com.example.instagarmdemo.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagarmdemo.databinding.FragmentHomeBinding
import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.adapter.HomePostAdapter
import com.example.instagarmdemo.ui.mvvm.HomeViewModel
import com.example.instagarmdemo.utility.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomePostAdapter
    private var postList = ArrayList<Post>()
    private val viewModel: HomeViewModel by viewModels()
    private val pHelpers by lazy {

        PreferenceHelper(requireContext())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val preferenceHelper = PreferenceHelper(requireContext())
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = HomePostAdapter(requireContext(), postList, preferenceHelper, currentUserID)
        binding.homePostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homePostRecyclerView.adapter = adapter

        viewModel.fetchPosts()
        viewModel.postList.observe(viewLifecycleOwner) { postList ->
            adapter.updatePosts(postList)
            loadLikeStates()
        }


        return binding.root
    }

    private fun loadLikeStates() {
        postList.forEach { post ->
            val isLiked =
                pHelpers.loadLikeState(post.postId, FirebaseAuth.getInstance().currentUser!!.uid)
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
