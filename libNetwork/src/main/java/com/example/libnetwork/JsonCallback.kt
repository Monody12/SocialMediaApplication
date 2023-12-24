package com.example.libnetwork

/**
 * 回调方法
 */
abstract class JsonCallback<T> {
    fun onSuccess(response: ApiResponse<T>) {}
    fun onError(response: ApiResponse<T>) {}
    fun onCacheSuccess(response: ApiResponse<T>) {}
}