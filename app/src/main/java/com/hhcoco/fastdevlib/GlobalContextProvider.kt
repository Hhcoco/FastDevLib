package com.hhcoco.fastdevlib

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.Nullable
import com.lxt.xstreamdemo.BuildConfig

/**
 * desc: 通过ContentProvider提供context.
 * time: 2018/8/13
 * @author wl
 */
class GlobalContextProvider : ContentProvider() {

    private var mContext: Context? = null

    companion object {

        private val TAG = GlobalContextProvider::class.java.simpleName!!

        private val instance by lazy { GlobalContextProvider() }

        /**
         *  获取全局context
         */
        fun globalContext(): Context {
            if (BuildConfig.DEBUG) {
                if (instance.mContext == null) {
                    Log.d(TAG, "context is null.")
                } else {
                    Log.d(TAG, "context is not null.")
                }
            }
            return instance.mContext!!
        }
    }

    override fun onCreate(): Boolean {
        instance.mContext = context.applicationContext
        return true
    }

    @Nullable
    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        return null
    }

    @Nullable
    override fun query(
        uri: Uri?,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    @Nullable
    override fun getType(uri: Uri?): String? {
        return null
    }
}