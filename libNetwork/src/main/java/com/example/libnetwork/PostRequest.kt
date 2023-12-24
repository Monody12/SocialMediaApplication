package com.example.libnetwork

class PostRequest<T> :Request<T, PostRequest<T>>{

    constructor(url: String) : super(url)
    override fun generateRequest(builder: okhttp3.Request.Builder): okhttp3.Request {
        val bodyBuilder = okhttp3.FormBody.Builder()
        for ((key, value) in params) {
            bodyBuilder.add(key, value.toString())
        }
        return builder.url(mUrl).post(bodyBuilder.build()).build()
    }
}