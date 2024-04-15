package com.example.instagarmdemo.ui.Models

class Reel {
    var reelUrl:String=""
    var caption:String = ""
    var uid: String = ""
    var name:String=""
    var profileLink:String ?= null
    constructor()
    constructor(reelUrl: String, caption: String) {
        this.reelUrl = reelUrl
        this.caption = caption
    }

    constructor(reelUrl: String, caption:
    String, profileLink: String,name:String,uid:String) {
        this.reelUrl = reelUrl
        this.caption = caption
        this.name = name
        this.profileLink = profileLink
        this.uid = uid
    }

}