package com.example.instagarmdemo.utility

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("global_pref", Context.MODE_PRIVATE)


    fun saveLikeState(postId: String, userId: String, isLiked: Boolean) {
        val key = generateLikeKey(postId, userId)
        sharedPreferences?.edit()?.putBoolean(key, isLiked)?.apply()
    }

    fun loadLikeState(postId: String, userId: String): Boolean {
        val key = generateLikeKey(postId, userId)
        return sharedPreferences?.getBoolean(key, false) == true
    }


    private fun generateLikeKey(postId: String, userId: String): String {
        return "like_state_${postId}_$userId"
    }
}
