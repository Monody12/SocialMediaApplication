package com.example.libnetwork.cache


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * 缓存数据库操作类
 * 写成抽象类或接口都行，是因为Room会在编译时，自动生成具体的实现类
 */
@Dao
interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun save(cache: Cache) : Long

    @Query("select * from cache where `key`=:key")
    abstract fun getCache(key: String) : Cache?

    @Delete
    abstract fun delete(cache: Cache) : Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(cache: Cache) : Int

}