package com.hhcoco.fastdevlib

import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson用工具类<BR></BR>
 * Created by fanhl on 2014/9/24.
 */
object GsonUtil {
    private val TAG = GsonUtil::class.java.simpleName

    private val gson: Gson by lazy {

        GsonBuilder().setExclusionStrategies(object : ExclusionStrategy {
            /**
             * 设置要过滤的属性
             */
            override fun shouldSkipField(attr: FieldAttributes): Boolean {
                return false
            }

            /**
             * 设置要过滤的类
             */
            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                // 这里，如果返回true就表示此类要过滤，否则就输出
                return false
            }
        }).create()
    }

    fun json(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> obj(json: String, classOfT: Class<T>): T? {
        try {
            return gson.fromJson(json, classOfT)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "解析json失败", e)
        }

        return null
    }

    fun <T> obj(json: String, typeOfT: Type): T? {
        try {
            return gson.fromJson<T>(json, typeOfT)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "解析json失败", e)
        }

        return null
    }

    fun <T> obj(jsonElement: JsonElement, classOfT: Class<T>): T? {
        try {
            return gson.fromJson(jsonElement, classOfT)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "解析json失败", e)
        }

        return null
    }

    fun <T> obj(jsonElement: JsonElement, typeOfT: Type): T? {
        try {
            return gson.fromJson<T>(jsonElement, typeOfT)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "解析json失败", e)
        }

        return null
    }
}
