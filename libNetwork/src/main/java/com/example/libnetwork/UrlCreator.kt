package com.example.libnetwork

class UrlCreator {
    companion object {
        fun createUrlFromParams(url: String, params: Map<String, Any>?): String {
            val builder = StringBuilder()
            builder.append(url)
            if (url.indexOf("?") > 0 || url.indexOf("&") > 0) {
                builder.append("&")
            } else {
                builder.append("?")
            }
            if (params != null) {
                for ((key, value) in params) {
                    val encodeValue = java.net.URLEncoder.encode(value.toString(), "UTF-8")
                    builder.append(key).append("=").append(encodeValue).append("&")
                }
                // 删除最后一个&符号
                builder.deleteCharAt(builder.length - 1)
            }
            return builder.toString()
        }
    }
}