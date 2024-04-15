package com.example.instagarmdemo.ui.Models

class UserModel {
    var image :String=""
    var name :String=""
    var email  :String=""
    var password :String=""
    var uid: String = ""
    var isFollow: Boolean = false
    constructor()
    constructor(image: String, name: String, email: String, password: String) {
        this.image = image
        this.name = name
        this.email = email
        this.password = password
    }

    constructor(name: String, email: String, password: String) {
        this.name = name
        this.email = email
        this.password = password
    }

    constructor(email: String, password: String) {
        this.email = email
        this.password = password
    }


}