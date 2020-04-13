package com.example.imagebtapp_v001b001

import android.util.Log


object Logger {
    private val DEBUG_ON = BuildConfig.DEBUG_ON
    private val VERBOSE_TAG = BuildConfig.VERBOSE_TAG
    fun d(tag: String, message: String) {
        if (DEBUG_ON)Log.d(getLogTag(tag), message)
    }

    private fun getLogTag(tag: String) : String {
        return if (!VERBOSE_TAG) tag
        else Throwable().fillInStackTrace().stackTrace.let {
                StringBuffer(tag).append(".(${it[2].fileName}:${it[2].lineNumber}).${it[2].methodName}").toString()
            }
    }
}