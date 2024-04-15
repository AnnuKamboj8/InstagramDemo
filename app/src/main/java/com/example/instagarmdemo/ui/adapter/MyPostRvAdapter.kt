package com.example.instagarmdemo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagarmdemo.databinding.MyPostListItemBinding
import com.example.instagarmdemo.ui.Models.Post

class MyPostRvAdapter(var context: Context,var postList:List<Post>):RecyclerView.Adapter<MyPostRvAdapter.ViewHolder>() {
    inner class ViewHolder (var binding:MyPostListItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding=MyPostListItemBinding.inflate( LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(postList.get(position).postUrl).into(holder.binding.myPostImage)
    }

    override fun getItemCount(): Int {
     return postList.size
    }

    fun updateData(newPostList: List<Post>) {
        postList = newPostList
        notifyDataSetChanged()
    }
}