package com.example.instagarmdemo.ui.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.MyPostListItemBinding
import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.Models.Reel

class MyReelRvAdapter(var context: Context, var reelList:List<Reel>):RecyclerView.Adapter<MyReelRvAdapter.ViewHolder>() {
    inner class ViewHolder (var binding:MyPostListItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding=MyPostListItemBinding.inflate( LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(reelList.get(position).reelUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_post) ///show thumbnail of video
            .into(holder.binding.myPostImage)
    /*    val videoUrl = reelList[position].reelUrl
        Log.d("MyReelRvAdapter", "Video URL: $videoUrl")

        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, Uri.parse(videoUrl))
            val bitmap = retriever.frameAtTime
            Glide.with(context)
                .load(bitmap)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.myPostImage)
        } catch (e: Exception) {
            Log.e("MyReelRvAdapter", "Error loading video thumbnail: ${e.message}")
            e.printStackTrace()
        }*/
    }

    override fun getItemCount(): Int {
     return reelList.size
    }
    fun updateData(newreelList: List<Reel>) {
        reelList = newreelList
        notifyDataSetChanged()
    }
}