package com.example.instagarmdemo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.HomeReelItemBinding
import com.example.instagarmdemo.ui.Models.Reel
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class ReelRvAdapter(var context: Context,var reelList:ArrayList<Reel>):RecyclerView.Adapter<ReelRvAdapter.ViewHolder>(){

    inner class ViewHolder(var binding:HomeReelItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      var binding= HomeReelItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

      /*  try {
            Firebase.firestore.collection(Keys.USER_NODE).document(reelList[position].uid)
                .get().addOnSuccessListener {
                    val user = it.toObject<UserModel>()
                    if (!isActivityDestroyed(holder.binding.root.context as AppCompatActivity)) {
                        Glide.with(context)
                            .load(reelList.get(position).profileLink)
                            .placeholder(R.drawable.profile)
                            .into(holder.binding.reelProfileImage)
                    }
                    holder.binding.reelNameTextView.text = user?.name
                }
        } catch (e: Exception) {

        }*/

       Glide.with(context).load(reelList.get(position).profileLink).placeholder(R.drawable.profile).into(holder.binding.reelProfileImage)
        holder.binding.reelTextView.setText(reelList.get(position).caption)
        holder.binding.reelNameTextView.setText(reelList.get(position).name)
        holder.binding.videoView.setVideoPath(reelList.get(position).reelUrl)
        holder.binding.videoView.setOnPreparedListener{
            holder.binding.reelProgressBar.visibility = View.GONE
            holder.binding.videoView.start()
        }
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

  /*  private fun isActivityDestroyed(activity: AppCompatActivity): Boolean {
        return activity.isDestroyed || activity.isFinishing
    }*/

}