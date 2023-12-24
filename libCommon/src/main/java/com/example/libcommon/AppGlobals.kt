package com.example.libcommon

import android.annotation.SuppressLint
import android.app.Application

class AppGlobals {
    companion object{
        private var sApplication : Application? = null

        @SuppressLint("PrivateApi")
        fun getApplication(): Application? {
            if (sApplication == null){
                try {
                    sApplication = Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null) as Application
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return sApplication
        }

    }
}