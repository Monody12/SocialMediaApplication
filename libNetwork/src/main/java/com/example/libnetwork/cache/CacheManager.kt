package com.example.libnetwork.cache

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class CacheManager {
    companion object {
        fun <T> save(key: String, body: T) {
            val cache = Cache()
            cache.key = key
            cache.data = toByteArray(body)

            CacheDatabase.get().getCache().save(cache)
        }

        /**
         * 读取缓存
         */
        fun getCache(key: String): Any? {
            val cache = CacheDatabase.get().getCache().getCache(key)
            if (cache?.data != null) {
                return toObject(cache.data!!)
            }
            return null
        }

        /**
         * 将字节数组转换为对象
         */
        private fun toObject(data: ByteArray): Any? {
            ByteArrayInputStream(data).use { byteArrayInputStream ->
                ObjectInputStream(byteArrayInputStream).use { objectInputStream ->
                    try {
                        return objectInputStream.readObject()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return null
        }

        /**
         * 将对象转换为字节数组
         */
        private fun <T> toByteArray(body: T): ByteArray? {
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
                    try {
                        objectOutputStream.writeObject(body)
                        objectOutputStream.flush()
                        return byteArrayOutputStream.toByteArray()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return null
        }


//        private fun <T> toByteArray(body: T): ByteArray? {
//            var byteArrayOutputStream : ByteArrayOutputStream? = null
//            var objectOutputStream : ObjectOutputStream? = null
//            try {
//                byteArrayOutputStream = ByteArrayOutputStream()
//                objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
//                objectOutputStream.writeObject(body)
//                objectOutputStream.flush()
//                return byteArrayOutputStream.toByteArray()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                try {
//                    byteArrayOutputStream?.close()
//                    objectOutputStream?.close()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//            return null
//        }
    }
}