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
data class Size(
    var width: String? = null,
    var height: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(width)
        writeString(height)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Size> = object : Parcelable.Creator<Size> {
            override fun createFromParcel(source: Parcel): Size = Size(source)
            override fun newArray(size: Int): Array<Size?> = arrayOfNulls(size)
        }
    }
}