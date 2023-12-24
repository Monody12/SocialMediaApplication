package com.example.libnetwork

class GetRequest <T> : Request<T, GetRequest<T>>{
    constructor(url: String) : super(url)

    override fun generateRequest(builder: okhttp3.Request.Builder): okhttp3.Request {
        val url = UrlCreator.createUrlFromParams(mUrl, params)
        return builder.get().url(url).build()
    }
}