package com.example.instagarmdemo.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.StroryListItemBinding
import com.example.instagarmdemo.databinding.StoryProfileItemBinding
import com.example.instagarmdemo.ui.Models.Story
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.ui.post.PostActivity
import com.example.instagarmdemo.ui.post.StoryViewActivity
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class StoryRvAdapter(private val context: Context, private val storyList: ArrayList<Story>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PROFILE = 0
        private const val VIEW_TYPE_LIST = 1
    }

    inner class ProfileViewHolder(val binding: StoryProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

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
             //   val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                val currentUserStory = storyList.find { it.uid == FirebaseAuth.getInstance().currentUser?.uid }

                currentUserStory?.let { story ->
                    Glide.with(context)
                        .load(story.storyUrl)
                        .into(profileHolder.binding.storyProfileUserImage)
                }
                    // Check if the story has expired
                val currentTime = System.currentTimeMillis()

                Firebase.firestore.collection(Keys.USER_NODE)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val user: UserModel? = documentSnapshot.toObject<UserModel>()
                        user?.let {
                            if (!it.image.isNullOrEmpty()) {
                                Glide.with(context).load(it.image)
                                    .into(profileHolder.binding.storyProfileUserImage)

                                if (it.hasAddedStory && (currentTime - (it.storyTime?.toLongOrNull() ?: 0)) > (24 * 60 * 60 * 1000)) {
                                    profileHolder.binding.plusIcon.visibility = View.GONE
                                    profileHolder.binding.storyProfileUserImage.borderColor = ContextCompat.getColor(context, R.color.green)
                                    profileHolder.binding.storyProfileUserImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.borderWidth)
                                } else{
                                    profileHolder.binding.plusIcon.visibility = View.VISIBLE
                                }
                                profileHolder.binding.storyProfileUserImage.setOnClickListener { view ->

                                    if (it.hasAddedStory &&  ((currentTime - (it.storyTime?.toLongOrNull() ?: 0)) > (24 * 60 * 60 * 1000))) {

                                        profileHolder.binding.plusIcon.visibility = View.GONE
                                        profileHolder.binding.storyProfileUserImage.borderColor = ContextCompat.getColor(context, R.color.subTextHint)
                                        profileHolder.binding.storyProfileUserImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.after_click_borderWidth)
                                     //   Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, StoryViewActivity::class.java)
                                        intent.putExtra(Keys.STORY_IMAGE_URL, it.storyImageUrl)
                                        context.startActivity(intent)
                                    }
                                    else {
                                        user.hasAddedStory= false
                                        profileHolder.binding.plusIcon.visibility = View.VISIBLE
                                        Toast.makeText(context, "no story", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, PostActivity::class.java)
                                        intent.putExtra(Keys.FROM_STORY, true)
                                        ContextCompat.startActivity(context, intent, null)

                                    }
                                }
                            }
                        }
                    }
            }

            VIEW_TYPE_LIST -> {

                val listHolder = holder as ListViewHolder
                val currentUser = if (position == 0) {
                    storyList[0]
                } else {
                    storyList[position - 1]
                }
                Firebase.firestore.collection(Keys.USER_NODE)
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val user: UserModel? = documentSnapshot.toObject<UserModel>()
                        user?.let {
                            Glide.with(context)
                                .load(user.image)
                                .placeholder(R.drawable.profile)
                                .into(listHolder.binding.storyProfileImage)
                            listHolder.binding.storyProfileName.text = user.name
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure to fetch user data
                    }

                listHolder.binding.storyProfileImage.borderColor = ContextCompat.getColor(context, R.color.highlight_pink)
                    listHolder.binding.storyProfileImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.borderWidth)


                    listHolder.binding.storyProfileImage.setOnClickListener {
                        listHolder.binding.storyProfileImage.borderWidth = context.resources.getDimensionPixelSize(R.dimen.after_click_borderWidth)
                        listHolder.binding.storyProfileImage.borderColor = ContextCompat.getColor(context, R.color.subTextHint)
                        val intent = Intent(context, StoryViewActivity::class.java)
                        intent.putExtra(Keys.STORY_IMAGE_URL, currentUser.storyUrl)
                        context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {

        return storyList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_PROFILE
        } else {
            VIEW_TYPE_LIST
        }

    }

    fun updatePosts(newPosts: List<Story>) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        val filteredPosts = newPosts.filter { it.uid != currentUserUid }
        val diffResult = DiffUtil.calculateDiff(PostDiffCallback(storyList, filteredPosts))
        storyList.clear()

        storyList.addAll(filteredPosts.sortedByDescending { it.time.toLong() })

        diffResult.dispatchUpdatesTo(this)
    }
    private class PostDiffCallback(
        private val oldList: List<Story>,
        private val newList: List<Story>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].storyId == newList[newItemPosition].storyId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }


}

/*   fun updateCurrentUserStory(currentUserStory: List<Story>) {

           storyList.clear()
           val currentUser = currentUserStory.find { it.uid == FirebaseAuth.getInstance().currentUser?.uid }

           if (currentUser != null) {

               storyList.add(currentUser)
           }
           notifyDataSetChanged()
       }*/
