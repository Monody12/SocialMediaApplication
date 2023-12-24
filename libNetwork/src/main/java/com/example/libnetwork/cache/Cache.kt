package com.example.libnetwork.cache

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "cache", primaryKeys = ["key"])
class Cache : Serializable {
    var key: String = ""
    var data: ByteArray? = null
}