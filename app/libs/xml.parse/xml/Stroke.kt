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
data class Stroke(
    var color: String? = null,
    var width: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(color)
        writeString(width)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Stroke> = object : Parcelable.Creator<Stroke> {
            override fun createFromParcel(source: Parcel): Stroke = Stroke(source)
            override fun newArray(size: Int): Array<Stroke?> = arrayOfNulls(size)
        }
    }
}