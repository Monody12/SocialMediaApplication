package com.example.libnetwork

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.arch.core.executor.ArchTaskExecutor
import com.example.libnetwork.cache.CacheManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class Request<T, R : Request<T, R>> : Cloneable {

    protected var mUrl: String
    private var headers: HashMap<String, String> = HashMap()
    protected var params: HashMap<String, Any> = HashMap()
    private var cacheKey: String? = null
    private var mType: Type? = null

    //private Class mClaz;
    private var mCacheStrategy: Int = NET_ONLY

    constructor(url: String) {
        mUrl = url
    }

    companion object {
        // 仅仅只访问本地缓存，即使本地不存在，也不会发起网络请求
        const val CACHE_ONLY = 1

        // 先访问缓存，同时发起网络的请求，成功后缓存到本地
        const val CACHE_FIRST = 2

        // 仅仅只访问服务器，不做任何存储
        const val NET_ONLY = 3

        // 先访问网络，成功后缓存到本地
        const val NET_CACHE = 4
    }

    @androidx.annotation.IntDef(
        CACHE_ONLY,
        CACHE_FIRST,
        NET_CACHE,
        NET_ONLY
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class CacheStrategy()

    fun addHeader(key: String, value: String): R {
        headers[key] = value
        return this as R
    }

    fun addParam(key: String, value: Any): R {
        try {
            if (value is String) {
                params[key] = value
            } else if (value.javaClass == Int::class.javaPrimitiveType) {
                params[key] = value
            } else if (value.javaClass == Long::class.javaPrimitiveType) {
                params[key] = value
            } else if (value.javaClass == Float::class.javaPrimitiveType) {
                params[key] = value
            } else if (value.javaClass == Double::class.javaPrimitiveType) {
                params[key] = value
            } else if (value.javaClass == Char::class.javaPrimitiveType) {
                params[key] = value
            } else if (value.javaClass == Boolean::class.javaPrimitiveType) {
                params[key] = value
            } else if (value.javaClass == Short::class.javaPrimitiveType) {
                params[key] = value
            } else if (value.javaClass == Byte::class.javaPrimitiveType) {
                params[key] = value
            } else {
                params[key] = value.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this as R
    }

    private fun addHeaders(builder: okhttp3.Request.Builder) {
        for ((key, value) in headers) {
            builder.addHeader(key, value)
        }
    }

    /**
     * 设置缓存策略
     */
    fun cacheStrategy(@CacheStrategy cacheStrategy: Int): R {
        mCacheStrategy = cacheStrategy
        return this as R
    }

    fun cacheKey(key: String): R {
        this.cacheKey = key
        return this as R
    }

    /**
     * 异步请求，含有callback
     */
    @SuppressLint("RestrictedApi")
    fun execute(callback: JsonCallback<T>) {
        if (mCacheStrategy != NET_ONLY) {
            // 读取缓存
            ArchTaskExecutor.getIOThreadExecutor().execute {
                Runnable {
                    val response : ApiResponse<T> = readCache()
                    if (response != null) {
                        callback.onCacheSuccess(response)
                    }
                }
            }
        }
        getCall().enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val response = ApiResponse<T>()
                response.message = e.message
                callback.onError(response)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val response = parseResponse(response, callback)
                // 响应成果，但还要判断业务层是否成功
                if (response.success) {
                    callback.onSuccess(response)
                } else {
                    callback.onError(response)
                }
            }
        })
    }

    private fun readCache(): ApiResponse<T> {
        val key: String = if (TextUtils.isEmpty(cacheKey)) generateCacheKey() else cacheKey!!
        val cache = CacheManager.getCache(key)
        val result = ApiResponse<T>()
        result.status = 304
        result.message = "缓存获取成功"
        result.success = true
        result.body = cache as T?
        return result
    }

    /**
     * 同步请求，没有callback
     * 无法获取泛型类型，在创建Request时显式指定泛型类型，在编译期也会被擦除
     */
    fun execute(): ApiResponse<T> {
        // 读取缓存
        if (mCacheStrategy == CACHE_ONLY) {
            val response = readCache()
            if (response.success) {
                return response
            }
        }
        val call = getCall()
        val response = call.execute()
        return parseResponse(response, null)
    }

    /**
     * 手动设置返回类型 Type
     */
    fun responseType(type: Type): R {
        mType = type
        return this as R
    }

    /**
     * 手动设置返回类型 Class
     */
    fun responseType(claz: Class<T>): R {
        mType = claz
        return this as R
    }

    private fun parseResponse(response: Response, callback: JsonCallback<T>?): ApiResponse<T> {
        var message: String? = null
        var status = response.code
        var success = response.isSuccessful
        val result: ApiResponse<T> = ApiResponse()
        val convert = ApiService.sConvert
        try {
            val content = response.body?.string()
            if (success) {
                if (callback != null) {
                    // 获取真正的实际类型
                    val type = callback.javaClass.genericSuperclass as ParameterizedType
                    val argument: Type = type.actualTypeArguments[0]
                    result.body = convert?.convert(content!!, argument) as T?
                } else if (mType != null) {
                    result.body = convert?.convert(content!!, mType!!) as T?
                } else {
                    android.util.Log.e("Request", "parseResponse: 无法解析")
                }
            } else {
                message = content
            }
        } catch (e: Exception) {
            message = e.message
            success = false
        }
        result.success = success
        result.status = status
        result.message = message
        // 缓存
        val isCache = (mCacheStrategy == CACHE_ONLY || mCacheStrategy == CACHE_FIRST)
                && result.body is Serializable
        if (isCache) {
            saveCache(result.body as T)
        }
        return result
    }

    private fun saveCache(t: T) {
        val key: String = if (TextUtils.isEmpty(cacheKey)) generateCacheKey() else cacheKey!!
        CacheManager.save(key, t)
    }

    private fun generateCacheKey(): String {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params)
        return cacheKey!!
    }


    private fun getCall(): Call {
        val builder = okhttp3.Request.Builder()
        addHeaders(builder)
        val request: okhttp3.Request = generateRequest(builder)
        return ApiService.okHttpClient.newCall(request)
    }

    abstract fun generateRequest(builder: okhttp3.Request.Builder): okhttp3.Request

}