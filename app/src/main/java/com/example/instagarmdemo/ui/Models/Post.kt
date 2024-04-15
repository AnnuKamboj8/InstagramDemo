package com.example.instagarmdemo.ui.Models

class Post {
    var postUrl: String = ""
    var postId: String = ""
    var caption: String = ""
    var uid: String = ""
    var time: String = ""
    var isLiked: Boolean = false
    var likeCount :  Int = 0
    var isLikedImage : Boolean = false

    constructor()
    constructor(postUrl: String, caption: String) {
        this.postUrl = postUrl
        this.caption = caption
    }

    constructor(postUrl: String, caption: String, uid: String, time: String)
    {
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid.toString()
        this.time = time

    }

   /* override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Post
        return postId == other.postId
    }

    override fun hashCode(): Int {
        return postId.hashCode()
    }*/


}