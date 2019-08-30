package com.hhcoco.fastdevlib

import android.os.Environment
import android.util.DisplayMetrics
import java.io.File

/**
 * desc: 扩展
 * time: 2019/8/23
 * @author wl
 */

val globalContext by lazy {  GlobalContextProvider.globalContext() }
val displayMetrics: DisplayMetrics by lazy { globalContext.resources.displayMetrics }
val kWidth by lazy { displayMetrics.widthPixels }
val kHeight by lazy { displayMetrics.heightPixels }
val density by lazy { displayMetrics.density }
val statusBarHeight by lazy {
    var default = density * 25
    runCatching {
        val id = globalContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        default = globalContext.resources.getDimensionPixelSize(id).toFloat()
    }
    default
}

val DATA_PATH by lazy { Environment.getExternalStorageDirectory().absolutePath + File.separator + "ble" + File.separator; }
