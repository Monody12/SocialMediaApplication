package com.example.libnavannotation

@Target(AnnotationTarget.CLASS)
annotation class FragmentDestination (
    val pageUrl: String,
    val needLogin: Boolean = false,
    val asStarter: Boolean = false
)