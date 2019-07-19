package com.hhcoco.fastdevlib

import android.graphics.Paint

/**
 * desc: 各种扩展集合.
 * time: 2019/7/18
 * @author wl
 */

/**
 *  计算文字缩放比例
 *  @param reality 真实距离
 *  @param minZoomLevel 最小缩放比例
 *  @param defaultTxtSize 默认的文字大小, 单位px.
 *  @return 返回缩放比例
 */
private val paint by lazy { Paint() }

fun Array<String>.calculateZoomLevel(reality: Int, defaultTxtSize: Array<Float>, minZoomLevel: Float = 0.5f): Float {
    if (this.size != defaultTxtSize.size) return 1f
    var widthCount = 0f
    this.withIndex().forEach {
        paint.reset()
        paint.textSize = defaultTxtSize[it.index]
        widthCount += paint.measureText(it.value)
    }
    return if (reality >= widthCount) {
        1f
    } else {
        val multiple = reality / widthCount
        Math.max(multiple, minZoomLevel)
    }
}