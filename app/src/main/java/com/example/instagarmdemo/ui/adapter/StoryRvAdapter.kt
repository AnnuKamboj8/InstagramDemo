package com.example.instagarmdemo.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.StroryListItemBinding
import com.example.instagarmdemo.databinding.StoryProfileItemBinding
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.ui.post.PostActivity
import com.example.instagarmdemo.ui.post.StoryViewActivity
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class StoryRvAdapter(private val context: Context, private val followList: ArrayList<UserModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PROFILE = 0
        private const val VIEW_TYPE_LIST = 1
    }

    inner class ProfileViewHolder(val binding: StoryProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ListViewHolder(val binding: StroryListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROFILE -> {
                val binding = StoryProfileItemBinding.inflate(LayoutInflater.from(context), parent, false)
                ProfileViewHolder(binding)
            }
            VIEW_TYPE_LIST -> {
                val binding = StroryListItemBinding.inflate(LayoutInflater.from(context), parent, false)
                ListViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_PROFILE -> {
                val profileHolder = holder as ProfileViewHolder
                var storyImageUrl: String?
                var hasAddedStory :Boolean

                // Retrieve the current user's data
                Firebase.firestore.collection(Keys.USER_NODE)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val user: UserModel? = documentSnapshot.toObject<UserModel>()
                        user?.let {
                            if (!it.image.isNullOrEmpty()) {
                                storyImageUrl = it.storyImageUrl
                                hasAddedStory=it.hasAddedStory

                                Glide.with(context).load(it.image)
                                    .into(profileHolder.binding.storyProfileUserImage)

                                profileHolder.binding.storyProfileUserImage.setOnClickListener {
                                    Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()

                                    profileHolder.binding.storyProfileUserImage.borderColor = ContextCompat.getColor(context, R.color.subTextHint)
                                    profileHolder.binding.storyProfileUserImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.after_click_borderWidth)

                                    if (hasAddedStory) {

                                        val intent = Intent(context, StoryViewActivity::class.java)
                                        intent.putExtra(Keys.STORY_IMAGE_URL, storyImageUrl)
                                        context.startActivity(intent)
                                    } else {
                                        var fromStory = true
                                        val intent = Intent(context, PostActivity::class.java)
                                        intent.putExtra(Keys.FROM_STORY, fromStory)
                                        context.startActivity(intent)
                                    }
                                }

                                if (it.hasAddedStory) {
                                    profileHolder.binding.plusIcon.visibility = View.GONE
                                    profileHolder.binding.storyProfileUserImage.borderColor = ContextCompat.getColor(context, R.color.green)
                                    profileHolder.binding.storyProfileUserImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.borderWidth)
                                }
                            }
                        }
                    }
            }

            VIEW_TYPE_LIST -> {
                val listHolder = holder as ListViewHolder
                val currentUser = followList[position - 1] // Adjust position to account for profile view type
                if (currentUser.hasAddedStory) {
                    // User has added a story, show in the list
                    Glide.with(context).load(currentUser.image).placeholder(R.drawable.profile)
                        .into(listHolder.binding.storyProfileImage)
                    listHolder.binding.storyProfileName.text = currentUser.name
                    listHolder.binding.storyProfileImage.borderColor = ContextCompat.getColor(context, R.color.highlight_pink)
                    listHolder.binding.storyProfileImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.borderWidth)

                    // Set click listener to open the story view activity
                    listHolder.binding.storyProfileImage.setOnClickListener {
                        listHolder.binding.storyProfileImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.after_click_borderWidth)
                        listHolder.binding.storyProfileImage.borderColor = ContextCompat.getColor(context, R.color.subTextHint)
                        val intent = Intent(context, StoryViewActivity::class.java)
                        intent.putExtra(Keys.STORY_IMAGE_URL, currentUser.storyImageUrl)
                        context.startActivity(intent)
                    }
                } else {
                    // User has not added a story, hide from the list
                    listHolder.binding.root.visibility = View.GONE
                    listHolder.binding.root.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }
        }
    }

    override fun getItemCount(): Int {

        return followList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_PROFILE
        } else {
            VIEW_TYPE_LIST
        }
    }



}

