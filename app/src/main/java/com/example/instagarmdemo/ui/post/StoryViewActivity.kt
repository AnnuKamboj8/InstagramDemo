package com.example.instagarmdemo.ui.post

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivityStoryViewBinding
import com.example.instagarmdemo.utility.Keys
import com.github.marlonlom.utilities.timeago.TimeAgo
import java.lang.Exception

class StoryViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryViewBinding
    private var storyDuration = 4000
    private var seekBarUpdateInterval = 100
    private var currentProgress = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_story_view)
        val imageUrl = intent.getStringExtra(Keys.STORY_IMAGE_URL)
        val profileUrl = intent.getStringExtra(Keys.USER_PROFILE)
        val name = intent.getStringExtra(Keys.USER_NAME)
        val storytime = intent.getStringExtra(Keys.STORY_TIME)

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageUrl).into(binding.storyImageView)
        }
        if (!profileUrl.isNullOrEmpty()) {
            Glide.with(this).load(profileUrl).into(binding.userProfileImageView)
        }

        try {
           // binding.timeDurationTextView.text = storytime?.toLong()?.let { TimeAgo.using(it) }
            val storyTimeInMillis = storytime?.toLongOrNull() ?: 0
            binding.timeDurationTextView.text = getTimeAgo(storyTimeInMillis)

        } catch (e: Exception) {
            binding.timeDurationTextView.text = ""
        }
        binding.usernameTextView.text=name
        binding.seekBar.max = storyDuration
        startSeekBarUpdate()
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, storyDuration.toLong())
    }

    private fun startSeekBarUpdate() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {

                binding.seekBar.progress = currentProgress
                currentProgress += seekBarUpdateInterval

                handler.postDelayed(this, seekBarUpdateInterval.toLong())


                if (currentProgress >= storyDuration) {
                    handler.removeCallbacks(this)
                }
            }
        }, seekBarUpdateInterval.toLong())
    }


    private fun getTimeAgo(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = currentTimeMillis - timestamp
        val timeDifferenceSeconds = timeDifferenceMillis / 1000
        val timeDifferenceMinutes = timeDifferenceSeconds / 60
        val timeDifferenceHours = timeDifferenceMinutes / 60
        val timeDifferenceDays = timeDifferenceHours / 24

        return when {
            timeDifferenceDays > 0 -> "$timeDifferenceDays d"
            timeDifferenceHours > 0 -> "$timeDifferenceHours h"
            timeDifferenceMinutes > 0 -> "$timeDifferenceMinutes m"
            else -> "$timeDifferenceSeconds s"
        }.replace(" ", "")
    }
}

