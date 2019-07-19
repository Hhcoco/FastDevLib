package com.lxt.core.skin.xml

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

/**
 * desc:
 * time: 2019/4/19
 * @author wl
 */
@Keep
data class ShapeP(
    var shape: Shape? = null
)

@Keep
data class Shape(
    var corners: Corners? = null,
    var solid: Solid? = null,
    var gradient: Gradient? = null,
    var stroke: Stroke? = null,
    var size: Size? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readParcelable<Corners>(Corners::class.java.classLoader),
        source.readParcelable<Solid>(Solid::class.java.classLoader),
        source.readParcelable<Gradient>(Gradient::class.java.classLoader),
        source.readParcelable<Stroke>(Stroke::class.java.classLoader),
        source.readParcelable<Size>(Size::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(corners, 0)
        writeParcelable(solid, 0)
        writeParcelable(gradient, 0)
        writeParcelable(stroke, 0)
        writeParcelable(size, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Shape> = object : Parcelable.Creator<Shape> {
            override fun createFromParcel(source: Parcel): Shape = Shape(source)
            override fun newArray(size: Int): Array<Shape?> = arrayOfNulls(size)
        }
    }
}