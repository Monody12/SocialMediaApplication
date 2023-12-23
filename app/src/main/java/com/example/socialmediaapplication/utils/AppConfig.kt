package com.example.socialmediaapplication.utils

import android.content.res.AssetManager
import com.alibaba.fastjson.JSON
import com.example.socialmediaapplication.model.BottomBar
import com.example.socialmediaapplication.model.Destination
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AppConfig {
    companion object{
        private var sDestConfig: HashMap<String, Destination>? = null

        private var sBottomBar: BottomBar? = null

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


        fun getBottomBar(): BottomBar? {
            if (sBottomBar == null) {
                val content = parseFile("main_tabs_config.json")
                sBottomBar = JSON.parseObject(content, BottomBar::class.java)
            }
            return sBottomBar
        }

        private fun parseFile(fileName: String): String? {
            val assets = AppGlobals.getApplication()!!.assets
            var `is`: InputStream? = null
            var br: BufferedReader? = null
            val builder = StringBuilder()
            try {
                `is` = assets.open(fileName)
                br = BufferedReader(InputStreamReader(`is`))
                var line: String? = null
                while (br.readLine().also { line = it } != null) {
                    builder.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                    br?.close()
                } catch (e: Exception) {
                }
            }
            return builder.toString()
        }
    }


}