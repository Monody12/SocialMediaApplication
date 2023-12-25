package com.example.libnetwork

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

open class ApiService {

    companion object{
        val interceptor = HttpLoggingInterceptor()
        // okHttp初始化
        var okHttpClient : OkHttpClient
        var trustManagers : Array<TrustManager>
        protected var sBaseUrl: String? = null
        var sConvert: Convert<*>? = null

        init {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()
            trustManagers = arrayOf(object :X509TrustManager{
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    TODO("Not yet implemented")
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManagers, java.security.SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        }

        /**
         * 允许传入convert自行解析
         */
        fun init(baseUrl : String,convert: Convert<*>?){
            sBaseUrl = baseUrl
            if (convert == null){
                sConvert = JsonConvert()
            }
            sConvert = convert
        }

         fun <T> `get`(url: String): GetRequest<T> {
            return GetRequest(sBaseUrl + url)
        }

        fun <T> post(url: String): PostRequest<T> {
            return PostRequest(sBaseUrl + url)
        }
    }


}