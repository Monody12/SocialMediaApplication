package com.example.socialmediaapplication.model

data class Destination(
    var asStarter: Boolean,
    var className: String,
    var id: Int,
    var isFragment: Boolean,
    var needLogin: Boolean,
    var pageUrl: String
)