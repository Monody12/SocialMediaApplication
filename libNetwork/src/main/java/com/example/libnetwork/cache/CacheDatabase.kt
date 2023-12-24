package com.example.libnetwork.cache


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.libcommon.AppGlobals

@Database(entities = [Cache::class], version = 1, exportSchema = true)
abstract class CacheDatabase : RoomDatabase() {

    companion object{
        val database: CacheDatabase by lazy {
            // 创建内存数据库
            val cacheDatabase = Room.inMemoryDatabaseBuilder(
                AppGlobals.getApplication()!!.applicationContext,
                CacheDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
            cacheDatabase
        }

        fun get(): CacheDatabase {
            return database
        }
    }

    abstract fun getCache(): CacheDao

}