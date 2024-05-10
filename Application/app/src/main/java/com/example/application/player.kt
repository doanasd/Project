package com.example.application

import java.io.Serializable

data class player(var id:Int,var name: String, var pass:String, var nickname:String,var img:String, var email:String) :
    Serializable
