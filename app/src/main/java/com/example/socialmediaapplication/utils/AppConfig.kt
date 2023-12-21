package com.example.socialmediaapplication.utils

import android.content.res.AssetManager
import com.example.socialmediaapplication.model.Destination
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppConfig {
    companion object{
        private var sDestConfig: HashMap<String, Destination>? = null

        fun getDestConfig(): HashMap<String, Destination> {
            if (sDestConfig == null){
                parseDestConfig()
            }
            return sDestConfig!!
        }

        /**
         * 解析destination.json文件
         */
        private fun parseDestConfig(){
            val assets : AssetManager = AppGlobals.getApplication()!!.assets
            val inputStream = assets.open("destination.json")
            val content = inputStream.bufferedReader().readText()
            sDestConfig = Gson().fromJson(content, object : TypeToken<HashMap<String, Destination>>(){}.type)
        }
    }
}