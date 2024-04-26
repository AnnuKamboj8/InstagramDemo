package com.example.instagarmdemo.ui.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.HomePostItemBinding
import com.example.instagarmdemo.extension.showToast

import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.utility.Keys
import com.example.instagarmdemo.utility.PreferenceHelper
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class HomePostAdapter(
    private val context: Context,
    private val postList: ArrayList<Post>,
    private val preferenceHelper: PreferenceHelper,
    private val currentUserID: String
) : RecyclerView.Adapter<HomePostAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: HomePostItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomePostItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = postList[position]

        try {
            Firebase.firestore.collection(Keys.USER_NODE).document(post.uid)
                .get().addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<UserModel>()
                    if (!isActivityDestroyed(holder.binding.root.context as AppCompatActivity)) {
                        Glide.with(context)
                            .load(user?.image)
                            .placeholder(R.drawable.user)
                            .into(holder.binding.profileImage)
                    }
                    holder.binding.nameTextView.text = user?.name
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Load post data
        if (!isActivityDestroyed(holder.binding.root.context as AppCompatActivity)) {
            Glide.with(context)
                .load(post.postUrl)
                .into(holder.binding.myPostImage)
        }

        try {
            holder.binding.timeTextView.text = TimeAgo.using(post.time.toLong())
            holder.binding.homePostCaption.text=post.caption
        } catch (e: Exception) {
            holder.binding.timeTextView.text = ""
            holder.binding.homePostCaption.text=""
        }

        loadLikeCount(post.postId, holder.binding.likeTextView)

        holder.binding.homeShareImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, post.postUrl)
            context.startActivity(intent)
        }


        holder.binding.homeLikeImage.setOnClickListener {
            toggleLikeState(post)
            updateLikeIcon(holder, post)
            updateLikeCount(holder, post)
         //   setLikeIcon(post.postId, holder.binding.homeLikeImage)
        }
        // Set initial like state and count
        setLikeIcon(post.postId, holder.binding.homeLikeImage)
    }



    private fun setLikeIcon(postId: String, imageView: ImageView) {
        val isLiked =
            preferenceHelper.loadLikeState(postId, FirebaseAuth.getInstance().currentUser!!.uid)

        if (isLiked) {
            imageView.setImageResource(R.drawable.hearts)
        } else {
            imageView.setImageResource(R.drawable.ic_like)
        }
    }

    private fun toggleLikeState(post: Post) {
        if (post.postId.isNullOrEmpty()) {
            // Handle the case where postId is null or empty
            Log.e(TAG, "PostId is null or empty for the post: $post")
            return
        }

        val isLiked = preferenceHelper.loadLikeState(post.postId, FirebaseAuth.getInstance().currentUser!!.uid)
        val updatedLikeState = !isLiked

        post.isLiked = updatedLikeState
        preferenceHelper.saveLikeState(post.postId, FirebaseAuth.getInstance().currentUser!!.uid, updatedLikeState)

        val postRef = Firebase.firestore.collection(Keys.POST).document(post.postId)
        postRef.update("likeCount", post.likeCount)
            .addOnSuccessListener {
               // context.showToast("Updated like Successfully")
                Log.d(TAG, "Like count updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating like count", e)
             //   context.showToast("Error in updating like count")
            }
    }



    private fun updateLikeIcon(holder: ViewHolder, post: Post) {
        if (post.isLiked) {
            holder.binding.homeLikeImage.setImageResource(R.drawable.hearts)
        } else {
            holder.binding.homeLikeImage.setImageResource(R.drawable.ic_like)
        }
    }

    private fun updateLikeCount(holder: ViewHolder, post: Post) {
        val likeCount = post.likeCount
        if (likeCount > 0) {
            holder.binding.likeTextView.text = if (likeCount > 1) {
                "$likeCount likes"
            } else {
                "$likeCount like"
            }
        } else {
            holder.binding.likeTextView.text = ""
        }
    }


    private fun loadLikeCount(postId: String, textView: TextView) {
        if (postId.isNotEmpty()) {
            Firebase.firestore.collection(Keys.POST).document(postId)
                .get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val post = documentSnapshot.toObject<Post>()
                        post?.let {
                            textView.text = if (post.likeCount > 0) {
                                if (post.likeCount > 1) {
                                    "${post.likeCount} likes"
                                } else {
                                    "${post.likeCount} like"
                                }
                            } else {
                                ""
                            }
                        }
                    } else {

                        textView.text = ""
                    }
                }.addOnFailureListener { exception ->

                    Log.e("HomePostAdapter", "Error loading like count for post $postId", exception)
                    textView.text = ""
                }
        } else {
            textView.text = ""
        }
    }



    private fun isActivityDestroyed(activity: AppCompatActivity): Boolean {
        return activity.isDestroyed || activity.isFinishing
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun updatePosts(newPosts: List<Post>) {

        newPosts.forEach { post ->
            val isLiked = preferenceHelper.loadLikeState(post.postId, currentUserID)
            post.isLiked = isLiked
        }

        val diffResult = DiffUtil.calculateDiff(PostDiffCallback(postList, newPosts))
        postList.clear()
        postList.addAll(newPosts)
        diffResult.dispatchUpdatesTo(this)
    }

    private class PostDiffCallback(
        private val oldList: List<Post>,
        private val newList: List<Post>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].postId == newList[newItemPosition].postId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}


