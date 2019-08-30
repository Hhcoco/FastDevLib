package com.hhcoco.fastdevlib

import android.os.Parcel
import android.os.Parcelable
import org.opencv.core.Point

/**
 * desc:
 * time: 2019/8/12
 * @author wl
 */
data class Point2(var xx: Double, var yy: Double) : Point(xx, yy), Parcelable {
    constructor(source: Parcel) : this(
        source.readDouble(),
        source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeDouble(xx)
        writeDouble(yy)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Point2> = object : Parcelable.Creator<Point2> {
            override fun createFromParcel(source: Parcel): Point2 = Point2(source)
            override fun newArray(size: Int): Array<Point2?> = arrayOfNulls(size)
        }
    }
}