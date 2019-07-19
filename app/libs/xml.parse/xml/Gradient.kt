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
data class Gradient(
    var endColor: String? = null,
    var startColor: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(endColor)
        writeString(startColor)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Gradient> = object : Parcelable.Creator<Gradient> {
            override fun createFromParcel(source: Parcel): Gradient = Gradient(source)
            override fun newArray(size: Int): Array<Gradient?> = arrayOfNulls(size)
        }
    }
}