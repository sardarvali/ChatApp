package com.example.chatapp

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var dob: String? = null

    constructor() {}

    constructor(name: String?, dob: String?, email: String?, uid: String?) {
        this.name = name
        this.dob = dob
        this.email = email
        this.uid = uid

    }
}