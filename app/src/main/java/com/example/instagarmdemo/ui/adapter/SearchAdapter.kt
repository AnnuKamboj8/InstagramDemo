package com.example.instagarmdemo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.SearchListItemBinding
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class SearchAdapter(var context: Context, var userList: ArrayList<UserModel>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: SearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SearchListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(context).load(user.image).placeholder(R.drawable.profile)
            .into(holder.binding.profileImage)
        holder.binding.searchNameTextView.text = user.name

        // Check if the user is followed
        checkIfFollowed(user.email, holder)

        holder.binding.followButton.setOnClickListener {
            if (user.isFollow) {
                // Remove user from following list
                Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
                    .whereEqualTo("email", user.email).get().addOnSuccessListener { documents ->
                        Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
                            .document(documents.documents[0].id).delete()
                        // Update follow state in UserModel
                        user.isFollow = false
                        // Update follow button text
                        holder.binding.followButton.text = context.getString(R.string.follow)
                    }
            } else {
                // Add user to following list
                Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
                    .document().set(user)
                // Update follow state in UserModel
                user.isFollow = true
                // Update follow button text
                holder.binding.followButton.text = context.getString(R.string.unfollow)
            }
        }
    }

    private fun checkIfFollowed(email: String?, holder: ViewHolder) {
        Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.FOLLOW)
            .whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // User is followed
                    userList.find { it.email == email }?.isFollow = true
                    holder.binding.followButton.text = context.getString(R.string.unfollow)
                } else {
                    // User is not followed
                    userList.find { it.email == email }?.isFollow = false
                    holder.binding.followButton.text = context.getString(R.string.follow)
                }
            }
    }


    fun updateData(newList: List<UserModel>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return userList.size
    }
}

