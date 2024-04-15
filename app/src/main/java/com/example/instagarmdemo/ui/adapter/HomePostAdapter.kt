package com.example.instagarmdemo.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.HomePostItemBinding

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

        // Load user data
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

        // Load time
        try {
            holder.binding.timeTextView.text = TimeAgo.using(post.time.toLong())
        } catch (e: Exception) {
            holder.binding.timeTextView.text = ""
        }

        // Share button click listener
        holder.binding.homeShareImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, post.postUrl)
            context.startActivity(intent)
        }

   /*     holder.binding.homeLikeImage.setOnClickListener {
            val isLiked = preferenceHelper.loadLikeState(post.postId, currentUserID)
            val newLikeState = !isLiked

            if (newLikeState) {
                post.likeCount++
            } else {
                post.likeCount--
            }

            // Save new like state
            post.isLiked = newLikeState
            preferenceHelper.saveLikeState(post.postId, currentUserID, newLikeState)


            // Notify adapter of data change
            notifyItemChanged(holder.adapterPosition)

            updateLikeCount(holder, post)
            updateLikeIcon(holder, post)

        }*/
        holder.binding.homeLikeImage.setOnClickListener {
            toggleLikeState(post.postId)
            setLikeIcon(post.postId, holder.binding.homeLikeImage)
        }
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

    private fun toggleLikeState(postId: String) {
        val isLiked =
            preferenceHelper.loadLikeState(postId, FirebaseAuth.getInstance().currentUser!!.uid)

        val updatedLikeState = !isLiked
        preferenceHelper.saveLikeState(
            postId,
            FirebaseAuth.getInstance().currentUser!!.uid,
            updatedLikeState
        )
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


