package com.example.instagarmdemo.ui.Models

class Story {
    var storyUrl: String = ""
    var storyId: String = ""
    var uid: String = ""
    var time: String = ""


    constructor()
    constructor(storyUrl: String, storyId: String, uid: String, time: String) {
        this.storyUrl = storyUrl
        this.storyId = storyId
        this.uid = uid
        this.time = time
    }

}
