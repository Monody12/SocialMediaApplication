package com.example.libnetwork

import com.alibaba.fastjson.JSON
import java.lang.reflect.Type

class JsonConvert : Convert<Any>{
    override fun convert(response: String, clazz: Class<Any>): Any? {
        val jsonObject = JSON.parseObject(response)
        val data = jsonObject.getJSONObject("data")
        if (data != null) {
            // 获取双重data的json字符串
            val data1 = data["data"]
            if (data1 != null) {
                // 将双重data的json字符串转换为对象
                return JSON.parseObject(data1.toString(), clazz)
            }
        }
        return null
    }

    override fun convert(response: String, type: Type): Any? {
        val jsonObject = JSON.parseObject(response)
        val data = jsonObject.getJSONObject("data")
        if (data != null) {
            // 获取双重data的json字符串
            val data1 = data["data"]
            if (data1 != null) {
                // 将双重data的json字符串转换为对象
                return JSON.parseObject(data1.toString(), type)
            }
        }
        return null
    }
}