package com.example.instagarmdemo.ui.post

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivityStoryViewBinding
import com.example.instagarmdemo.utility.Keys

class StoryViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_story_view)

        val imageUrl = intent.getStringExtra(Keys.STORY_IMAGE_URL)
    /*    if (imageUrl != null) {
            Toast.makeText(this, "Image URL: $imageUrl", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Image URL is null", Toast.LENGTH_LONG).show()
        }*/
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageUrl).into(binding.storyImageView)


        }
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 4000)
    }
}

